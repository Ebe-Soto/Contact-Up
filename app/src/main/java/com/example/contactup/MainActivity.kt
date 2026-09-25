package com.example.contactup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactup.data.Contacto
import com.example.contactup.data.IniciarSesion
import com.example.contactup.data.RegistrarUser
import com.example.contactup.ui.theme.ContactUpTheme
import kotlinx.coroutines.launch
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.util.Log
import android.media.SoundPool
import androidx.compose.runtime.mutableStateListOf
import com.example.contactup.data.ObtenerDatosUsuario

fun telefonoComoCorreo(telefono: String): String {
    val soloDigitos = telefono.filter { it.isDigit() }
    return "$soloDigitos@gmail.com"   // Firebase Auth exige formato de correo, aunque sea "falso"
}

class MainActivity : ComponentActivity() {

    private lateinit var soundPool: SoundPool

    // Variables para guardar identificadores de varios sonidos
    private var sonidoLogin = 0
    private var sonidoRegistro = 0
    private var sonidoError = 0
    private var sonidoCrear = 0
    private var sonidoBorrar = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        // Generamos soundPool permitiendo reproducir hasta 3 sonidos a la vez
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .build()

        // Llamamos a los archivos .wav dentro de carpeta res/raw
        sonidoLogin = soundPool.load(this, R.raw.login, 1)
        sonidoRegistro = soundPool.load(this, R.raw.register, 1)
        sonidoError = soundPool.load(this, R.raw.error, 1)
        sonidoCrear = soundPool.load(this, R.raw.create, 1)
        sonidoBorrar = soundPool.load(this, R.raw.delete, 0)

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            var modoOscuro by remember {
                mutableStateOf(false)
            }

            var nombreUsuario by remember {
                mutableStateOf("")
            }

            var telefonoUsuario by remember {
                mutableStateOf("")
            }

            ContactUpTheme(darkTheme = modoOscuro) {

                val contactos = remember { mutableStateListOf<Contacto>() }

                val navController = rememberNavController()

                // Variable para controlar el estado de carga
                var isLoading by remember { mutableStateOf(false) }

                // Controla los mensajes que aparecen mediante Snackbar
                val snackbarHostState = remember { SnackbarHostState() }

                // Permite ejecutar acciones mediante corrutinas
                val scope = rememberCoroutineScope()

                // Obtiene el contexto actual de la aplicación
                val context = LocalContext.current

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->

                    NavHost(
                        navController = navController,
                        startDestination = Pantalla.Login.ruta,
                        modifier = Modifier.padding(padding)
                    ) {

                        composable(Pantalla.Login.ruta) {
                            PantallaLogin(
                                OnClick = { telefono, contrasena ->
                                    isLoading = true
                                    IniciarSesion(telefono, contrasena) { exito, error ->
                                        isLoading = false
                                        if (exito) {

                                            // Feedback de sonido al iniciar sesion exitosamente
                                            soundPool.play(
                                                sonidoLogin,
                                                1f,
                                                1f,
                                                1,
                                                0,
                                                1f
                                            )

                                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                            // Si el usuario utliza un dispositivo más viejo, no cargará el proceso Vibration
                                            // Se soluciona mediante una función antigua
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(
                                                        80,
                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )

                                                // Se utiliza como prueba para reconocer en el emulador su ejecucion
                                                Log.d("CONTACTUP", "Vibración ejecutada")

                                            } else {

                                                // Metodo utilizado en versiones antiguas de Android
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(80)

                                                // Se utiliza como prueba para reconocer en el emulador su ejecucion
                                                Log.d("CONTACTUP", "Vibración antigua ejecutada")
                                            }

                                            ObtenerDatosUsuario { nombre, telefono ->

                                                nombreUsuario = nombre ?: ""
                                                telefonoUsuario = telefono ?: ""

                                                scope.launch {
                                                    snackbarHostState.showSnackbar("¡Bienvenido!")
                                                }

                                                navController.navigate(Pantalla.Todos.ruta) {
                                                    popUpTo(Pantalla.Login.ruta) {
                                                        inclusive = true
                                                    }
                                                }
                                            }

                                        } else {

                                            scope.launch {
                                                snackbarHostState.showSnackbar( "No se pudo iniciar sesión. Teléfono o Contraseña incorrectos.")
                                            }

                                            // Feedback de sonido de error
                                            soundPool.play(
                                                sonidoError,
                                                1f,
                                                1f,
                                                1,
                                                0,
                                                1f
                                            )

                                            val vibrator =
                                                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(
                                                        80,
                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )

                                                Log.d("CONTACTUP", "Vibración de error ejecutada")

                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(80)

                                                Log.d("CONTACTUP", "Vibración de error antigua ejecutada")
                                            }
                                        }
                                    }
                                },
                                onClickReg = { navController.navigate(Pantalla.Registro.ruta) }
                            )
                        }

                        composable(Pantalla.Registro.ruta) {
                            PantallaRegistro(
                                isLoading = isLoading,
                                onCrearCuentaClick = { nombre, telefono, contrasena ->
                                    isLoading = true
                                    RegistrarUser(nombre, telefono, contrasena) { exito, error ->
                                        isLoading = false
                                        if (exito) {

                                            // Feedback de sonido al registrarse con exito
                                            soundPool.play(
                                                sonidoRegistro,
                                                1f,
                                                1f,
                                                1,
                                                0,
                                                1f
                                            )

                                            // Declaramos la variable para acceder al sensor del telefono
                                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                            // Si el usuario utliza un dispositivo más viejo, no cargará el proceso
                                            // Se soluciona con una condición If para que no pierda la posibilidad de usar la app
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(
                                                        80,
                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )

                                                // Se utiliza como prueba para reconocer en el emulador su ejecucion
                                                Log.d("CONTACTUP", "Vibración ejecutada")

                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(80)

                                                Log.d("CONTACTUP", "Vibración ejecutada (API antigua)")
                                            }

                                            scope.launch {
                                                snackbarHostState.showSnackbar("¡Cuenta creada con éxito!")
                                            }

                                            navController.navigate(Pantalla.Login.ruta) {
                                                popUpTo(Pantalla.Registro.ruta) {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(error ?: "No se pudo crear la cuenta")

                                                // Feedback de sonido de error
                                                soundPool.play(
                                                    sonidoError,
                                                    1f,
                                                    1f,
                                                    1,
                                                    0,
                                                    1f
                                                )
                                            }
                                        }
                                    }
                                },
                                onClickLogin = { navController.navigate(Pantalla.Login.ruta) }
                            )
                        }

                        composable(Pantalla.AgregarContacto.ruta) {
                            FormAgregarCon(
                                onClickCancelar = { navController.popBackStack() },
                                onClickGuardar = { nombre, telefono, correo ->

                                    contactos.add(
                                        Contacto(
                                            id = contactos.size + 1,
                                            nombre = nombre,
                                            telefono = telefono,
                                            correo = correo
                                        )
                                    )

                                    // Feedback de sonido exitoso
                                    soundPool.play(
                                        sonidoCrear,
                                        1f,
                                        1f,
                                        1,
                                        0,
                                        1f
                                    )

                                    // Vibracion
                                    val vibrator =
                                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(
                                                80,
                                                VibrationEffect.DEFAULT_AMPLITUDE
                                            )
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(80)
                                    }

                                    // Feedback visual de exito
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Contacto agregado con éxito!")
                                    }

                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Pantalla.DetalleContacto.ruta) { backStackEntry ->

                            val contactoId =
                                backStackEntry.arguments?.getString("contactoId")?.toIntOrNull()

                            val contacto =
                                contactos.firstOrNull { it.id == contactoId }

                            if (contacto != null) {
                                PantallaDetalleContacto(
                                    contacto = contacto,
                                    onClickAtras = {
                                        navController.popBackStack()
                                    },
                                    onClickEditar = {
                                        // Pendiente
                                    },
                                    onClickLlamar = {
                                        // Pendiente
                                    },
                                    onClickFavorito = {
                                        val index =
                                            contactos.indexOfFirst { it.id == contacto.id }

                                        if (index != -1) {
                                            contactos[index] =
                                                contactos[index].copy(
                                                    favorito = !contactos[index].favorito
                                                )
                                        }
                                    },
                                    onClickAgregarGrupo = {
                                        // Pendiente
                                    },
                                    onClickEliminar = {
                                        soundPool.play(
                                            sonidoBorrar,
                                            1f,
                                            1f,
                                            1,
                                            0,
                                            1f
                                        )

                                        val vibrator =
                                            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    80,
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
                                        } else {
                                            @Suppress("DEPRECATION")
                                            vibrator.vibrate(80)
                                        }

                                        // Metodo de Eliminacion
                                        contactos.removeAll { it.id == contacto.id }

                                        // Feedback visual de exito
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Contacto eliminado")
                                        }

                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable(Pantalla.Perfil.ruta) {
                            PantallaPerfil(
                                nombre = nombreUsuario,
                                telefono = telefonoUsuario,
                                onClickAtras = {
                                    navController.popBackStack()
                                },
                                onClickEditar = {
                                    // Pendiente
                                }
                            )
                        }


                        composable(Pantalla.Todos.ruta) {
                            PantallaPrincipal(
                                navController = navController,
                                contactos = contactos,
                                tabActual = Pantalla.Todos,
                                modoOscuro = modoOscuro,
                                onModoOscuroChange = { modoOscuro = it },
                                onClickAdd = { navController.navigate(Pantalla.AgregarContacto.ruta) },
                                onClickZoom = {
                                    // Pendiente
                                },
                                onFavoritoClick = { contacto ->
                                    val index = contactos.indexOfFirst { it.id == contacto.id }
                                    if (index != -1) {
                                        contactos[index] = contactos[index].copy(favorito = !contactos[index].favorito)
                                    }
                                },
                                mostrarBotonAgregar = true
                            )
                        }

                        composable(Pantalla.Favoritos.ruta) {
                            PantallaFavoritos(
                                navController = navController,
                                contactos = contactos.filter { it.favorito },
                                onFavoritoClick = { contacto ->
                                    val index = contactos.indexOfFirst { it.id == contacto.id }

                                    if (index != -1) {
                                        contactos[index] =
                                            contactos[index].copy(
                                                favorito = !contactos[index].favorito
                                            )
                                    }
                                }
                            )
                        }
                        composable(Pantalla.Grupos.ruta) {
                            PantallaPrincipal(
                                navController = navController,
                                contactos = emptyList(),
                                tabActual = Pantalla.Grupos,
                                modoOscuro = modoOscuro,
                                onModoOscuroChange = { modoOscuro = it },
                                mostrarBotonAgregar = false
                            )
                        }
                    }
                }
            }
        }
    }
}