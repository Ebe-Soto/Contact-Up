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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contactup.ui.responsive.LocalDimensiones
import com.example.contactup.ui.theme.ContactUpTheme

@Composable
fun PantallaEditarPerfil(
    nombre: String,
    telefono: String,
    correo: String = "",
    onClickCancelar: () -> Unit = {},
    onGuardar: (nombre: String, telefono: String, correo: String) -> Unit = { _, _, _ -> }
) {
    var nombreState by remember { mutableStateOf(nombre) }
    var correoState by remember { mutableStateOf(correo) }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(LocalDimensiones.current.paddingContenido)
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
                    errorNombre = if (!esNombreValido(nombreState)) "El nombre no puede estar vacío" else null
                    errorCorreo = if (!esCorreoValido(correoState)) "Correo inválido, debe contener @" else null

                    if (errorNombre == null && errorCorreo == null) {
                        onGuardar(nombreState.trim(), telefono, correoState.trim())
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

        // Avatar (misma convención que PantallaPerfil: iniciales fijas "TU")
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "TU",
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
            value = nombreState,
            onValueChange = { nombreState = it; errorNombre = null },
            isError = errorNombre != null,
            supportingText = errorNombre?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Text(
            "NÚMERO DE TELÉFONO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = {},
            enabled = false,
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "El número de teléfono no se puede modificar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            "CORREO ELECTRÓNICO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = correoState,
            onValueChange = { correoState = it; errorCorreo = null },
            leadingIcon = {
                Icon(Icons.Default.Drafts, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            isError = errorCorreo != null,
            supportingText = errorCorreo?.let { mensaje -> { Text(mensaje, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---- Previews ----

@Preview(name = "Claro", showBackground = true)
@Composable
fun PreviewPantallaEditarPerfilClaro() {
    ContactUpTheme(darkTheme = false) {
        PantallaEditarPerfil(nombre = "Tomás Úbeda", telefono = "+34 612 345 678", correo = "tomas.ubeda@agenda.com")
    }
}

@Preview(name = "Oscuro", showBackground = true)
@Composable
fun PreviewPantallaEditarPerfilOscuro() {
    ContactUpTheme(darkTheme = true) {
        PantallaEditarPerfil(nombre = "Tomás Úbeda", telefono = "+34 612 345 678", correo = "tomas.ubeda@agenda.com")
    }
}