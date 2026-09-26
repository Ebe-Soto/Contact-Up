package com.example.contactup

import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioUtilsTest {

    @Test
    fun correoNuloSeReemplazaPorCadenaVacia() {
        val resultado = sanitizarDatosUsuario("Ebe", "8134078074", null)
        assertEquals("", resultado.correo)
    }

    @Test
    fun datosCompletosSeMantienenIgual() {
        val resultado = sanitizarDatosUsuario("Ebe", "8134078074", "ebay2317@gmail.com")
        assertEquals("Ebe", resultado.nombre)
        assertEquals("8134078074", resultado.telefono)
        assertEquals("ebay2317@gmail.com", resultado.correo)
    }
}