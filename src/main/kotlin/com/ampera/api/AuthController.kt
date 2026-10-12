package com.ampera.api

import com.ampera.application.UsuarioService
import com.ampera.dto.SigninRequest
import com.ampera.dto.SigninResponse
import com.ampera.dto.UsuarioRequest
import com.ampera.dto.UsuarioResponse
import com.ampera.infrastructure.security.JwtUtil
import com.ampera.infrastructure.security.UsuarioPrincipal
import jakarta.validation.Valid
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val autenticacao: AuthenticationManager,
    private val jwt: JwtUtil,
    private val usuarios: UsuarioService,
) {
    @PostMapping("/signin")
    suspend fun signin(@Valid @RequestBody pedido: SigninRequest): SigninResponse {
        val auth = autenticacao.authenticate(UsernamePasswordAuthenticationToken(pedido.email, pedido.senha))
        val principal = auth.principal as UsuarioPrincipal
        return SigninResponse(jwt.gerar(principal.id, principal.username), usuarios.buscar(principal.id))
    }

    @PostMapping("/signup")
    suspend fun signup(@Valid @RequestBody pedido: UsuarioRequest): UsuarioResponse = usuarios.criar(pedido)
}
