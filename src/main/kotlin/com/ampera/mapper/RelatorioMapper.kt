package com.ampera.mapper

import com.ampera.dto.RelatorioRequest
import com.ampera.dto.RelatorioResponse
import com.ampera.infrastructure.persistence.Relatorio
import com.ampera.infrastructure.persistence.Residencia
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class RelatorioMapper {
    fun toResponse(e: Relatorio) = RelatorioResponse(
        e.id, e.residencia.id, e.titulo, e.inicio, e.fim, e.consumoKwh, e.custoTotal, e.previsaoKwh, e.criadoEm,
    )

    fun toEntity(r: RelatorioRequest, residencia: Residencia) =
        Relatorio(residencia = residencia, criadoEm = Instant.now()).also { atualizar(it, r, residencia) }

    fun atualizar(e: Relatorio, r: RelatorioRequest, residencia: Residencia) {
        e.residencia = residencia
        e.titulo = r.titulo.trim()
        e.inicio = r.inicio
        e.fim = r.fim
        e.consumoKwh = r.consumoKwh
        e.custoTotal = r.custoTotal
        e.previsaoKwh = r.previsaoKwh
    }
}
