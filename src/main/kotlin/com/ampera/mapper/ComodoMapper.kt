package com.ampera.mapper

import com.ampera.dto.ComodoRequest
import com.ampera.dto.ComodoResponse
import com.ampera.infrastructure.persistence.Comodo
import com.ampera.infrastructure.persistence.Residencia
import org.springframework.stereotype.Component

@Component
class ComodoMapper {
    fun toResponse(e: Comodo) = ComodoResponse(e.id, e.residencia.id, e.nome, e.tipo, e.andar, e.areaM2, e.ativo)

    fun toEntity(r: ComodoRequest, residencia: Residencia) = Comodo(residencia = residencia).also { atualizar(it, r, residencia) }

    fun atualizar(e: Comodo, r: ComodoRequest, residencia: Residencia) {
        e.residencia = residencia
        e.nome = r.nome.trim()
        e.tipo = r.tipo.trim()
        e.andar = r.andar
        e.areaM2 = r.areaM2
        e.ativo = r.ativo
    }
}
