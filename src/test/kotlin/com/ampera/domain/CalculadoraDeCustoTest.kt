package com.ampera.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CalculadoraDeCustoTest {
    @Test
    fun `custo arredonda metade para cima na quarta casa decimal`() {
        assertEquals(BigDecimal("0.1235"), CalculadoraDeCusto().calcular(1.0, BigDecimal("0.12345")))
        assertEquals(BigDecimal("0.1234"), CalculadoraDeCusto().calcular(1.0, BigDecimal("0.12344")))
    }

    @Test
    fun `energia zero resulta em custo zero com quatro casas`() {
        assertEquals(BigDecimal("0.0000"), CalculadoraDeCusto().calcular(0.0, BigDecimal("0.85")))
    }
}
