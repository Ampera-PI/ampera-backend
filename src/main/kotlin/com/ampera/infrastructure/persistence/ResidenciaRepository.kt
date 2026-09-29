package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ResidenciaRepository : JpaRepository<Residencia, Long> {
    fun findByUsuarioId(usuarioId: Long): List<Residencia>

    @Query(
        """
        select r from Residencia r
        where r.usuario.id = :usuarioId
          and lower(r.nome) like lower(concat('%', :termo, '%'))
        order by r.nome
        """,
    )
    fun buscarPorNome(usuarioId: Long, termo: String): List<Residencia>
}
