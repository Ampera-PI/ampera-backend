package com.ampera.domain

class AcessoPorDono {
    fun exigir(donoId: Long, atualId: Long) {
        if (donoId != atualId) {
            throw AcessoNegadoException("Recurso de outro usuário")
        }
    }
}
