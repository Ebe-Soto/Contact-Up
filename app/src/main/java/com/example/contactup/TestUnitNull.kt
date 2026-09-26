package com.example.contactup

data class DatosUsuario(
    val nombre: String,
    val telefono: String,
    val correo: String
)

fun sanitizarDatosUsuario(
    nombre: String?,
    telefono: String?,
    correo: String?
): DatosUsuario {
    return DatosUsuario(
        nombre = nombre ?: "",
        telefono = telefono ?: "",
        correo = correo ?: ""
    )
}