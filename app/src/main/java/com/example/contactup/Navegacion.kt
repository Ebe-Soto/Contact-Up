package com.example.contactup

sealed class Pantalla(val ruta: String, val titulo: String) {
    object Login : Pantalla("login", "Login")
    object Registro : Pantalla("registro", "Registro")

    object AgregarContacto : Pantalla("agregarContacto", "Agregar Contacto")

    object DetalleContacto : Pantalla("detalle_contacto/{contactoId}", "Detalle del contacto")

    object Perfil : Pantalla("perfil", "Mi Perfil")
    object Todos : Pantalla("todos", "Todos")
    object Favoritos : Pantalla("favoritos", "Favoritos")
    object Grupos : Pantalla("grupos", "Grupos")
}

val tabsPrincipales = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Grupos)