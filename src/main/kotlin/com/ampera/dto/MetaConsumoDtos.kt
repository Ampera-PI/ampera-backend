package com.ampera.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class MetaConsumoRequest(
    @field:NotNull @field:Positive val residenciaId: Long,
    @field:NotBlank @field:Size(max = 120) val titulo: String = "Meta de consumo",
    @field:Positive val limiteKwh: Double,
    @field:NotNull val inicio: LocalDate,
    @field:NotNull val fim: LocalDate,
    val ativa: Boolean = true,
)

data class MetaConsumoResponse(
    val id: Long,
    val residenciaId: Long,
    val titulo: String,
    val limiteKwh: Double,
    val inicio: LocalDate,
    val fim: LocalDate,
    val ativa: Boolean,
    val consumoKwh: Double,
)
