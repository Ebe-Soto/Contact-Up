package com.example.contactup

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.contactup.data.Contacto

@Composable
fun PantallaPrincipal(
    navController: NavController,
    contactos: List<Contacto> = emptyList(),
    onFavoritoClick: (Contacto) -> Unit = {},
    onEliminarContacto: (Contacto) -> Unit = {},
    tabActual: Pantalla = Pantalla.Todos,
    onClickAdd:() -> Unit = {},
    onClickZoom:() -> Unit = {},
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    mostrarBotonAgregar: Boolean = true
)
{

    var busqueda by remember {
        mutableStateOf("")
    }

    // Modo zoom: se activa/desactiva con el botón de lupa
    var modoZoom by remember { mutableStateOf(false) }

    // Contacto pendiente de confirmación para eliminar (swipe izquierdo)
    var contactoAEliminar by remember { mutableStateOf<Contacto?>(null) }

    // Contacto pendiente de confirmación para quitar de favoritos
    var contactoAQuitarFavorito by remember { mutableStateOf<Contacto?>(null) }

    val contactosFiltrados = contactos.filter {
        it.nombre.contains(busqueda, ignoreCase = true)
    }

    val contactosAgrupados = contactosFiltrados
        .sortedBy { it.nombre }
        .groupBy { it.nombre.first().uppercase() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Encabezado
            Column {
                Text(
                    "Contactos",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${contactos.size} Contactos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(15.dp))

            // Navegacion Perfil Usuario
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Pantalla.Perfil.ruta)
                    },

                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("TU", color = MaterialTheme.colorScheme.onSurface)
                }

                Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(
                        "Tu perfil",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Ver información del usuario logeado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.alpha(0.5f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "",
                        modifier = Modifier.size(25.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            Column {

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar contactos...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(15.dp))

                // Tabs navegables
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

                Spacer(Modifier.height(12.dp))

                // Toggle modo claro/oscuro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (modoOscuro) "Modo oscuro" else "Modo claro",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (modoOscuro) "Cambiar a claro" else "Cambiar a oscuro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = { onModoOscuroChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (modoZoom) {
                    Text(
                        "Modo zoom activo: pellizca para acercar, toca la lupa para salir",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Lista de contactos: envuelta en el área con zoom por pellizco
                AreaConZoom(
                    activo = modoZoom,
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        contactosAgrupados.forEach { (letra, contactosDeLetra) ->
                            item {
                                Text(
                                    letra,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(contactosDeLetra, key = { it.id }) { contacto ->
                                FilaDeslizable(
                                    item = contacto,
                                    onSolicitarEliminar = { contactoAEliminar = it }
                                ) {
                                    FilaContacto(
                                        contacto = contacto,
                                        onFavoritoClick = {
                                            if (contacto.favorito) {
                                                contactoAQuitarFavorito = contacto
                                            } else {
                                                onFavoritoClick(contacto)
                                            }
                                        },
                                        onClickContacto = {
                                            navController.navigate(
                                                "detalle_contacto/${contacto.id}"
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botones flotantes inferiores
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = {
                    modoZoom = !modoZoom
                    onClickZoom()
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (modoZoom) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(32.dp)
                    )
            ) {
                Icon(
                    Icons.Default.ZoomIn,
                    contentDescription = "Activar modo zoom",
                    tint = if (modoZoom) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                )
            }
            if (mostrarBotonAgregar) {
                IconButton(
                    onClick = onClickAdd,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(32.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar contacto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // Diálogo: confirmar eliminación de contacto (por swipe)
    contactoAEliminar?.let { contacto ->
        DialogoConfirmacion(
            titulo = "Eliminar contacto",
            mensaje = "¿Seguro que quieres eliminar a ${contacto.nombre}?",
            textoConfirmar = "Eliminar",
            esDestructivo = true,
            onConfirmar = {
                onEliminarContacto(contacto)
                contactoAEliminar = null
            },
            onCancelar = { contactoAEliminar = null }
        )
    }

    // Diálogo: confirmar quitar de favoritos
    contactoAQuitarFavorito?.let { contacto ->
        DialogoConfirmacion(
            titulo = "Quitar de favoritos",
            mensaje = "¿Seguro que quieres quitar de favoritos a: ${contacto.nombre}?",
            textoConfirmar = "Quitar",
            esDestructivo = false,
            onConfirmar = {
                onFavoritoClick(contacto)
                contactoAQuitarFavorito = null
            },
            onCancelar = { contactoAQuitarFavorito = null }
        )
    }
}

@Composable
fun FilaContacto(
    contacto: Contacto,
    onFavoritoClick: () -> Unit,
    onClickContacto:() -> Unit

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClickContacto)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                contacto.nombre.split(" ").take(2).joinToString("") { it.first().toString() },
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                contacto.nombre,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                contacto.telefono,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onFavoritoClick) {
            Icon(
                imageVector = if (contacto.favorito) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "Favorito",
                tint = if (contacto.favorito) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}