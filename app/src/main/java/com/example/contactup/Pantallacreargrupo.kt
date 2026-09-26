package com.example.contactup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contactup.data.ColoresGrupo
import com.example.contactup.data.Contacto
import com.example.contactup.data.Grupo
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaCrearGrupo(
    contactosDisponibles: List<Contacto> = emptyList(),
    onCancelar: () -> Unit = {},
    onCrear: (Grupo, List<Contacto>) -> Unit = { _, _ -> }
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf(ColoresGrupo[1]) }
    var miembrosSeleccionados by remember { mutableStateOf(setOf<Contacto>()) }
    var errorNombre by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Barra superior: Cancelar / Crear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cancelar",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onCancelar)
            )
            Text(
                "Crear",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    errorNombre = if (nombre.isBlank()) "El nombre del grupo es obligatorio" else null
                    if (errorNombre == null) {
                        onCrear(
                            Grupo(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                color = colorSeleccionado
                            ),
                            miembrosSeleccionados.toList()
                        )
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        // Nombre del grupo
        Text(
            "NOMBRE DEL GRUPO",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                errorNombre = null
            },
            placeholder = { Text("p. ej. Trabajo") },
            isError = errorNombre != null,
            supportingText = errorNombre?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Descripción
        Text(
            "DESCRIPCIÓN",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = { Text("Colegas y contactos del proyecto") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Color del grupo
        Text(
            "COLOR DEL GRUPO",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ColoresGrupo.forEach { color ->
                val seleccionado = color == colorSeleccionado
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { colorSeleccionado = color }
                        .then(
                            if (seleccionado)
                                Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (seleccionado) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Selección de miembros
        Text(
            "SELECCIONAR MIEMBROS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))

        if (contactosDisponibles.isEmpty()) {
            Text(
                "Aún no tienes contactos guardados. Agrega uno primero desde la pantalla de Contactos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contactosDisponibles) { contacto ->
                    FilaSeleccionContacto(
                        contacto = contacto,
                        seleccionado = contacto in miembrosSeleccionados,
                        onToggle = {
                            miembrosSeleccionados = if (contacto in miembrosSeleccionados) {
                                miembrosSeleccionados - contacto
                            } else {
                                miembrosSeleccionados + contacto
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilaSeleccionContacto(
    contacto: Contacto,
    seleccionado: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                contacto.nombre.split(" ").take(2).joinToString("") { it.first().toString() },
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(contacto.nombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                contacto.telefono,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent)
                .border(
                    1.5.dp,
                    if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ---- Previews ----

private val contactosPreview = listOf(
    Contacto(id = 1, nombre = "Adriana Acosta", telefono = "+34 612 345 678", correo = "adriana@correo.com"),
    Contacto(id = 2, nombre = "Alejandro Gómez", telefono = "+34 699 888 777", correo = "alejandro@correo.com"),
    Contacto(id = 3, nombre = "Beatriz Caro", telefono = "+34 655 444 333", correo = "beatriz@correo.com")
)

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaCrearGrupoClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaCrearGrupo(contactosDisponibles = contactosPreview)
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaCrearGrupoOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaCrearGrupo(contactosDisponibles = contactosPreview)
    }
}