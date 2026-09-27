package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ComodoRepository : JpaRepository<Comodo, Long> {
    fun findByResidenciaUsuarioId(usuarioId: Long): List<Comodo>
    fun findByResidenciaIdAndResidenciaUsuarioId(residenciaId: Long, usuarioId: Long): List<Comodo>
    @Query(
        """
        select c from Comodo c
        where c.residencia.usuario.id = :usuarioId
          and lower(c.nome) like lower(concat('%', :termo, '%'))
        order by c.nome
        """,
    )
    fun buscarPorNome(usuarioId: Long, termo: String): List<Comodo>
}
