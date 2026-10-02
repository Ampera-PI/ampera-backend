package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RelatorioRepository : JpaRepository<Relatorio, Long> {
    fun findByResidenciaUsuarioId(usuarioId: Long): List<Relatorio>

    @Query(
        """
        select r from Relatorio r
        where r.residencia.usuario.id = :usuarioId
          and lower(r.titulo) like lower(concat('%', :termo, '%'))
        order by r.titulo
        """,
    )
    fun buscarPorTitulo(usuarioId: Long, termo: String): List<Relatorio>
}
