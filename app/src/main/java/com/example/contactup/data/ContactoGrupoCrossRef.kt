package com.example.contactup.data

import androidx.room.Entity

@Entity(
    tableName = "contacto_grupo_cross_ref",
    primaryKeys = ["contactoId", "grupoId"]
)
data class ContactoGrupoCrossRef(
    val contactoId: Int,
    val grupoId: Int
)