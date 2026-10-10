package com.ampera.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${ampera.jwt.secret}") secret: String,
    @Value("\${ampera.jwt.validade-ms}") private val validadeMs: Long,
) {
    private val chave: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun gerar(id: Long, email: String): String {
        val agora = Date()
        return Jwts.builder()
            .subject(email)
            .claim("uid", id)
            .issuedAt(agora)
            .expiration(Date(agora.time + validadeMs))
            .signWith(chave)
            .compact()
    }

    fun validar(token: String): Boolean = claims(token) != null

    fun usuarioDe(token: String): String? = claims(token)?.subject

    fun idDe(token: String): Long? = (claims(token)?.get("uid") as? Number)?.toLong()

    private fun claims(token: String): Claims? = try {
        Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).payload
    } catch (erro: JwtException) {
        log.warn("JWT recusado: {}", erro.message)
        null
    } catch (erro: IllegalArgumentException) {
        null
    }

    private companion object {
        val log = LoggerFactory.getLogger(JwtUtil::class.java)
    }
}
