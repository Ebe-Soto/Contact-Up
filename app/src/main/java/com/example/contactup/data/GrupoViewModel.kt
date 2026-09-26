package com.example.contactup.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GrupoViewModel(private val repository: GrupoRepository) : ViewModel() {

    val gruposConContactos: StateFlow<List<GrupoConContactos>> = repository.gruposConContactos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun crearGrupo(grupo: Grupo, miembros: List<Contacto>) {
        viewModelScope.launch {
            repository.crearGrupo(grupo, miembros)
        }
    }

    fun eliminarGrupo(grupo: Grupo) {
        viewModelScope.launch {
            repository.eliminarGrupo(grupo)
        }
    }

    class Factory(private val repository: GrupoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GrupoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GrupoViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}