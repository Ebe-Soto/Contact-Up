package com.example.contactup

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contactup.data.ColoresGrupo
import com.example.contactup.data.Contacto
import com.example.contactup.data.Grupo
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaLlamadaGrupal(
    grupo: Grupo,
    onCerrar: () -> Unit = {},
    onIniciarLlamada: (List<Contacto>) -> Unit = {}
) {
    var seleccionados by remember { mutableStateOf(grupo.miembros.toSet()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Barra superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCerrar) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                "Llamada grupal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onCerrar) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "Destinatario: ${grupo.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            "Seleccionar participantes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            "${seleccionados.size} de ${grupo.miembros.size} seleccionados para unirse",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(16.dp))

        // Indicador de tipo de llamada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(24.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Llamada de voz", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (grupo.miembros.isEmpty()) {
            Text(
                "Este grupo no tiene miembros para llamar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f).padding(top = 24.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(grupo.miembros) { contacto ->
                    TarjetaMiembroSeleccionable(
                        contacto = contacto,
                        seleccionado = contacto in seleccionados,
                        onToggle = {
                            seleccionados = if (contacto in seleccionados) {
                                seleccionados - contacto
                            } else {
                                seleccionados + contacto
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onCerrar,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val listaSeleccionados = seleccionados.toList()

                    // Notificación "Llamando a..." — con un solo participante mostramos su
                    // nombre, con varios mostramos el nombre del grupo y cuántos entran.
                    val mensaje = when {
                        listaSeleccionados.size == 1 -> "Llamando a ${listaSeleccionados.first().nombre}"
                        else -> "Llamando a ${grupo.nombre} (${listaSeleccionados.size} participantes)"
                    }
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

                    onIniciarLlamada(listaSeleccionados)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                enabled = seleccionados.isNotEmpty()
            ) {
                Text("Iniciar llamada")
            }
        }
    }
}

@Composable
fun TarjetaMiembroSeleccionable(
    contacto: Contacto,
    seleccionado: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(
                1.5.dp,
                if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggle)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        contacto.nombre.split(" ").take(2).joinToString("") { it.first().toString() },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (seleccionado) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                contacto.nombre.split(" ").first(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ---- Previews ----

private val grupoLlamadaPreview = Grupo(
    id = 1,
    nombre = "Trabajo",
    color = ColoresGrupo[1],
    miembros = listOf(
        Contacto(nombre = "María García", telefono = "", correo = ""),
        Contacto(nombre = "Adriana Acosta", telefono = "", correo = ""),
        Contacto(nombre = "Carlos Gutiérrez", telefono = "", correo = ""),
        Contacto(nombre = "Jorge Plaza", telefono = "", correo = ""),
        Contacto(nombre = "Lucía Naranjo", telefono = "", correo = ""),
        Contacto(nombre = "Elena Ríos", telefono = "", correo = ""),
        Contacto(nombre = "Andrés Soto", telefono = "", correo = ""),
        Contacto(nombre = "Paula Torres", telefono = "", correo = ""),
        Contacto(nombre = "Miguel Ángel", telefono = "", correo = ""),
        Contacto(nombre = "Sofía Molina", telefono = "", correo = "")
    )
)

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaLlamadaGrupalClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaLlamadaGrupal(grupo = grupoLlamadaPreview)
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaLlamadaGrupalOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaLlamadaGrupal(grupo = grupoLlamadaPreview)
    }
}