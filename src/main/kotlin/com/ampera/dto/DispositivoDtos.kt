package com.ampera.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class DispositivoRequest(
    @field:NotNull @field:Positive val comodoId: Long,
    @field:NotBlank @field:Size(max = 120) val nome: String,
    @field:NotBlank @field:Size(max = 80) val codigoMqtt: String,
    @field:Size(max = 80) val fabricante: String = "",
    @field:PositiveOrZero val potenciaNominalW: Double = 0.0,
    @field:PastOrPresent val instalacao: LocalDate? = null,
    val ativo: Boolean = true,
)

data class DispositivoResponse(
    val id: Long,
    val comodoId: Long,
    val nome: String,
    val codigoMqtt: String,
    val fabricante: String,
    val potenciaNominalW: Double,
    val instalacao: LocalDate?,
    val ativo: Boolean,
)
