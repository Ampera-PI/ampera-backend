package com.ampera.mapper

import com.ampera.dto.AlertaRequest
import com.ampera.dto.AlertaResponse
import com.ampera.infrastructure.persistence.Alerta
import com.ampera.infrastructure.persistence.Dispositivo
import org.springframework.stereotype.Component

@Component
class AlertaMapper {
    fun toResponse(e: Alerta) =
        AlertaResponse(e.id, e.dispositivo.id, e.grandeza, e.limiar, e.mensagem, e.ativo, e.disparado)

    fun toEntity(r: AlertaRequest, dispositivo: Dispositivo) = Alerta(dispositivo = dispositivo).also { atualizar(it, r, dispositivo) }

    fun atualizar(e: Alerta, r: AlertaRequest, dispositivo: Dispositivo) {
        e.dispositivo = dispositivo
        e.grandeza = r.grandeza
        e.limiar = r.limiar
        e.mensagem = r.mensagem.trim()
        e.ativo = r.ativo
    }
}
