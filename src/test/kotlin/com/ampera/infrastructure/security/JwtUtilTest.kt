package com.ampera.infrastructure.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JwtUtilTest {
    private val segredo = "segredo-de-teste-com-pelo-menos-32-bytes!!"
    private val jwt = JwtUtil(segredo, 60_000)

    @Test
    fun `token gerado e valido e devolve o usuario`() {
        val token = jwt.gerar(7, "ana@ampera.local")
        assertTrue(jwt.validar(token))
        assertEquals("ana@ampera.local", jwt.usuarioDe(token))
        assertEquals(7L, jwt.idDe(token))
    }

    @Test
    fun `token adulterado e recusado`() {
        val token = jwt.gerar(7, "ana@ampera.local") + "x"
        assertFalse(jwt.validar(token))
        assertNull(jwt.usuarioDe(token))
    }

    @Test
    fun `token assinado com outra chave e recusado`() {
        val outro = JwtUtil("outra-chave-de-teste-com-32-bytes-ou-mais!", 60_000)
        assertFalse(jwt.validar(outro.gerar(1, "x@y.z")))
    }

    @Test
    fun `token expirado e recusado`() {
        val expirado = JwtUtil(segredo, -1_000).gerar(1, "x@y.z")
        assertFalse(jwt.validar(expirado))
    }
}
