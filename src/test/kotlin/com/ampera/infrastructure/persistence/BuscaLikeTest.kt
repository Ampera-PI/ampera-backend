package com.ampera.infrastructure.persistence

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest(
    properties = [
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
    ],
)
class BuscaLikeTest {
    @Autowired lateinit var usuarios: UsuarioRepository
    @Autowired lateinit var residencias: ResidenciaRepository
    @Autowired lateinit var comodos: ComodoRepository
    @Autowired lateinit var dispositivos: DispositivoRepository

    private fun usuario(email: String, nome: String) = usuarios.save(Usuario(nome = nome, email = email, senhaHash = "x"))

    @Test
    fun `busca parcial de usuario ignora maiusculas`() {
        usuario("a@x.com", "Arthur Lima")
        usuario("b@x.com", "Maria Silva")
        assertEquals(listOf("Arthur Lima"), usuarios.buscarPorNome("THUR").map { it.nome })
    }

    @Test
    fun `busca de residencia respeita o dono`() {
        val ana = usuario("ana@x.com", "Ana")
        val beto = usuario("beto@x.com", "Beto")
        residencias.save(Residencia(usuario = ana, nome = "Casa de Praia", endereco = "Rua 1", cidade = "Santos"))
        residencias.save(Residencia(usuario = beto, nome = "Praia Grande", endereco = "Rua 2", cidade = "Santos"))

        val achadas = residencias.buscarPorNome(ana.id, "praia")

        assertEquals(listOf("Casa de Praia"), achadas.map { it.nome })
        assertTrue(residencias.buscarPorNome(ana.id, "montanha").isEmpty())
    }

    @Test
    fun `busca de dispositivo percorre residencia e comodo ate o dono`() {
        val ana = usuario("ana@x.com", "Ana")
        val casa = residencias.save(Residencia(usuario = ana, nome = "Casa", endereco = "Rua 1", cidade = "Santos"))
        val sala = comodos.save(Comodo(residencia = casa, nome = "Sala"))
        dispositivos.save(Dispositivo(comodo = sala, nome = "Medidor da sala", codigoMqtt = "medidor-sala"))
        dispositivos.save(Dispositivo(comodo = sala, nome = "Geladeira", codigoMqtt = "geladeira"))

        assertEquals(listOf("Medidor da sala"), dispositivos.buscarPorNome(ana.id, "medi").map { it.nome })
        assertTrue(dispositivos.buscarPorNome(ana.id + 99, "medi").isEmpty())
    }
}
