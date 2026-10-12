package com.ampera.api

import com.ampera.domain.AcessoNegadoException
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.ErroResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException::class)
    fun naoEncontrado(ex: RecursoNaoEncontradoException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErroResponse(ex.message))

    @ExceptionHandler(AcessoNegadoException::class)
    fun negado(ex: AcessoNegadoException) =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErroResponse(ex.message))

    @ExceptionHandler(RegraDeNegocioException::class)
    fun regra(ex: RegraDeNegocioException) =
        ResponseEntity.badRequest().body(ErroResponse(ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacao(ex: MethodArgumentNotValidException): ResponseEntity<ErroResponse> {
        val texto = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(ErroResponse(texto))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun corpoInvalido(@Suppress("UNUSED_PARAMETER") ex: HttpMessageNotReadableException) =
        ResponseEntity.badRequest().body(ErroResponse("Corpo inválido"))

    @ExceptionHandler(BadCredentialsException::class, AuthenticationException::class)
    fun credenciais(@Suppress("UNUSED_PARAMETER") ex: AuthenticationException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErroResponse("E-mail ou senha inválidos"))
}
