package com.example.contactup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.contactup.data.ColoresGrupo
import com.example.contactup.data.Contacto
import com.example.contactup.data.Grupo
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaGrupos(
    navController: NavController,
    grupos: List<Grupo>,
    tabActual: Pantalla = Pantalla.Grupos
) {
    var busqueda by remember { mutableStateOf("") }

    val gruposFiltrados = grupos.filter {
        it.nombre.contains(busqueda, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Encabezado
            Text(
                "Grupos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Gestiona tus listas de contactos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(15.dp))

            // Tabs navegables (mismo patrón que PantallaPrincipal)
            Row(modifier = Modifier.fillMaxWidth()) {
                tabsPrincipales.forEach { tab ->
                    val seleccionada = tab == tabActual
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (!seleccionada) {
                                    navController.navigate(tab.ruta) {
                                        popUpTo(Pantalla.Todos.ruta) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            tab.titulo,
                            color = if (seleccionada) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (seleccionada) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar grupos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(15.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(gruposFiltrados) { grupo ->
                    FilaGrupo(
                        grupo = grupo,
                        onClick = { navController.navigate(Pantalla.DetalleGrupo.crearRuta(grupo.id)) }
                    )
                }
            }
        }

        // Botón flotante para crear grupo
        FloatingActionButton(
            onClick = { navController.navigate(Pantalla.CrearGrupo.ruta) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Crear grupo")
        }
    }
}

@Composable
fun FilaGrupo(
    grupo: Grupo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(grupo.color, RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                grupo.nombre,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${grupo.miembros.size} miembros",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// ---- Previews ----

private val gruposPreview = listOf(
    Grupo(id = 1, nombre = "Trabajo", color = ColoresGrupo[1], miembros = List(12) { Contacto(nombre = "Miembro $it", telefono = "") }),
    Grupo(id = 2, nombre = "Familia", color = ColoresGrupo[0], miembros = List(8) { Contacto(nombre = "Miembro $it", telefono = "") }),
    Grupo(id = 3, nombre = "Amigos", color = ColoresGrupo[2], miembros = List(15) { Contacto(nombre = "Miembro $it", telefono = "") }),
    Grupo(id = 4, nombre = "Universidad", color = ColoresGrupo[3], miembros = List(6) { Contacto(nombre = "Miembro $it", telefono = "") }),
    Grupo(id = 5, nombre = "Gimnasio", color = ColoresGrupo[4], miembros = List(4) { Contacto(nombre = "Miembro $it", telefono = "") })
)

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaGruposClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaGrupos(navController = rememberNavController(), grupos = gruposPreview)
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaGruposOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaGrupos(navController = rememberNavController(), grupos = gruposPreview)
    }
}