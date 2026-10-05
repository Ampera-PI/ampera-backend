package com.ampera.dto

import com.ampera.domain.OrigemMedicao
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant

data class MedicaoRequest(
    @field:NotNull @field:Positive val dispositivoId: Long,
    @field:PositiveOrZero val tensao: Double,
    @field:PositiveOrZero val corrente: Double,
    @field:PositiveOrZero val potencia: Double,
    @field:PositiveOrZero val energiaKwh: Double,
    @field:Size(max = 200) val observacao: String = "",
    val validada: Boolean = true,
)

data class MedicaoResponse(
    val id: Long,
    val dispositivoId: Long,
    val tensao: Double,
    val corrente: Double,
    val potencia: Double,
    val energiaKwh: Double,
    val custo: BigDecimal,
    val instante: Instant,
    val origem: OrigemMedicao,
    val observacao: String,
    val validada: Boolean,
)
