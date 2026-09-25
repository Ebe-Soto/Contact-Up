package com.example.contactup.data

import com.example.contactup.telefonoComoCorreo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

fun RegistrarUser(
    nombre: String,
    telefono: String,
    contrasena: String,
    onResultado: (Boolean, String?) -> Unit
) {
    val correo = telefonoComoCorreo(telefono)

    Firebase.auth.createUserWithEmailAndPassword(correo, contrasena)
        .addOnSuccessListener { resultado ->
            val uid = resultado.user?.uid

            if (uid == null) {
                onResultado(false, "No se pudo obtener el UID del usuario")
                return@addOnSuccessListener
            }

            val datosUsuario = mapOf(
                "nombre" to nombre,
                "telefono" to telefono
            )

            Firebase.database
                .getReference("users/$uid")
                .setValue(datosUsuario)
                .addOnSuccessListener {

                    Firebase.auth.signOut()

                    onResultado(true, null)
                }
                .addOnFailureListener { e ->
                    onResultado(false, e.message)
                }
        }
        .addOnFailureListener { e ->
            onResultado(false, e.message)
        }
}

fun IniciarSesion(
    telefono: String,
    contrasena: String,
    onResultado: (Boolean, String?) -> Unit
) {
    val correo = telefonoComoCorreo(telefono)

    Firebase.auth.signInWithEmailAndPassword(correo, contrasena)
        .addOnSuccessListener {
            onResultado(true, null)
        }
        .addOnFailureListener { e ->
            onResultado(false, e.message)
        }
}

fun ObtenerDatosUsuario(
    onResultado: (String?, String?) -> Unit
) {
    val usuario = Firebase.auth.currentUser

    if (usuario == null) {
        onResultado(null, null)
        return
    }

    Firebase.database
        .getReference("users/${usuario.uid}")
        .get()
        .addOnSuccessListener { snapshot ->

            val nombre = snapshot.child("nombre")
                .getValue(String::class.java)

            val telefono = snapshot.child("telefono")
                .getValue(String::class.java)

            onResultado(nombre, telefono)
        }
        .addOnFailureListener {
            onResultado(null, null)
        }
}