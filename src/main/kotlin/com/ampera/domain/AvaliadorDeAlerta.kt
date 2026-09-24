package com.ampera.domain

class AvaliadorDeAlerta {
    fun disparou(
        grandeza: Grandeza,
        limiar: Double,
        tensao: Double,
        corrente: Double,
        potencia: Double,
    ): Boolean {
        val valor = when (grandeza) {
            Grandeza.TENSAO -> tensao
            Grandeza.CORRENTE -> corrente
            Grandeza.POTENCIA -> potencia
        }
        return valor > limiar
    }
}
