package com.example.contactup.data

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import androidx.compose.ui.graphics.toArgb

class ConversorColor {

    @TypeConverter
    fun fromColor(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(valorArgb: Int): Color {
        return Color(valorArgb)
    }
}