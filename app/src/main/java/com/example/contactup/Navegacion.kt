package com.example.contactup

sealed class Pantalla(val ruta: String, val titulo: String) {
    object Login : Pantalla("login", "Login")
    object Registro : Pantalla("registro", "Registro")
    object Verificacion : Pantalla("verificacion", "Verificación")
    object Todos : Pantalla("todos", "Todos")
    object Favoritos : Pantalla("favoritos", "Favoritos")
    object Grupos : Pantalla("grupos", "Grupos")
    object CrearGrupo : Pantalla("crear_grupo", "Crear Grupo")

    object DetalleGrupo : Pantalla("detalle_grupo/{grupoId}", "Detalle de Grupo") {
        fun crearRuta(grupoId: Int) = "detalle_grupo/$grupoId"
    }

    object LlamadaGrupal : Pantalla("llamada_grupal/{grupoId}", "Llamada Grupal") {
        fun crearRuta(grupoId: Int) = "llamada_grupal/$grupoId"
    }
}

val tabsPrincipales = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Grupos)