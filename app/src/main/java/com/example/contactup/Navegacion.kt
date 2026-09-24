package com.example.contactup

sealed class Pantalla(val ruta: String, val titulo: String) {
    object Login : Pantalla("login", "Login")
    object Registro : Pantalla("registro", "Registro")
    object Verificacion : Pantalla("verificacion", "Verificación")
    object Todos : Pantalla("todos", "Todos")
    object Favoritos : Pantalla("favoritos", "Favoritos")
    object Grupos : Pantalla("grupos", "Grupos")
}

val tabsPrincipales = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Grupos)