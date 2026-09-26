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
import com.example.contactup.data.Grupo
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
import com.example.contactup.data.ActualizarPerfilUsuario
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

            var correoUsuario by remember {
                mutableStateOf("")
            }

            ContactUpTheme(darkTheme = modoOscuro) {

                val contactos = remember { mutableStateListOf<Contacto>() }

                // Estado de grupos, compartido entre las pantallas de Grupos
                val grupos = remember { mutableStateListOf<Grupo>() }

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

                                            ObtenerDatosUsuario { nombre, telefono, correo ->

                                                nombreUsuario = nombre ?: ""
                                                telefonoUsuario = telefono ?: ""
                                                correoUsuario = correo ?: ""

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
                                    grupos = grupos.filter { g -> g.miembros.any { it.id == contacto.id } }.map { it.nombre },
                                    onClickAtras = {
                                        navController.popBackStack()
                                    },
                                    onClickEditar = {
                                        navController.navigate("editar_contacto/${contacto.id}")
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
                                        navController.navigate("editar_contacto/${contacto.id}")
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

                        composable(Pantalla.EditarContacto.ruta) { backStackEntry ->

                            val contactoId =
                                backStackEntry.arguments?.getString("contactoId")?.toIntOrNull()

                            val contacto =
                                contactos.firstOrNull { it.id == contactoId }

                            if (contacto != null) {
                                PantallaEditarContacto(
                                    contacto = contacto,
                                    todosLosGrupos = grupos,
                                    onClickCancelar = { navController.popBackStack() },
                                    onGuardar = { contactoEditado, gruposSeleccionados ->

                                        // Actualiza los datos del contacto
                                        val index = contactos.indexOfFirst { it.id == contacto.id }
                                        if (index != -1) {
                                            contactos[index] = contactoEditado
                                        }

                                        // Sincroniza la pertenencia a grupos (agrega/quita al contacto de cada grupo)
                                        for (i in grupos.indices) {
                                            val grupo = grupos[i]
                                            val debeEstar = grupo.id in gruposSeleccionados
                                            val estaActualmente = grupo.miembros.any { it.id == contacto.id }

                                            grupos[i] = when {
                                                debeEstar && !estaActualmente ->
                                                    grupo.copy(miembros = grupo.miembros + contactoEditado)
                                                !debeEstar && estaActualmente ->
                                                    grupo.copy(miembros = grupo.miembros.filterNot { it.id == contacto.id })
                                                debeEstar && estaActualmente ->
                                                    grupo.copy(miembros = grupo.miembros.map { if (it.id == contacto.id) contactoEditado else it })
                                                else -> grupo
                                            }
                                        }

                                        soundPool.play(sonidoCrear, 1f, 1f, 1, 0, 1f)

                                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                                            )
                                        } else {
                                            @Suppress("DEPRECATION")
                                            vibrator.vibrate(80)
                                        }

                                        scope.launch {
                                            snackbarHostState.showSnackbar("Contacto actualizado")
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
                                correo = correoUsuario,
                                onClickAtras = {
                                    navController.popBackStack()
                                },
                                onClickEditar = {
                                    navController.navigate(Pantalla.EditarPerfil.ruta)
                                }
                            )
                        }

                        composable(Pantalla.EditarPerfil.ruta) {
                            PantallaEditarPerfil(
                                nombre = nombreUsuario,
                                telefono = telefonoUsuario,
                                correo = correoUsuario,
                                onClickCancelar = { navController.popBackStack() },
                                onGuardar = { nombre, telefono, correo ->

                                    // Nota: "telefono" aquí es solo el número mostrado en el perfil.
                                    // No cambia el correo "falso" con el que Firebase Auth identifica
                                    // la cuenta (generado a partir del teléfono original de registro),
                                    // así que editarlo aquí no afecta las credenciales de inicio de sesión.
                                    nombreUsuario = nombre
                                    telefonoUsuario = telefono
                                    correoUsuario = correo

                                    ActualizarPerfilUsuario(nombre, telefono, correo) { exito, _ ->
                                        if (exito) {
                                            soundPool.play(sonidoCrear, 1f, 1f, 1, 0, 1f)

                                            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                vibrator.vibrate(
                                                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                                                )
                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator.vibrate(80)
                                            }

                                            scope.launch {
                                                snackbarHostState.showSnackbar("Perfil actualizado")
                                            }
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("No se pudo actualizar el perfil")
                                            }
                                        }
                                    }

                                    navController.popBackStack()
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
                            PantallaGrupos(
                                navController = navController,
                                grupos = grupos,
                                tabActual = Pantalla.Grupos
                            )
                        }

                        composable(Pantalla.CrearGrupo.ruta) {
                            PantallaCrearGrupo(
                                contactosDisponibles = contactos,
                                onCancelar = { navController.popBackStack() },
                                onCrear = { nuevoGrupo ->
                                    val idAsignado = (grupos.maxOfOrNull { it.id } ?: 0) + 1
                                    grupos.add(nuevoGrupo.copy(id = idAsignado))

                                    // Feedback de sonido y vibración, igual que al agregar un contacto
                                    soundPool.play(sonidoCrear, 1f, 1f, 1, 0, 1f)

                                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(80)
                                    }

                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Grupo creado con éxito!")
                                    }

                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Pantalla.DetalleGrupo.ruta) { backStackEntry ->

                            val grupoId =
                                backStackEntry.arguments?.getString("grupoId")?.toIntOrNull()

                            val grupo = grupos.firstOrNull { it.id == grupoId }

                            if (grupo != null) {
                                PantallaDetalleGrupo(
                                    grupo = grupo,
                                    onBack = { navController.popBackStack() },
                                    onLlamadaGrupalClick = {
                                        navController.navigate("llamada_grupal/${grupo.id}")
                                    }
                                )
                            }
                        }

                        composable(Pantalla.LlamadaGrupal.ruta) { backStackEntry ->

                            val grupoId =
                                backStackEntry.arguments?.getString("grupoId")?.toIntOrNull()

                            val grupo = grupos.firstOrNull { it.id == grupoId }

                            if (grupo != null) {
                                PantallaLlamadaGrupal(
                                    grupo = grupo,
                                    onCerrar = { navController.popBackStack() },
                                    onIniciarLlamada = { _ ->
                                        // TODO: conectar con la lógica real de llamada grupal (WebRTC, etc.)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Función de llamada grupal en desarrollo")
                                        }
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}