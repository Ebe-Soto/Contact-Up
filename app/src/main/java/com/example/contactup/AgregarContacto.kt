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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactup.ui.responsive.LocalDimensiones
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.contactup.ui.theme.ContactUpTheme


@Composable
fun FormAgregarCon(
    onClickCancelar:() -> Unit = {},
    onClickGuardar:(
        nombre: String,
        telefono: String,
        correo: String
    ) -> Unit = { _, _, _, -> }

) {

    var nombre by remember{
        mutableStateOf("")
    }

    var telefono by remember{
        mutableStateOf("")
    }

    var correo by remember{
        mutableStateOf("")
    }

    var errorNombre by remember {
        mutableStateOf<String?>(null) }

    var errorTelefono by remember {
        mutableStateOf<String?>(null) }

    var errorCorreo by remember {
        mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(LocalDimensiones.current.paddingContenido)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable(onClick = onClickCancelar)
                        .background(MaterialTheme.colorScheme.background),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Cancelar",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp,0.dp,50.dp)
                )

                Button(
                    onClick = {

                        errorNombre =
                            if (!esNombreValido(nombre))
                                "El nombre no puede estar vacío"
                            else
                                null

                        errorTelefono =
                            if (!esTelefonoAgregadoValido(telefono))
                                "Número inválido, debe tener 10 dígitos"
                            else
                                null

                        errorCorreo =
                            if (!esCorreoValido(correo))
                                "Correo inválido, debe contener @"
                            else
                                null

                        if (
                            errorNombre == null &&
                            errorTelefono == null &&
                            errorCorreo == null
                        ) {
                            onClickGuardar(
                                nombre.trim(),
                                telefono.trim(),
                                correo.trim()
                            )
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .padding(55.dp,0.dp,0.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Guardar",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Text("NOMBRE",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = null
                },
                placeholder = { Text("Nombre Completo") },
                isError = errorNombre != null,
                supportingText = {
                    Text(
                        errorNombre ?: "El campo no debe estar vacío",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (errorNombre != null)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            Text("TELÉFONO",
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
                        errorTelefono ?: "Debe ser un número de 10 dígitos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (errorTelefono != null)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("CORREO",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
                placeholder = {
                    Text("ejemplo@correo.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Drafts,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                isError = errorCorreo != null,
                supportingText = {
                    Text(
                        errorCorreo ?: "Debe contener @",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (errorCorreo != null)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForm() {
    ContactUpTheme() {
        FormAgregarCon()
    }
}

fun esNombreValido(nombre: String): Boolean {
    return nombre.trim().isNotEmpty()
}

fun esTelefonoAgregadoValido(telefono: String): Boolean {
    return telefono.length == 10 && telefono.all { it.isDigit() }
}

fun esCorreoValido(correo: String): Boolean {
    return correo.contains("@") &&
            correo.substringAfter("@").isNotEmpty()
}