package com.example.contactup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.contactup.data.GrupoConContactos
import com.example.contactup.data.id
import com.example.contactup.data.nombre
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaEditarContacto(
    contacto: Contacto,
    todosLosGrupos: List<GrupoConContactos> = emptyList(),
    onClickCancelar: () -> Unit = {},
    onGuardar: (contactoEditado: Contacto, gruposSeleccionados: Set<Int>) -> Unit = { _, _ -> }
) {
    var nombre by remember { mutableStateOf(contacto.nombre) }
    var telefono by remember { mutableStateOf(contacto.telefono) }
    var correo by remember { mutableStateOf(contacto.correo) }

    var gruposSeleccionados by remember {
        mutableStateOf(
            todosLosGrupos
                .filter { grupo -> grupo.miembros.any { it.id == contacto.id } }
                .map { it.id }
                .toSet()
        )
    }

    var mostrarMasGrupos by remember { mutableStateOf(false) }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }

    val gruposUnidos = todosLosGrupos.filter { it.id in gruposSeleccionados }
    val gruposDisponibles = todosLosGrupos.filter { it.id !in gruposSeleccionados }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Barra superior: Cancelar / Guardar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onClickCancelar)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = {
                    errorNombre = if (!esNombreValido(nombre)) "El nombre no puede estar vacío" else null
                    errorTelefono = if (!esTelefonoAgregadoValido(telefono)) "Número inválido, debe tener 10 dígitos" else null
                    errorCorreo = if (!esCorreoValido(correo)) "Correo inválido, debe contener @" else null

                    if (errorNombre == null && errorTelefono == null && errorCorreo == null) {
                        onGuardar(
                            contacto.copy(
                                nombre = nombre.trim(),
                                telefono = telefono.trim(),
                                correo = correo.trim()
                            ),
                            gruposSeleccionados
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Guardar")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Avatar (se actualiza en vivo con el nombre que se va escribiendo)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
                        .joinToString("") { it.first().uppercase() }
                        .ifEmpty { "?" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "NOMBRE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; errorNombre = null },
            isError = errorNombre != null,
            supportingText = errorNombre?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Text(
            "TELÉFONO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it; errorTelefono = null },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            isError = errorTelefono != null,
            supportingText = errorTelefono?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Text(
            "CORREO ELECTRÓNICO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; errorCorreo = null },
            leadingIcon = {
                Icon(Icons.Default.Drafts, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            isError = errorCorreo != null,
            supportingText = errorCorreo?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))

        Text(
            "GRUPOS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            gruposUnidos.forEach { grupo ->
                ChipGrupo(
                    texto = grupo.nombre,
                    seleccionado = true,
                    onClick = { gruposSeleccionados = gruposSeleccionados - grupo.id }
                )
            }
            ChipGrupo(
                texto = "+ Añadir",
                seleccionado = false,
                onClick = { mostrarMasGrupos = !mostrarMasGrupos }
            )
        }

        if (mostrarMasGrupos) {
            Spacer(Modifier.height(10.dp))
            if (gruposDisponibles.isEmpty()) {
                Text(
                    "No hay más grupos disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp)
                ) {
                    gruposDisponibles.forEach { grupo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { gruposSeleccionados = gruposSeleccionados + grupo.id }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                grupo.nombre,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(Icons.Default.Add, contentDescription = "Añadir a ${grupo.nombre}", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipGrupo(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .border(
                1.dp,
                if (seleccionado) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
        )
        if (seleccionado) {
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ---- Previews ----

private val contactoPreview = Contacto(id = 1, nombre = "María García", telefono = "+34 677 555 444", correo = "maria.garcia@gmail.com")

private val gruposPreviewEditar = listOf(
    GrupoConContactos(grupo = Grupo(id = 1, nombre = "Trabajo", color = ColoresGrupo[1]), miembros = listOf(contactoPreview)),
    GrupoConContactos(grupo = Grupo(id = 2, nombre = "Familia", color = ColoresGrupo[0]), miembros = listOf(contactoPreview)),
    GrupoConContactos(grupo = Grupo(id = 3, nombre = "Amigos", color = ColoresGrupo[2]), miembros = emptyList())
)

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaEditarContactoClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaEditarContacto(contacto = contactoPreview, todosLosGrupos = gruposPreviewEditar)
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaEditarContactoOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaEditarContacto(contacto = contactoPreview, todosLosGrupos = gruposPreviewEditar)
    }
}