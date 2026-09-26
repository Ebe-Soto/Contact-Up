package com.example.contactup.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(contacto: Contacto)

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Delete
    suspend fun eliminar(contacto: Contacto)

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE favorito = 1 ORDER BY nombre ASC")
    fun obtenerFavoritos(): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Contacto?
}