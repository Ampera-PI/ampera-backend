package com.ampera.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "usuario")
class Usuario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(nullable = false, length = 120)
    var nome: String = "",
    @Column(nullable = false, unique = true, length = 180)
    var email: String = "",
    @Column(name = "senha_hash", nullable = false, length = 255)
    var senhaHash: String = "",
    @Column(nullable = false, length = 20)
    var telefone: String = "",
    @Column(name = "data_nascimento")
    var dataNascimento: LocalDate? = null,
    @Column(nullable = false)
    var ativo: Boolean = true,
)
