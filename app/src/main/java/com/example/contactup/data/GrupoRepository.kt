package com.example.contactup.data

import kotlinx.coroutines.flow.Flow

class GrupoRepository(private val grupoDao: GrupoDao) {

    val gruposConContactos: Flow<List<GrupoConContactos>> = grupoDao.obtenerGruposConContactos()

    suspend fun crearGrupo(grupo: Grupo, miembros: List<Contacto>): Long {
        val grupoId = grupoDao.insertarGrupo(grupo)
        miembros.forEach { contacto ->
            grupoDao.agregarMiembro(
                ContactoGrupoCrossRef(contactoId = contacto.id, grupoId = grupoId.toInt())
            )
        }
        return grupoId
    }

    suspend fun eliminarGrupo(grupo: Grupo) {
        grupoDao.eliminarGrupo(grupo)
    }

    suspend fun agregarMiembro(grupoId: Int, contactoId: Int) {
        grupoDao.agregarMiembro(ContactoGrupoCrossRef(contactoId = contactoId, grupoId = grupoId))
    }

    suspend fun quitarMiembro(grupoId: Int, contactoId: Int) {
        grupoDao.quitarMiembro(grupoId, contactoId)
    }

    suspend fun obtenerGrupoPorId(id: Int): GrupoConContactos? {
        return grupoDao.obtenerGrupoPorId(id)
    }
}