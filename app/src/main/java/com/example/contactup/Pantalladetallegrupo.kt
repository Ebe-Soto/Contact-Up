package com.example.contactup

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contactup.data.ColoresGrupo
import com.example.contactup.data.Contacto
import com.example.contactup.data.Grupo
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaDetalleGrupo(
    grupo: Grupo,
    onBack: () -> Unit = {},
    onLlamadaGrupalClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Barra superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Info del grupo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(grupo.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = Color.White)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    grupo.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${grupo.miembros.size} miembros activos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Llamada grupal + agregar miembro
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onLlamadaGrupalClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(26.dp),
                enabled = grupo.miembros.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Llamada grupal")
            }

            Spacer(Modifier.width(10.dp))

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Agregar miembro", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Miembros del grupo (${grupo.miembros.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(10.dp))

        if (grupo.miembros.isEmpty()) {
            Text(
                "Este grupo aún no tiene miembros.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(grupo.miembros) { contacto ->
                    FilaMiembroDetalle(contacto)
                }
            }
        }
    }
}

@Composable
fun FilaMiembroDetalle(contacto: Contacto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
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

        IconButton(onClick = { }) {
            Icon(Icons.Default.Phone, contentDescription = "Llamar", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// ---- Previews ----

private val grupoPreview = Grupo(
    id = 1,
    nombre = "Trabajo",
    color = ColoresGrupo[1],
    miembros = listOf(
        Contacto(nombre = "María García", telefono = "+34 612 111 111", correo = "maria@correo.com"),
        Contacto(nombre = "Adriana Acosta", telefono = "+34 612 345 678", correo = "adriana@correo.com"),
        Contacto(nombre = "Carlos Gutiérrez", telefono = "+34 600 111 222", correo = "carlos@correo.com"),
        Contacto(nombre = "Jorge Plaza", telefono = "+34 633 222 111", correo = "jorge@correo.com"),
        Contacto(nombre = "Lucía Naranjo", telefono = "+34 644 555 666", correo = "lucia@correo.com")
    )
)

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaDetalleGrupoClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaDetalleGrupo(grupo = grupoPreview)
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaDetalleGrupoOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaDetalleGrupo(grupo = grupoPreview)
    }
}