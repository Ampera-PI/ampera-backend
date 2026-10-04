package com.ampera.mapper

import com.ampera.dto.TarifaRequest
import com.ampera.dto.TarifaResponse
import com.ampera.infrastructure.persistence.Residencia
import com.ampera.infrastructure.persistence.Tarifa
import org.springframework.stereotype.Component

@Component
class TarifaMapper {
    fun toResponse(e: Tarifa) = TarifaResponse(
        e.id, e.residencia.id, e.distribuidora, e.bandeira, e.valorKwh, e.vigenciaInicio, e.vigenciaFim, e.ativa,
    )

    fun toEntity(r: TarifaRequest, residencia: Residencia) = Tarifa(residencia = residencia).also { atualizar(it, r, residencia) }

    fun atualizar(e: Tarifa, r: TarifaRequest, residencia: Residencia) {
        e.residencia = residencia
        e.distribuidora = r.distribuidora.trim()
        e.bandeira = r.bandeira.trim().uppercase()
        e.valorKwh = r.valorKwh
        e.vigenciaInicio = r.vigenciaInicio
        e.vigenciaFim = r.vigenciaFim
        e.ativa = r.ativa
    }
}
