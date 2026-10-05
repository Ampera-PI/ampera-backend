package com.ampera.mapper

import com.ampera.dto.ResidenciaRequest
import com.ampera.dto.ResidenciaResponse
import com.ampera.infrastructure.persistence.Residencia
import com.ampera.infrastructure.persistence.Usuario
import org.springframework.stereotype.Component

@Component
class ResidenciaMapper {
    fun toResponse(e: Residencia) =
        ResidenciaResponse(e.id, e.usuario.id, e.nome, e.endereco, e.cidade, e.areaM2, e.numeroMoradores)

    fun toEntity(r: ResidenciaRequest, usuario: Usuario) = Residencia(usuario = usuario).also { atualizar(it, r) }

    fun atualizar(e: Residencia, r: ResidenciaRequest) {
        e.nome = r.nome.trim()
        e.endereco = r.endereco.trim()
        e.cidade = r.cidade.trim()
        e.areaM2 = r.areaM2
        e.numeroMoradores = r.numeroMoradores
    }
}
