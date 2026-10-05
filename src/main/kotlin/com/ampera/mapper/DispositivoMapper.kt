package com.ampera.mapper

import com.ampera.dto.DispositivoRequest
import com.ampera.dto.DispositivoResponse
import com.ampera.infrastructure.persistence.Comodo
import com.ampera.infrastructure.persistence.Dispositivo
import org.springframework.stereotype.Component

@Component
class DispositivoMapper {
    fun toResponse(e: Dispositivo) =
        DispositivoResponse(e.id, e.comodo.id, e.nome, e.codigoMqtt, e.fabricante, e.potenciaNominalW, e.instalacao, e.ativo)

    fun toEntity(r: DispositivoRequest, comodo: Comodo) = Dispositivo(comodo = comodo).also { atualizar(it, r, comodo) }

    fun atualizar(e: Dispositivo, r: DispositivoRequest, comodo: Comodo) {
        e.comodo = comodo
        e.nome = r.nome.trim()
        e.codigoMqtt = r.codigoMqtt.trim()
        e.fabricante = r.fabricante.trim()
        e.potenciaNominalW = r.potenciaNominalW
        e.instalacao = r.instalacao
        e.ativo = r.ativo
    }
}
