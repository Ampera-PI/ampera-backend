package com.ampera.mapper

import com.ampera.dto.UsuarioRequest
import com.ampera.dto.UsuarioResponse
import com.ampera.infrastructure.persistence.Usuario
import org.springframework.stereotype.Component

@Component
class UsuarioMapper {
    fun toResponse(e: Usuario) = UsuarioResponse(e.id, e.nome, e.email, e.telefone, e.dataNascimento, e.ativo)

    fun toEntity(r: UsuarioRequest, senhaHash: String) = Usuario(
        nome = r.nome.trim(),
        email = r.email.trim(),
        senhaHash = senhaHash,
        telefone = r.telefone.trim(),
        dataNascimento = r.dataNascimento,
        ativo = r.ativo,
    )

    fun atualizar(e: Usuario, r: UsuarioRequest) {
        e.nome = r.nome.trim()
        e.email = r.email.trim()
        e.telefone = r.telefone.trim()
        e.dataNascimento = r.dataNascimento
        e.ativo = r.ativo
    }
}
