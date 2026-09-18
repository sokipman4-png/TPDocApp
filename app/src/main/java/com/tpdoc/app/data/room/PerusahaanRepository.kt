package com.tpdoc.app.data.room

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PerusahaanRepository(context: Context) {

    private val dao = AppDatabase.get(context).perusahaanDao()

    val semua: Flow<List<Perusahaan>> = dao.observeAll()
    val induk: Flow<List<Perusahaan>> = dao.observeInduk()
    val semuaNegara: Flow<List<String>> = dao.observeAllNegara()

    fun byId(id: Long): Flow<Perusahaan?> = dao.observeById(id)

    suspend fun getById(id: Long): Perusahaan? = dao.getById(id)

    suspend fun getAll(): List<Perusahaan> = dao.getAll()

    fun search(query: String): Flow<List<Perusahaan>> = dao.search(query)

    fun filter(status: String?, negara: String?): Flow<List<Perusahaan>> = dao.filter(status, negara)

    suspend fun count(): Int = dao.count()

    suspend fun insert(perusahaan: Perusahaan): Long {
        val p = if (perusahaan.npwp.isBlank()) perusahaan.copy(npwp = "-") else perusahaan
        return dao.insert(p)
    }

    suspend fun update(perusahaan: Perusahaan) {
        val p = if (perusahaan.npwp.isBlank()) perusahaan.copy(npwp = "-") else perusahaan
        dao.update(p)
    }

    suspend fun delete(perusahaan: Perusahaan) = dao.delete(perusahaan)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun anakByParent(parentId: Long): Flow<List<Perusahaan>> = dao.observeAnakByParent(parentId)
}