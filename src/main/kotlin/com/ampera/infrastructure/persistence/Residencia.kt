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
@Table(name = "residencia")
class Residencia(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    var usuario: Usuario = Usuario(),
    @Column(nullable = false, length = 120)
    var nome: String = "",
    @Column(nullable = false, length = 255)
    var endereco: String = "",
    @Column(nullable = false, length = 80)
    var cidade: String = "",
    @Column(name = "area_m2", nullable = false)
    var areaM2: Double = 0.0,
    @Column(name = "numero_moradores", nullable = false)
    var numeroMoradores: Int = 1,
)
