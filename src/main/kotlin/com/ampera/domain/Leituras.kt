package com.ampera.domain

data class LeituraDoMedidor(
    val codigoMqtt: String,
    val tensao: Double,
    val corrente: Double,
    val potencia: Double,
    val energiaKwh: Double,
)

data class LeituraEnergia(
    val dia: String,
    val instanteMillis: Long,
    val energiaKwh: Double,
)
