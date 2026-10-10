package com.ampera.application

import com.ampera.domain.CalculadoraDeCusto
import com.ampera.domain.ConsumoDoPeriodo
import com.ampera.domain.PrevisorDeConsumo
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.GerarRelatorioRequest
import com.ampera.dto.RelatorioRequest
import com.ampera.dto.RelatorioResponse
import com.ampera.infrastructure.persistence.MedicaoRepository
import com.ampera.infrastructure.persistence.Relatorio
import com.ampera.infrastructure.persistence.RelatorioRepository
import com.ampera.infrastructure.persistence.TarifaRepository
import com.ampera.mapper.RelatorioMapper
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Service
class RelatorioService(
    private val relatorios: RelatorioRepository,
    private val medicoes: MedicaoRepository,
    private val tarifas: TarifaRepository,
    private val mapper: RelatorioMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
    private val previsor: PrevisorDeConsumo = PrevisorDeConsumo(),
    private val consumo: ConsumoDoPeriodo = ConsumoDoPeriodo(),
    private val calculadora: CalculadoraDeCusto = CalculadoraDeCusto(),
) {
    suspend fun listar(residenciaId: Long?, busca: String? = null): List<RelatorioResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (residenciaId != null) donos.residencia(residenciaId, usuarioId)
            val lista = if (busca.isNullOrBlank()) {
                relatorios.findByResidenciaUsuarioId(usuarioId)
            } else {
                relatorios.buscarPorTitulo(usuarioId, busca.trim())
            }
            lista.filter { residenciaId == null || it.residencia.id == residenciaId }.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): RelatorioResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: RelatorioRequest): RelatorioResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido.inicio, pedido.fim)
            val residencia = donos.residencia(pedido.residenciaId, usuarioId)
            mapper.toResponse(relatorios.save(mapper.toEntity(pedido, residencia)))
        }
    }

    suspend fun atualizar(id: Long, pedido: RelatorioRequest): RelatorioResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido.inicio, pedido.fim)
            val relatorio = carregar(id, usuarioId)
            mapper.atualizar(relatorio, pedido, donos.residencia(pedido.residenciaId, usuarioId))
            mapper.toResponse(relatorio)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            relatorios.deleteById(id)
        }
    }

    suspend fun gerar(pedido: GerarRelatorioRequest): RelatorioResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido.inicio, pedido.fim)
            val residencia = donos.residencia(pedido.residenciaId, usuarioId)
            val leituras = medicoes.doPeriodo(residencia.id, inicioDoDia(pedido.inicio), inicioDoDia(pedido.fim.plusDays(1)))
            val diario = consumo.diario(leituras.map { it.paraLeituraEnergia() })
            val total = diario.sum()
            val tarifa = tarifas.vigentes(residencia.id, pedido.fim).firstOrNull()?.valorKwh ?: BigDecimal.ONE
            val salvo = relatorios.save(
                Relatorio(
                    residencia = residencia,
                    titulo = pedido.titulo.trim().ifBlank { "Relatório gerado" },
                    inicio = pedido.inicio,
                    fim = pedido.fim,
                    consumoKwh = total,
                    custoTotal = calculadora.calcular(total, tarifa),
                    previsaoKwh = previsor.preverProximo(diario),
                    criadoEm = Instant.now(),
                ),
            )
            mapper.toResponse(salvo)
        }
    }

    private fun validar(inicio: LocalDate, fim: LocalDate) {
        if (fim.isBefore(inicio)) throw RegraDeNegocioException("Fim do relatório anterior ao início")
    }

    private fun carregar(id: Long, usuarioId: Long): Relatorio {
        val relatorio = relatorios.findById(id).orElseThrow { RecursoNaoEncontradoException("Relatório não encontrado") }
        donos.residencia(relatorio.residencia.id, usuarioId)
        return relatorio
    }
}
