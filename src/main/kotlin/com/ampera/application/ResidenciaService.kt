package com.ampera.application

import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.dto.ResidenciaRequest
import com.ampera.dto.ResidenciaResponse
import com.ampera.infrastructure.persistence.ResidenciaRepository
import com.ampera.infrastructure.persistence.UsuarioRepository
import com.ampera.mapper.ResidenciaMapper
import org.springframework.stereotype.Service

@Service
class ResidenciaService(
    private val residencias: ResidenciaRepository,
    private val usuarios: UsuarioRepository,
    private val mapper: ResidenciaMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(busca: String? = null): List<ResidenciaResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            val lista = if (busca.isNullOrBlank()) {
                residencias.findByUsuarioId(usuarioId)
            } else {
                residencias.buscarPorNome(usuarioId, busca.trim())
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): ResidenciaResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(donos.residencia(id, usuarioId)) }
    }

    suspend fun criar(pedido: ResidenciaRequest): ResidenciaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val usuario = usuarios.findById(usuarioId).orElseThrow { RecursoNaoEncontradoException("Usuário não encontrado") }
            mapper.toResponse(residencias.save(mapper.toEntity(pedido, usuario)))
        }
    }

    suspend fun atualizar(id: Long, pedido: ResidenciaRequest): ResidenciaResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val residencia = donos.residencia(id, usuarioId)
            mapper.atualizar(residencia, pedido)
            mapper.toResponse(residencia)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            donos.residencia(id, usuarioId)
            residencias.deleteById(id)
        }
    }
}
