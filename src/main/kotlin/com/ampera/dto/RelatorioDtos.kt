package com.ampera.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class RelatorioRequest(
    @field:NotNull @field:Positive val residenciaId: Long,
    @field:NotBlank @field:Size(max = 120) val titulo: String = "Relatório",
    @field:NotNull val inicio: LocalDate,
    @field:NotNull val fim: LocalDate,
    @field:PositiveOrZero val consumoKwh: Double = 0.0,
    @field:PositiveOrZero val custoTotal: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero val previsaoKwh: Double = 0.0,
)

data class GerarRelatorioRequest(
    @field:NotNull @field:Positive val residenciaId: Long,
    @field:NotNull val inicio: LocalDate,
    @field:NotNull val fim: LocalDate,
    @field:Size(max = 120) val titulo: String = "Relatório gerado",
)

data class RelatorioResponse(
    val id: Long,
    val residenciaId: Long,
    val titulo: String,
    val inicio: LocalDate,
    val fim: LocalDate,
    val consumoKwh: Double,
    val custoTotal: BigDecimal,
    val previsaoKwh: Double,
    val criadoEm: Instant,
)
