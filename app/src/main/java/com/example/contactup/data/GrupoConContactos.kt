package com.example.contactup.data

import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GrupoConContactos(
    @Embedded val grupo: Grupo,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ContactoGrupoCrossRef::class,
            parentColumn = "grupoId",
            entityColumn = "contactoId"
        )
    )
    val miembros: List<Contacto>
)

// Accesos directos, para no tener que escribir "grupo.grupo.nombre" en las pantallas
val GrupoConContactos.id: Int get() = grupo.id
val GrupoConContactos.nombre: String get() = grupo.nombre
val GrupoConContactos.descripcion: String get() = grupo.descripcion
val GrupoConContactos.color: Color get() = grupo.color