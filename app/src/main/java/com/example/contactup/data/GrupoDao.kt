package com.example.contactup.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GrupoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGrupo(grupo: Grupo): Long

    @Delete
    suspend fun eliminarGrupo(grupo: Grupo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarMiembro(crossRef: ContactoGrupoCrossRef)

    @Query("DELETE FROM contacto_grupo_cross_ref WHERE grupoId = :grupoId AND contactoId = :contactoId")
    suspend fun quitarMiembro(grupoId: Int, contactoId: Int)

    @Transaction
    @Query("SELECT * FROM grupos ORDER BY nombre ASC")
    fun obtenerGruposConContactos(): Flow<List<GrupoConContactos>>

    @Transaction
    @Query("SELECT * FROM grupos WHERE id = :id")
    suspend fun obtenerGrupoPorId(id: Int): GrupoConContactos?
}