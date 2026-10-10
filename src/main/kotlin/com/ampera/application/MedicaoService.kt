package com.ampera.application

import com.ampera.domain.AvaliadorDeAlerta
import com.ampera.domain.CalculadoraDeCusto
import com.ampera.domain.LeituraDoMedidor
import com.ampera.domain.LinhaDeBaseEnergia
import com.ampera.domain.OrigemMedicao
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.dto.MedicaoRequest
import com.ampera.dto.MedicaoResponse
import com.ampera.infrastructure.persistence.AlertaRepository
import com.ampera.infrastructure.persistence.Dispositivo
import com.ampera.infrastructure.persistence.Medicao
import com.ampera.infrastructure.persistence.MedicaoRepository
import com.ampera.infrastructure.persistence.TarifaRepository
import com.ampera.mapper.MedicaoMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class MedicaoService(
    private val medicoes: MedicaoRepository,
    private val tarifas: TarifaRepository,
    private val alertas: AlertaRepository,
    private val mapper: MedicaoMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
    private val calculadora: CalculadoraDeCusto = CalculadoraDeCusto(),
    private val avaliador: AvaliadorDeAlerta = AvaliadorDeAlerta(),
    private val linhaDeBase: LinhaDeBaseEnergia = LinhaDeBaseEnergia(),
) {
    suspend fun recentes(dispositivoId: Long?, limite: Int, busca: String? = null): Flow<MedicaoResponse> {
        val usuarioId = sessao.id()
        val itens = io { transacao.ler { listar(usuarioId, dispositivoId, limite, busca) } }
        return flow { itens.forEach { emit(it) } }
    }

    suspend fun buscar(id: Long): MedicaoResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: MedicaoRequest): MedicaoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val dispositivo = donos.dispositivo(pedido.dispositivoId, usuarioId)
            mapper.toResponse(gravar(mapper.toEntity(pedido), dispositivo, pedido.leitura(dispositivo), OrigemMedicao.MANUAL))
        }
    }

    suspend fun atualizar(id: Long, pedido: MedicaoRequest): MedicaoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val medicao = carregar(id, usuarioId)
            val dispositivo = donos.dispositivo(pedido.dispositivoId, usuarioId)
            mapper.atualizar(medicao, pedido)
            mapper.toResponse(gravar(medicao, dispositivo, pedido.leitura(dispositivo), OrigemMedicao.MANUAL))
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            medicoes.deleteById(id)
        }
    }

    private fun listar(usuarioId: Long, dispositivoId: Long?, limite: Int, busca: String?): List<MedicaoResponse> {
        val pagina = PageRequest.of(0, limite.coerceIn(1, 200))
        if (dispositivoId != null) donos.dispositivo(dispositivoId, usuarioId)
        val lista = when {
            !busca.isNullOrBlank() -> medicoes.buscarPorObservacao(usuarioId, busca.trim(), pagina)
                .filter { dispositivoId == null || it.dispositivo.id == dispositivoId }
            dispositivoId == null -> medicoes.findByDispositivoComodoResidenciaUsuarioIdOrderByInstanteDesc(usuarioId, pagina)
            else -> medicoes.findByDispositivoIdOrderByInstanteDesc(dispositivoId, pagina)
        }
        return lista.map { mapper.toResponse(it) }
    }

    private fun MedicaoRequest.leitura(dispositivo: Dispositivo) =
        LeituraDoMedidor(dispositivo.codigoMqtt, tensao, corrente, potencia, energiaKwh)

    private fun gravar(medicao: Medicao, dispositivo: Dispositivo, leitura: LeituraDoMedidor, origem: OrigemMedicao): Medicao {
        val anterior = medicoes.findTopByDispositivoIdOrderByInstanteDesc(dispositivo.id)
        if (anterior?.id != medicao.id && linhaDeBase.reiniciou(anterior?.energiaKwh, leitura.energiaKwh)) {
            log.info("Contador de energia reiniciou no dispositivo {}", dispositivo.codigoMqtt)
        }
        val tarifa = tarifas.vigentes(dispositivo.comodo.residencia.id, LocalDate.now(ZoneOffset.UTC)).firstOrNull()
        medicao.dispositivo = dispositivo
        medicao.tensao = leitura.tensao
        medicao.corrente = leitura.corrente
        medicao.potencia = leitura.potencia
        medicao.energiaKwh = leitura.energiaKwh
        medicao.custo = calculadora.calcular(leitura.energiaKwh, tarifa?.valorKwh ?: BigDecimal.ONE)
        medicao.origem = origem
        val salva = medicoes.save(medicao)
        alertas.findByDispositivoIdAndAtivoTrue(dispositivo.id).forEach { alerta ->
            alerta.disparado = avaliador.disparou(alerta.grandeza, alerta.limiar, salva.tensao, salva.corrente, salva.potencia)
        }
        return salva
    }

    private fun carregar(id: Long, usuarioId: Long): Medicao {
        val medicao = medicoes.findById(id).orElseThrow { RecursoNaoEncontradoException("Medição não encontrada") }
        donos.dispositivo(medicao.dispositivo.id, usuarioId)
        return medicao
    }

    private companion object {
        val log = LoggerFactory.getLogger(MedicaoService::class.java)
    }
}
