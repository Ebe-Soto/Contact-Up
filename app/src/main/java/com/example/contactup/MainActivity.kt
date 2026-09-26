package com.example.contactup

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactup.data.ColoresGrupo
import com.example.contactup.data.Contacto
import com.example.contactup.data.Grupo
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
    Contacto(id = 4, nombre = "Carlos Gutiérrez", telefono = "+34 600 111 222", favorito = true),
    Contacto(id = 5, nombre = "María García", telefono = "+34 622 333 444"),
    Contacto(id = 6, nombre = "Jorge Plaza", telefono = "+34 633 222 111"),
    Contacto(id = 7, nombre = "Lucía Naranjo", telefono = "+34 644 555 666")
)

// Grupo de ejemplo, similar al mostrado en el diseño de Figma
private val gruposEjemplo = listOf(
    Grupo(
        id = 1,
        nombre = "Trabajo",
        descripcion = "Colegas y contactos del proyecto",
        color = ColoresGrupo[1],
        miembros = contactosEjemplo.filter {
            it.nombre in listOf("María García", "Adriana Acosta", "Carlos Gutiérrez", "Jorge Plaza", "Lucía Naranjo")
        }
    )
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

    // Estado de grupos compartido entre las pantallas de Grupos
    val grupos = remember { mutableStateListOf(*gruposEjemplo.toTypedArray()) }

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

        // ---- Flujo de Grupos ----

        composable(Pantalla.Grupos.ruta) {
            PantallaGrupos(
                navController = navController,
                grupos = grupos,
                tabActual = Pantalla.Grupos
            )
        }

        composable(Pantalla.CrearGrupo.ruta) {
            PantallaCrearGrupo(
                contactosDisponibles = contactosEjemplo,
                onCancelar = { navController.popBackStack() },
                onCrear = { nuevoGrupo ->
                    val idAsignado = (grupos.maxOfOrNull { it.id } ?: 0) + 1
                    grupos.add(nuevoGrupo.copy(id = idAsignado))
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Pantalla.DetalleGrupo.ruta,
            arguments = listOf(navArgument("grupoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getInt("grupoId") ?: -1
            val grupo = grupos.find { it.id == grupoId }
            if (grupo != null) {
                PantallaDetalleGrupo(
                    grupo = grupo,
                    onBack = { navController.popBackStack() },
                    onLlamadaGrupalClick = {
                        navController.navigate(Pantalla.LlamadaGrupal.crearRuta(grupo.id))
                    }
                )
            }
        }

        composable(
            route = Pantalla.LlamadaGrupal.ruta,
            arguments = listOf(navArgument("grupoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getInt("grupoId") ?: -1
            val grupo = grupos.find { it.id == grupoId }
            if (grupo != null) {
                PantallaLlamadaGrupal(
                    grupo = grupo,
                    onCerrar = { navController.popBackStack() },
                    onIniciarLlamada = { _ ->
                        // TODO: conectar con la lógica real de llamada grupal (p. ej. WebRTC / Firebase)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}