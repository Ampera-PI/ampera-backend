package com.ampera.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class LinhaDeBaseEnergiaTest {
    @Test
    fun `leitura igual a anterior nao indica reinicio`() {
        assertFalse(LinhaDeBaseEnergia().reiniciou(2.5, 2.5))
    }
}
