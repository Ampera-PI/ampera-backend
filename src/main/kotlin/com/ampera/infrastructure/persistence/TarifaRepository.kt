package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface TarifaRepository : JpaRepository<Tarifa, Long> {
    fun findByResidenciaUsuarioId(usuarioId: Long): List<Tarifa>
    fun findByResidenciaIdAndResidenciaUsuarioId(residenciaId: Long, usuarioId: Long): List<Tarifa>

    @Query(
        """
        select t from Tarifa t
        where t.residencia.usuario.id = :usuarioId
          and lower(t.distribuidora) like lower(concat('%', :termo, '%'))
        order by t.distribuidora
        """,
    )
    fun buscarPorDistribuidora(usuarioId: Long, termo: String): List<Tarifa>

    @Query(
        """
        select t from Tarifa t
        where t.residencia.id = :residenciaId
          and t.ativa = true
          and t.vigenciaInicio <= :dia
          and (t.vigenciaFim is null or t.vigenciaFim >= :dia)
        order by t.vigenciaInicio desc
        """,
    )
    fun vigentes(residenciaId: Long, dia: LocalDate): List<Tarifa>
}
