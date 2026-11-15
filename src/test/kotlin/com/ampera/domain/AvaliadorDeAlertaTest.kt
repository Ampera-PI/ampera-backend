package com.ampera.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class AvaliadorDeAlertaTest {
    @ParameterizedTest
    @EnumSource(Grandeza::class)
    fun `cada grandeza considera apenas seu valor e exige ultrapassar limiar`(grandeza: Grandeza) {
        for (valor in listOf(9.0, 10.0, 11.0)) {
            val resultado = AvaliadorDeAlerta().disparou(
                grandeza, 10.0,
                if (grandeza == Grandeza.TENSAO) valor else 999.0,
                if (grandeza == Grandeza.CORRENTE) valor else 999.0,
                if (grandeza == Grandeza.POTENCIA) valor else 999.0,
            )
            assertEquals(valor > 10.0, resultado)
        }
    }
}
