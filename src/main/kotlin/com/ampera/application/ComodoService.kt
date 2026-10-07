package com.ampera.application

import com.ampera.dto.ComodoRequest
import com.ampera.dto.ComodoResponse
import com.ampera.infrastructure.persistence.ComodoRepository
import com.ampera.mapper.ComodoMapper
import org.springframework.stereotype.Service

@Service
class ComodoService(
    private val comodos: ComodoRepository,
    private val mapper: ComodoMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(residenciaId: Long?, busca: String? = null): List<ComodoResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (residenciaId != null) donos.residencia(residenciaId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> comodos.buscarPorNome(usuarioId, busca.trim())
                    .filter { residenciaId == null || it.residencia.id == residenciaId }
                residenciaId == null -> comodos.findByResidenciaUsuarioId(usuarioId)
                else -> comodos.findByResidenciaIdAndResidenciaUsuarioId(residenciaId, usuarioId)
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): ComodoResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(donos.comodo(id, usuarioId)) }
    }

    suspend fun criar(pedido: ComodoRequest): ComodoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val residencia = donos.residencia(pedido.residenciaId, usuarioId)
            mapper.toResponse(comodos.save(mapper.toEntity(pedido, residencia)))
        }
    }

    suspend fun atualizar(id: Long, pedido: ComodoRequest): ComodoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val comodo = donos.comodo(id, usuarioId)
            mapper.atualizar(comodo, pedido, donos.residencia(pedido.residenciaId, usuarioId))
            mapper.toResponse(comodo)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            donos.comodo(id, usuarioId)
            comodos.deleteById(id)
        }
    }
}
