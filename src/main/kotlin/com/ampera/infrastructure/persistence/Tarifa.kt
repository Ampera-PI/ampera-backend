package com.ampera.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "tarifa")
class Tarifa(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id")
    var residencia: Residencia = Residencia(),
    @Column(nullable = false, length = 80)
    var distribuidora: String = "",
    @Column(nullable = false, length = 20)
    var bandeira: String = "VERDE",
    @Column(name = "valor_kwh")
    var valorKwh: BigDecimal = BigDecimal.ZERO,
    @Column(name = "vigencia_inicio")
    var vigenciaInicio: LocalDate = LocalDate.EPOCH,
    @Column(name = "vigencia_fim")
    var vigenciaFim: LocalDate? = null,
    @Column(nullable = false)
    var ativa: Boolean = true,
)
