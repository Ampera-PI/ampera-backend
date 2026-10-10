package com.ampera.api

import com.ampera.application.AlertaService
import com.ampera.dto.AlertaRequest
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize(USUARIO_AUTENTICADO)
@RequestMapping("/api/alertas")
class AlertaController(private val servico: AlertaService) {
    @GetMapping
    suspend fun listar(
        @RequestParam(required = false) dispositivoId: Long?,
        @RequestParam(required = false) busca: String?,
    ) = servico.listar(dispositivoId, busca)

    @GetMapping("/{id}")
    suspend fun buscar(@PathVariable id: Long) = servico.buscar(id)

    @PostMapping
    suspend fun criar(@Valid @RequestBody pedido: AlertaRequest) = servico.criar(pedido)

    @PutMapping("/{id}")
    suspend fun atualizar(@PathVariable id: Long, @Valid @RequestBody pedido: AlertaRequest) = servico.atualizar(id, pedido)

    @DeleteMapping("/{id}")
    suspend fun excluir(@PathVariable id: Long) = servico.excluir(id)
}
