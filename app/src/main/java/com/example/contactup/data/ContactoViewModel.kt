package com.example.contactup.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactoViewModel(private val repository: ContactoRepository) : ViewModel() {

    val todosLosContactos: StateFlow<List<Contacto>> = repository.todosLosContactos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val contactosFavoritos: StateFlow<List<Contacto>> = repository.contactosFavoritos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarContacto(nombre: String, telefono: String, correo: String) {
        viewModelScope.launch {
            repository.insertar(Contacto(nombre = nombre, telefono = telefono, correo = correo))
        }
    }

    fun actualizarContacto(contacto: Contacto) {
        viewModelScope.launch {
            repository.actualizar(contacto)
        }
    }

    fun eliminarContacto(contacto: Contacto) {
        viewModelScope.launch {
            repository.eliminar(contacto)
        }
    }

    fun marcarFavorito(contacto: Contacto, esFavorito: Boolean) {
        viewModelScope.launch {
            repository.actualizar(contacto.copy(favorito = esFavorito))
        }
    }

    class Factory(private val repository: ContactoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactoViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}