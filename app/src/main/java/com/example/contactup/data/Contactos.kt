package com.example.contactup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Contacto(
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val favorito: Boolean = false
)