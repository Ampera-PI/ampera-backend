package com.ampera.application

import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.dto.AlertaRequest
import com.ampera.dto.AlertaResponse
import com.ampera.infrastructure.persistence.Alerta
import com.ampera.infrastructure.persistence.AlertaRepository
import com.ampera.mapper.AlertaMapper
import org.springframework.stereotype.Service

@Service
class AlertaService(
    private val alertas: AlertaRepository,
    private val mapper: AlertaMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(dispositivoId: Long?, busca: String? = null): List<AlertaResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (dispositivoId != null) donos.dispositivo(dispositivoId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> alertas.buscarPorMensagem(usuarioId, busca.trim())
                    .filter { dispositivoId == null || it.dispositivo.id == dispositivoId }
                dispositivoId == null -> alertas.findByDispositivoComodoResidenciaUsuarioId(usuarioId)
                else -> alertas.findByDispositivoIdAndDispositivoComodoResidenciaUsuarioId(dispositivoId, usuarioId)
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): AlertaResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: AlertaRequest): AlertaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val dispositivo = donos.dispositivo(pedido.dispositivoId, usuarioId)
            mapper.toResponse(alertas.save(mapper.toEntity(pedido, dispositivo)))
        }
    }

    suspend fun atualizar(id: Long, pedido: AlertaRequest): AlertaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val alerta = carregar(id, usuarioId)
            mapper.atualizar(alerta, pedido, donos.dispositivo(pedido.dispositivoId, usuarioId))
            mapper.toResponse(alerta)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            alertas.deleteById(id)
        }
    }

    private fun carregar(id: Long, usuarioId: Long): Alerta {
        val alerta = alertas.findById(id).orElseThrow { RecursoNaoEncontradoException("Alerta não encontrado") }
        donos.dispositivo(alerta.dispositivo.id, usuarioId)
        return alerta
    }
}
