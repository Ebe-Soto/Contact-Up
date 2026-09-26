package com.example.contactup

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Diálogo de confirmación genérico, usa los colores del tema de la app.
 * Se usa tanto para "eliminar contacto" como para "quitar de favoritos".
 */
@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    textoConfirmar: String = "Eliminar",
    textoCancelar: String = "Cancelar",
    esDestructivo: Boolean = true,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(20.dp),
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(
                    textoConfirmar,
                    color = if (esDestructivo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(textoCancelar, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}

/**
 * Envuelve una fila (ej. FilaContacto) para permitir deslizar hacia la izquierda.
 * No elimina nada por sí sola: solo "revela" el fondo rojo y avisa mediante
 * onSolicitarEliminar para que la pantalla muestre el diálogo de confirmación.
 * El swipe siempre vuelve a su posición (el borrado real solo pasa si se confirma).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilaDeslizable(
    item: T,
    onSolicitarEliminar: (T) -> Unit,
    content: @Composable () -> Unit
) {
    val estadoDeslizar = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart) {
                onSolicitarEliminar(item)
            }
            // Nunca confirmamos el dismiss automáticamente: la fila vuelve a su lugar
            // y solo se elimina de la lista si el usuario confirma en el diálogo.
            false
        }
    )

    SwipeToDismissBox(
        state = estadoDeslizar,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }
        }
    ) {
        content()
    }
}

/**
 * Envuelve cualquier contenido (normalmente una LazyColumn de contactos) para
 * permitir hacer zoom con gesto de pellizco (pinch) y desplazarse mientras el
 * zoom está activo. Cuando "activo" pasa a false, se resetea automáticamente.
 */
@Composable
fun AreaConZoom(
    activo: Boolean,
    modifier: Modifier = Modifier,
    escalaMaxima: Float = 4f,
    content: @Composable () -> Unit
) {
    var escala by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(activo) {
        if (!activo) {
            escala = 1f
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier = modifier.then(
            if (activo) {
                Modifier.pointerInput(activo) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        escala = (escala * zoom).coerceIn(1f, escalaMaxima)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
            } else Modifier
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = escala,
                    scaleY = escala,
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            content()
        }
    }
}

/**
 * Genera una vibración corta (feedback háptico), compatible con versiones
 * antiguas y nuevas de Android. Mismo patrón que se usa en MainActivity
 * para login, registro, crear/eliminar contacto, etc.
 * Se usa, por ejemplo, al iniciar una llamada (individual o grupal).
 */
fun vibrarCorto(context: Context, duracionMs: Long = 80) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(duracionMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duracionMs)
    }
}