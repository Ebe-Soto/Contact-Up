package com.example.contactup

import org.junit.Test
import com.example.contactup.data.Contacto
import org.junit.Assert.assertEquals

class BusquedaYAgrupacionTest {

    private val listaDePrueba = listOf(
        Contacto(id = 1, nombre = "Karla Hernández", telefono = "8112223344", correo = "karla@gmail.com"),
        Contacto(id = 2, nombre = "Alejandro Cruz", telefono = "8199887766", correo = "alex@gmail.com"),
        Contacto(id = 3, nombre = "Ana López", telefono = "8144556677", correo = "ana@gmail.com")
    )

    @Test
    fun busquedaFiltraPorNombreSinImportarMayusculas() {
        val busqueda = "ale"
        val resultado = listaDePrueba.filter {
            it.nombre.contains(busqueda, ignoreCase = true)
        }
        assertEquals(1, resultado.size)
        assertEquals("Alejandro Cruz", resultado.first().nombre)
    }

    @Test
    fun agrupacionOrdenaYAgrupaPorPrimeraLetra() {
        val agrupado = listaDePrueba
            .sortedBy { it.nombre }
            .groupBy { it.nombre.first().uppercase() }

        assertEquals(listOf("A", "K"), agrupado.keys.toList())
        assertEquals(2, agrupado["A"]?.size)
        assertEquals(1, agrupado["K"]?.size)
    }
}