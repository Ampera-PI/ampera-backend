package com.ampera.domain

class RecursoNaoEncontradoException(mensagem: String) : RuntimeException(mensagem)

class AcessoNegadoException(mensagem: String) : RuntimeException(mensagem)

class RegraDeNegocioException(mensagem: String) : RuntimeException(mensagem)
