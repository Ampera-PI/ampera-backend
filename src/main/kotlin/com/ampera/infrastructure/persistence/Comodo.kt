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

@Entity
@Table(name = "comodo")
class Comodo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id")
    var residencia: Residencia = Residencia(),
    @Column(nullable = false, length = 120)
    var nome: String = "",
    @Column(nullable = false, length = 40)
    var tipo: String = "Outro",
    @Column(nullable = false)
    var andar: Int = 0,
    @Column(name = "area_m2", nullable = false)
    var areaM2: Double = 0.0,
    @Column(nullable = false)
    var ativo: Boolean = true,
)
