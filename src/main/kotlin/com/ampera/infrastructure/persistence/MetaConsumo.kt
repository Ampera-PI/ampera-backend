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
import java.time.LocalDate

@Entity
@Table(name = "meta_consumo")
class MetaConsumo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id")
    var residencia: Residencia = Residencia(),
    @Column(nullable = false, length = 120)
    var titulo: String = "Meta de consumo",
    @Column(name = "limite_kwh")
    var limiteKwh: Double = 0.0,
    var inicio: LocalDate = LocalDate.EPOCH,
    var fim: LocalDate = LocalDate.EPOCH,
    @Column(nullable = false)
    var ativa: Boolean = true,
)
