package com.ampera.domain

import java.math.BigDecimal
import java.math.RoundingMode

class CalculadoraDeCusto {
    fun calcular(energiaKwh: Double, valorKwh: BigDecimal): BigDecimal =
        valorKwh.multiply(BigDecimal.valueOf(energiaKwh)).setScale(4, RoundingMode.HALF_UP)
}
