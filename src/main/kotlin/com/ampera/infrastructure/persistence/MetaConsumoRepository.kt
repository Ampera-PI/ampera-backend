package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MetaConsumoRepository : JpaRepository<MetaConsumo, Long> {
    fun findByResidenciaUsuarioId(usuarioId: Long): List<MetaConsumo>
    fun findByResidenciaIdAndResidenciaUsuarioId(residenciaId: Long, usuarioId: Long): List<MetaConsumo>

    @Query(
        """
        select m from MetaConsumo m
        where m.residencia.usuario.id = :usuarioId
          and lower(m.titulo) like lower(concat('%', :termo, '%'))
        order by m.titulo
        """,
    )
    fun buscarPorTitulo(usuarioId: Long, termo: String): List<MetaConsumo>
}
