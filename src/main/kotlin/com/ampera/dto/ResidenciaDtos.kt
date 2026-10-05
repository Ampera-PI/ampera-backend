package com.ampera.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class ResidenciaRequest(
    @field:NotBlank @field:Size(max = 120) val nome: String,
    @field:NotBlank @field:Size(max = 255) val endereco: String,
    @field:NotBlank @field:Size(max = 80) val cidade: String,
    @field:PositiveOrZero val areaM2: Double = 0.0,
    @field:Min(1) @field:Max(100) val numeroMoradores: Int = 1,
)

data class ResidenciaResponse(
    val id: Long,
    val usuarioId: Long,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val areaM2: Double,
    val numeroMoradores: Int,
)
