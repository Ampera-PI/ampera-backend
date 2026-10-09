package com.ampera.application

import com.ampera.domain.AcessoNegadoException
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.UsuarioRequest
import com.ampera.dto.UsuarioResponse
import com.ampera.infrastructure.persistence.UsuarioRepository
import com.ampera.mapper.UsuarioMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarios: UsuarioRepository,
    private val mapper: UsuarioMapper,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
    private val senhas: PasswordEncoder,
) {
    suspend fun listar(busca: String? = null): List<UsuarioResponse> = io {
        transacao.ler {
            val lista = if (busca.isNullOrBlank()) usuarios.findAll() else usuarios.buscarPorNome(busca.trim())
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): UsuarioResponse = io {
        transacao.ler { mapper.toResponse(carregar(id)) }
    }

    suspend fun criar(pedido: UsuarioRequest): UsuarioResponse = io {
        val senha = pedido.senha?.takeIf { it.length >= 6 }
            ?: throw RegraDeNegocioException("Senha deve ter ao menos 6 caracteres")
        transacao.escrever {
            if (usuarios.findByEmail(pedido.email.trim()) != null) throw RegraDeNegocioException("E-mail já cadastrado")
            mapper.toResponse(usuarios.save(mapper.toEntity(pedido, senhas.encode(senha))))
        }
    }

    suspend fun atualizar(id: Long, pedido: UsuarioRequest): UsuarioResponse = io {
        if (sessao.id() != id) throw AcessoNegadoException("Só é possível alterar o próprio usuário")
        transacao.escrever {
            val usuario = carregar(id)
            usuarios.findByEmail(pedido.email.trim())?.let { if (it.id != id) throw RegraDeNegocioException("E-mail já cadastrado") }
            mapper.atualizar(usuario, pedido)
            pedido.senha?.takeIf { it.isNotBlank() }?.let { usuario.senhaHash = senhas.encode(it) }
            mapper.toResponse(usuario)
        }
    }

    suspend fun excluir(id: Long) = io {
        if (sessao.id() != id) throw AcessoNegadoException("Só é possível excluir o próprio usuário")
        transacao.escrever {
            carregar(id)
            usuarios.deleteById(id)
        }
    }

    private fun carregar(id: Long) =
        usuarios.findById(id).orElseThrow { RecursoNaoEncontradoException("Usuário não encontrado") }
}
