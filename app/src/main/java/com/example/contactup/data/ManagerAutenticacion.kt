package com.example.contactup.data

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthManager(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private var verificationId: String? = null

    fun enviarCodigo(
        telefono: String,
        activity: Activity,
        onCodigoEnviado: () -> Unit,
        onVerificacionAutomatica: () -> Unit,
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                iniciarSesionConCredencial(credential, onVerificacionAutomatica, onError)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Error al verificar el número")
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                onCodigoEnviado()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(telefono)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun confirmarCodigo(
        codigo: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        val id = verificationId ?: return onError("No se ha enviado ningún código todavía")
        val credential = PhoneAuthProvider.getCredential(id, codigo)
        iniciarSesionConCredencial(credential, onExito, onError)
    }

    private fun iniciarSesionConCredencial(
        credential: PhoneAuthCredential,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { onExito() }
            .addOnFailureListener { onError(it.message ?: "No se pudo iniciar sesión") }
    }
}