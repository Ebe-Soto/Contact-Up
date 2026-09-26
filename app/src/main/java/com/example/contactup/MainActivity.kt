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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contactup.data.AppDatabase
import com.example.contactup.data.ContactoRepository
import com.example.contactup.data.ContactoViewModel
import com.example.contactup.data.GrupoRepository
import com.example.contactup.data.GrupoViewModel
import androidx.compose.runtime.collectAsState
import com.example.contactup.data.id
import com.example.contactup.data.nombre
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.contactup.ui.responsive.ContenidoAdaptable
import com.example.contactup.ui.responsive.ProveedorResponsivo

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


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
            val windowSizeClass = calculateWindowSizeClass(this)

            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { ContactoRepository(database.contactoDao()) }
            val grupoRepository = remember { GrupoRepository(database.grupoDao()) }
            val grupoViewModel: GrupoViewModel = viewModel(
                factory = GrupoViewModel.Factory(grupoRepository)
            )

            val gruposConContactos by grupoViewModel.gruposConContactos.collectAsState()

            val contactoViewModel: ContactoViewModel = viewModel(
                factory = ContactoViewModel.Factory(repository)
            )

            val contactos by contactoViewModel.todosLosContactos.collectAsState()

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
                ProveedorResponsivo(windowSizeClass = windowSizeClass) {
                val navController = rememberNavController()

                // Variable para controlar el estado de carga
                var isLoading by remember { mutableStateOf(false) }

                // Controla los mensajes que aparecen mediante Snackbar
                val snackbarHostState = remember { SnackbarHostState() }

                // Permite ejecutar acciones mediante corrutinas
                val scope = rememberCoroutineScope()

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { padding ->

                        ContenidoAdaptable(modifier = Modifier.padding(padding)) {
                            NavHost(
                                navController = navController,
                                startDestination = Pantalla.Login.ruta,
                                modifier = Modifier.fillMaxSize()
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

                                    contactoViewModel.agregarContacto(nombre, telefono, correo)

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
                                    grupos = gruposConContactos.filter { g -> g.miembros.any { it.id == contacto.id } }.map { it.nombre },
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
                                        contactoViewModel.marcarFavorito(contacto, !contacto.favorito)
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

                                        // Metodo de Eliminacion (ahora vía Room)
                                        contactoViewModel.eliminarContacto(contacto)

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
                                    todosLosGrupos = gruposConContactos,
                                    onClickCancelar = { navController.popBackStack() },
                                    onGuardar = { contactoEditado, gruposSeleccionados ->

                                        // Actualiza los datos del contacto
                                        contactoViewModel.actualizarContacto(contactoEditado)

                                        // Sincroniza la pertenencia a grupos (agrega/quita al contacto de cada grupo)
                                        for (grupo in gruposConContactos) {
                                            val debeEstar = grupo.id in gruposSeleccionados
                                            val estaActualmente = grupo.miembros.any { it.id == contacto.id }

                                            if (debeEstar && !estaActualmente) {
                                                grupoViewModel.agregarMiembro(grupo.id, contacto.id)
                                            } else if (!debeEstar && estaActualmente) {
                                                grupoViewModel.quitarMiembro(grupo.id, contacto.id)
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
                                    // El modo zoom (pellizcar para acercar) ya se maneja
                                    // internamente en PantallaPrincipal; este callback queda
                                    // disponible por si luego quieres agregar analítica, sonido, etc.
                                },
                                onFavoritoClick = { contacto ->
                                    contactoViewModel.marcarFavorito(contacto, !contacto.favorito)
                                },
                                onEliminarContacto = { contacto ->
                                    soundPool.play(sonidoBorrar, 1f, 1f, 1, 0, 1f)

                                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(80)
                                    }

                                    // También lo quitamos de cualquier grupo al que pertenezca
                                    for (grupo in gruposConContactos) {
                                        if (grupo.miembros.any { it.id == contacto.id }) {
                                            grupoViewModel.quitarMiembro(grupo.id, contacto.id)
                                        }
                                    }

                                    contactoViewModel.eliminarContacto(contacto)

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Contacto eliminado")
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
                                    contactoViewModel.marcarFavorito(contacto, !contacto.favorito)
                                },
                                onEliminarContacto = { contacto ->
                                    soundPool.play(sonidoBorrar, 1f, 1f, 1, 0, 1f)

                                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(80)
                                    }

                                    for (grupo in gruposConContactos) {
                                        if (grupo.miembros.any { it.id == contacto.id }) {
                                            grupoViewModel.quitarMiembro(grupo.id, contacto.id)
                                        }
                                    }

                                    contactoViewModel.eliminarContacto(contacto)

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Contacto eliminado")
                                    }
                                }
                            )
                        }
                        composable(Pantalla.Grupos.ruta) {
                            PantallaGrupos(
                                navController = navController,
                                grupos = gruposConContactos,
                                tabActual = Pantalla.Grupos
                            )
                        }

                        composable(Pantalla.CrearGrupo.ruta) {
                            PantallaCrearGrupo(
                                contactosDisponibles = contactos,
                                onCancelar = { navController.popBackStack() },
                                onCrear = { nuevoGrupo, miembros ->
                                    grupoViewModel.crearGrupo(nuevoGrupo, miembros)

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

                            val grupo = gruposConContactos.firstOrNull { it.id == grupoId }

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

                            val grupo = gruposConContactos.firstOrNull { it.id == grupoId }

                            if (grupo != null) {
                                PantallaLlamadaGrupal(
                                    grupo = grupo,
                                    onCerrar = { navController.popBackStack() },
                                    onIniciarLlamada = { _ ->
                                        // El Toast "Llamando a..." ya se muestra dentro de
                                        // PantallaLlamadaGrupal al presionar "Iniciar llamada".
                                        // TODO: conectar con la lógica real de llamada grupal (WebRTC, etc.)
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
    }
}