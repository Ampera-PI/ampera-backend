package com.ampera.application

import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.TarifaRequest
import com.ampera.dto.TarifaResponse
import com.ampera.infrastructure.persistence.Tarifa
import com.ampera.infrastructure.persistence.TarifaRepository
import com.ampera.mapper.TarifaMapper
import org.springframework.stereotype.Service

@Service
class TarifaService(
    private val tarifas: TarifaRepository,
    private val mapper: TarifaMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(residenciaId: Long?, busca: String? = null): List<TarifaResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (residenciaId != null) donos.residencia(residenciaId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> tarifas.buscarPorDistribuidora(usuarioId, busca.trim())
                    .filter { residenciaId == null || it.residencia.id == residenciaId }
                residenciaId == null -> tarifas.findByResidenciaUsuarioId(usuarioId)
                else -> tarifas.findByResidenciaIdAndResidenciaUsuarioId(residenciaId, usuarioId)
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): TarifaResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: TarifaRequest): TarifaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido)
            val residencia = donos.residencia(pedido.residenciaId, usuarioId)
            mapper.toResponse(tarifas.save(mapper.toEntity(pedido, residencia)))
        }
    }

    suspend fun atualizar(id: Long, pedido: TarifaRequest): TarifaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido)
            val tarifa = carregar(id, usuarioId)
            mapper.atualizar(tarifa, pedido, donos.residencia(pedido.residenciaId, usuarioId))
            mapper.toResponse(tarifa)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            tarifas.deleteById(id)
        }
    }

    private fun carregar(id: Long, usuarioId: Long): Tarifa {
        val tarifa = tarifas.findById(id).orElseThrow { RecursoNaoEncontradoException("Tarifa não encontrada") }
        donos.residencia(tarifa.residencia.id, usuarioId)
        return tarifa
    }

    private fun validar(pedido: TarifaRequest) {
        if (pedido.vigenciaFim != null && pedido.vigenciaFim.isBefore(pedido.vigenciaInicio)) {
            throw RegraDeNegocioException("Vigência final anterior à inicial")
        }
    }
}
