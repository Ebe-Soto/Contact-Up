package com.example.contactup.data

import kotlinx.coroutines.flow.Flow

class ContactoRepository(private val contactoDao: ContactoDao) {

    val todosLosContactos: Flow<List<Contacto>> = contactoDao.obtenerTodos()

    val contactosFavoritos: Flow<List<Contacto>> = contactoDao.obtenerFavoritos()

    suspend fun insertar(contacto: Contacto) {
        contactoDao.insertar(contacto)
    }

    suspend fun actualizar(contacto: Contacto) {
        contactoDao.actualizar(contacto)
    }

    suspend fun eliminar(contacto: Contacto) {
        contactoDao.eliminar(contacto)
    }

    suspend fun obtenerPorId(id: Int): Contacto? {
        return contactoDao.obtenerPorId(id)
    }
}