package com.ampera.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class TarifaRequest(
    @field:NotNull @field:Positive val residenciaId: Long,
    @field:Size(max = 80) val distribuidora: String = "",
    @field:NotBlank @field:Size(max = 20) val bandeira: String = "VERDE",
    @field:NotNull @field:Positive val valorKwh: BigDecimal,
    @field:NotNull val vigenciaInicio: LocalDate,
    val vigenciaFim: LocalDate? = null,
    val ativa: Boolean = true,
)

data class TarifaResponse(
    val id: Long,
    val residenciaId: Long,
    val distribuidora: String,
    val bandeira: String,
    val valorKwh: BigDecimal,
    val vigenciaInicio: LocalDate,
    val vigenciaFim: LocalDate?,
    val ativa: Boolean,
)
