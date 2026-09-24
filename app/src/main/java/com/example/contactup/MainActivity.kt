package com.example.contactup

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactup.data.Contacto
import com.example.contactup.data.PhoneAuthManager
import com.example.contactup.ui.theme.ContactUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactUpTheme {
                ContactUpApp()
            }
        }
    }
}

private val contactosEjemplo = listOf(
    Contacto(id = 1, nombre = "Adriana Acosta", telefono = "+34 612 345 678", favorito = true),
    Contacto(id = 2, nombre = "Alejandro Gómez", telefono = "+34 699 888 777"),
    Contacto(id = 3, nombre = "Beatriz Caro", telefono = "+34 655 444 333"),
    Contacto(id = 4, nombre = "Carlos Gutiérrez", telefono = "+34 600 111 222", favorito = true)
)

fun formatearTelefono(telefono: String): String {
    val soloDigitos = telefono.filter { it.isDigit() }
    return "+52$soloDigitos"   // Le damos el formato necesario para que firebase pueda procesarlo
}

@Composable
fun ContactUpApp() {
    val navController = rememberNavController()
    val activity = LocalActivity.current as Activity
    val phoneAuthManager = remember { PhoneAuthManager() }

    var telefonoTemporal by remember { mutableStateOf("") }
    var errorAuth by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = Pantalla.Login.ruta) {

        composable(Pantalla.Login.ruta) {
            PantallaLogin(
                OnClick = { telefono, _ ->
                    telefonoTemporal = formatearTelefono(telefono)
                    phoneAuthManager.enviarCodigo(
                        telefono = telefono,
                        activity = activity,
                        onCodigoEnviado = { navController.navigate(Pantalla.Verificacion.ruta) },
                        onVerificacionAutomatica = {
                            navController.navigate(Pantalla.Todos.ruta) {
                                popUpTo(Pantalla.Login.ruta) { inclusive = true }
                            }
                        },
                        onError = { mensaje -> errorAuth = mensaje }
                    )
                },
                onClickReg = { navController.navigate(Pantalla.Registro.ruta) }
            )
        }

        composable(Pantalla.Registro.ruta) {
            PantallaRegistro(
                onCrearCuentaClick = { nombre, telefono, _ ->
                    telefonoTemporal = formatearTelefono(telefono)
                    phoneAuthManager.enviarCodigo(
                        telefono = telefonoTemporal,
                        activity = activity,
                        onCodigoEnviado = { navController.navigate(Pantalla.Verificacion.ruta) },
                        onVerificacionAutomatica = {
                            navController.navigate(Pantalla.Todos.ruta) {
                                popUpTo(Pantalla.Login.ruta) { inclusive = true }
                            }
                        },
                        onError = { mensaje -> errorAuth = mensaje }
                    )
                }
            )
        }

        composable(Pantalla.Verificacion.ruta) {
            PantallaVerificacion(
                telefono = telefonoTemporal,
                onConfirmar = { codigo ->
                    phoneAuthManager.confirmarCodigo(
                        codigo = codigo,
                        onExito = {
                            navController.navigate(Pantalla.Todos.ruta) {
                                popUpTo(Pantalla.Login.ruta) { inclusive = true }
                            }
                        },
                        onError = { mensaje -> errorAuth = mensaje }
                    )
                }
            )
        }

        composable(Pantalla.Todos.ruta) {
            PantallaPrincipal(
                navController = navController,
                contactos = contactosEjemplo,
                tabActual = Pantalla.Todos
            )
        }
        composable(Pantalla.Favoritos.ruta) {
            PantallaPrincipal(
                navController = navController,
                contactos = contactosEjemplo.filter { it.favorito },
                tabActual = Pantalla.Favoritos
            )
        }
        composable(Pantalla.Grupos.ruta) {
            PantallaPrincipal(
                navController = navController,
                contactos = contactosEjemplo,
                tabActual = Pantalla.Grupos
            )
        }
    }
}