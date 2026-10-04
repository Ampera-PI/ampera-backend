package com.ampera.mapper

import com.ampera.dto.MetaConsumoRequest
import com.ampera.dto.MetaConsumoResponse
import com.ampera.infrastructure.persistence.MetaConsumo
import com.ampera.infrastructure.persistence.Residencia
import org.springframework.stereotype.Component

@Component
class MetaConsumoMapper {
    fun toResponse(e: MetaConsumo, consumoKwh: Double) =
        MetaConsumoResponse(e.id, e.residencia.id, e.titulo, e.limiteKwh, e.inicio, e.fim, e.ativa, consumoKwh)

    fun toEntity(r: MetaConsumoRequest, residencia: Residencia) = MetaConsumo(residencia = residencia).also { atualizar(it, r, residencia) }

    fun atualizar(e: MetaConsumo, r: MetaConsumoRequest, residencia: Residencia) {
        e.residencia = residencia
        e.titulo = r.titulo.trim()
        e.limiteKwh = r.limiteKwh
        e.inicio = r.inicio
        e.fim = r.fim
        e.ativa = r.ativa
    }
}
