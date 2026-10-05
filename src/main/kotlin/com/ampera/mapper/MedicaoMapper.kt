package com.ampera.mapper

import com.ampera.dto.MedicaoRequest
import com.ampera.dto.MedicaoResponse
import com.ampera.infrastructure.persistence.Medicao
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class MedicaoMapper {
    fun toResponse(e: Medicao) = MedicaoResponse(
        e.id, e.dispositivo.id, e.tensao, e.corrente, e.potencia, e.energiaKwh, e.custo, e.instante, e.origem, e.observacao, e.validada,
    )

    fun toEntity(r: MedicaoRequest) = Medicao(instante = Instant.now()).also { atualizar(it, r) }

    fun atualizar(e: Medicao, r: MedicaoRequest) {
        e.observacao = r.observacao.trim()
        e.validada = r.validada
    }
}
