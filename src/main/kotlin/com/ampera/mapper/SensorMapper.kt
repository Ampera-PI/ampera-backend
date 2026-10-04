package com.ampera.mapper

import com.ampera.dto.SensorRequest
import com.ampera.dto.SensorResponse
import com.ampera.infrastructure.persistence.Dispositivo
import com.ampera.infrastructure.persistence.Sensor
import org.springframework.stereotype.Component

@Component
class SensorMapper {
    fun toResponse(e: Sensor) =
        SensorResponse(e.id, e.dispositivo.id, e.tipo, e.unidade, e.modelo, e.precisao, e.calibradoEm, e.ativo)

    fun toEntity(r: SensorRequest, dispositivo: Dispositivo) = Sensor(dispositivo = dispositivo).also { atualizar(it, r, dispositivo) }

    fun atualizar(e: Sensor, r: SensorRequest, dispositivo: Dispositivo) {
        e.dispositivo = dispositivo
        e.tipo = r.tipo
        e.unidade = r.unidade.trim()
        e.modelo = r.modelo.trim()
        e.precisao = r.precisao
        e.calibradoEm = r.calibradoEm
        e.ativo = r.ativo
    }
}
