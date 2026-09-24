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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.contactup.ui.theme.ContactUpTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone

@Composable
fun PantallaRegistro(
    onCrearCuentaClick:(
        nombre: String,
        telefono: String,
        contrasena: String
    ) -> Unit = { _, _, _ -> }

){
    var nombre by remember {
        mutableStateOf("")
    }

    var telefono by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var aceptarTerminos by remember {
        mutableStateOf(false)
    }

    var errorTelefono by remember {
        mutableStateOf<String?>(null) }

    var errorContrasena by remember {
        mutableStateOf<String?>(null) }

    var errorTerminos by remember {
        mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row (
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            // Icono Visual
            Box (
                modifier = Modifier
                    .size(75.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        Spacer(Modifier.height((5.dp)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            // Titulo
            Text(
                "Contact Up!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(6.dp))
        Spacer(Modifier.height(24.dp))

        // Creacion de Campo Nombre Completo
        Text("Nombre completo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Tu nombre")}, // Label de ejemplo que se quita al insertar informacion
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))


        // Creacion de Campo Numero Telefonico
        Text("Número Telefónico",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errorTelefono = null
            },
            placeholder = {
                Text("Número de 10 Dígitos") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = errorTelefono != null,
            supportingText = {
                Text(
                    errorTelefono ?: "Debe ser un numero de 10 digitos",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Debe ser un numero de 10 dígitos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(14.dp))

        // Creacion de Campo Contrasena
        Text("Contraseña",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface)

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                errorContrasena = null
            },
            placeholder = { Text("••••••••") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = errorContrasena != null,
            supportingText = {
                Text(
                    errorContrasena ?: "Mínimo 8 caracteres",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "8+ caracteres, número y símbolo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(5.dp))

        // Creacion checkbox de terminos y condiciones
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptarTerminos,
                onCheckedChange = {
                    aceptarTerminos = it
                    errorTerminos = null
                },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.onSurface)
            )
            Text(
                "Acepto los términos y condiciones",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (errorTerminos != null) {
            Text(
                errorTerminos!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(5.dp))

        // Boton crear cuenta
        Button(
            // Al hacer click, el programa ejecuta las validaciones correspondientes
            // Si es correcto, se guardaran las variables dentro de la funcion definida al inicio
            // Si no cumplen, se mostrara un mensaje de error
            onClick = {
                errorTelefono = if (!esTelefonoValido(telefono)) "Número inválido, debe ser de 10 dígitos" else null
                errorContrasena = if (!esContrasenaValida(contrasena)) "Debe tener al menos 8 caracteres" else null
                errorTerminos = if (!aceptarTerminos) "Debes aceptar los términos y condiciones" else null

                if (errorTelefono == null && errorContrasena == null && errorTerminos == null) {
                    onCrearCuentaClick(nombre, telefono, contrasena)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text("Crear cuenta",
                fontSize = 16.sp
            )
        }
        Text(
            "Al continuar aceptas la Política de privacidad.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PantallaRegistroPreview(){
    ContactUpTheme() {
        PantallaRegistro()
    }
}

// Funcion de validacion de correo (estructura)
fun esTelefonoValido(telefono: String): Boolean {
    return telefono.length == 10
}

// Funcion de validacion de contrasena (longitud minima)
fun esContrasenaValida(contrasena: String): Boolean {
    return contrasena.length >= 8
}