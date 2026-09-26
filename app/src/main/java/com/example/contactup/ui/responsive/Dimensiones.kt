package com.example.contactup.ui.responsive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Conjunto de medidas que cambian según el ancho de pantalla del dispositivo,
 * para que el padding de cada pantalla no se vea apretado en un teléfono
 * pequeño ni demasiado suelto en una tablet.
 */
data class Dimensiones(
    val paddingContenido: Dp,
    val espacioEntreSecciones: Dp = 16.dp
)

// Teléfono compacto (< 600dp de ancho, la mayoría de los teléfonos)
private val DimensionesCompactas = Dimensiones(
    paddingContenido = 16.dp,
    espacioEntreSecciones = 12.dp
)

// Teléfono grande / tablet pequeña (600dp - 840dp)
private val DimensionesMedianas = Dimensiones(
    paddingContenido = 24.dp,
    espacioEntreSecciones = 16.dp
)

// Tablet grande (> 840dp)
private val DimensionesExpandidas = Dimensiones(
    paddingContenido = 32.dp,
    espacioEntreSecciones = 20.dp
)

/**
 * CompositionLocal con un valor por defecto (DimensionesMedianas), para que
 * ningún Composable truene si por alguna razón se usa fuera de ProveerDimensiones
 * (por ejemplo, en un @Preview aislado).
 */
val LocalDimensiones = staticCompositionLocalOf { DimensionesMedianas }

/**
 * Calcula las Dimensiones adecuadas según el ancho de pantalla actual y las
 * expone a través de LocalDimensiones para todo el árbol de Compose que
 * quede dentro de "content".
 *
 * Se debe envolver el contenido de MainActivity con este Composable
 * (dentro de ContactUpTheme) para que LocalDimensiones.current refleje
 * el tamaño real del dispositivo en cada pantalla.
 */
@Composable
fun ProveerDimensiones(content: @Composable () -> Unit) {
    val anchoPantalla = LocalConfiguration.current.screenWidthDp.dp

    val dimensiones = when {
        anchoPantalla < 600.dp -> DimensionesCompactas
        anchoPantalla < 840.dp -> DimensionesMedianas
        else -> DimensionesExpandidas
    }

    CompositionLocalProvider(LocalDimensiones provides dimensiones) {
        content()
    }
}