package com.example.contactup.data

import androidx.compose.ui.graphics.Color

data class Grupo(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val color: Color,
    val miembros: List<Contacto> = emptyList()
)

// Paleta de colores seleccionables al crear un grupo (coincide con el diseño de Figma)
val ColoresGrupo = listOf(
    Color(0xFFE8935B), // Naranja
    Color(0xFF8B5CF6), // Morado
    Color(0xFF4CAF50), // Verde
    Color(0xFFE8B923), // Amarillo
    Color(0xFF4A90D9)  // Azul
)