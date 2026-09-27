package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmail(email: String): Usuario?

    @Query("select u from Usuario u where lower(u.nome) like lower(concat('%', :termo, '%')) order by u.nome")
    fun buscarPorNome(termo: String): List<Usuario>
}
