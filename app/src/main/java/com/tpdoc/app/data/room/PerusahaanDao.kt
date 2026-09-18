package com.tpdoc.app.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerusahaanDao {

    @Query("SELECT * FROM perusahaan ORDER BY nama")
    fun observeAll(): Flow<List<Perusahaan>>

    @Query("SELECT * FROM perusahaan WHERE id = :id")
    fun observeById(id: Long): Flow<Perusahaan?>

    @Query("SELECT * FROM perusahaan WHERE id = :id")
    suspend fun getById(id: Long): Perusahaan?

    @Query("SELECT * FROM perusahaan WHERE npwp = :npwp AND (:excludeId <= 0 OR id != :excludeId) LIMIT 1")
    suspend fun findByNpwp(npwp: String, excludeId: Long): Perusahaan?

    @Query("SELECT * FROM perusahaan WHERE parentId IS NULL ORDER BY nama")
    fun observeInduk(): Flow<List<Perusahaan>>

    @Query("SELECT * FROM perusahaan WHERE parentId = :parentId ORDER BY nama")
    fun observeAnakByParent(parentId: Long): Flow<List<Perusahaan>>

    @Query("SELECT * FROM perusahaan ORDER BY nama")
    suspend fun getAll(): List<Perusahaan>

    @Query(
        """SELECT * FROM perusahaan 
        WHERE nama LIKE '%' || :query || '%' 
           OR npwp LIKE '%' || :query || '%'
           OR negara LIKE '%' || :query || '%'
           OR status LIKE '%' || :query || '%'
        ORDER BY nama""",
    )
    fun search(query: String): Flow<List<Perusahaan>>

    @Query(
        """SELECT * FROM perusahaan 
        WHERE (:status IS NULL OR status = :status)
          AND (:negara IS NULL OR negara = :negara)
        ORDER BY nama""",
    )
    fun filter(status: String?, negara: String?): Flow<List<Perusahaan>>

    @Query("SELECT COUNT(*) FROM perusahaan")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(perusahaan: Perusahaan): Long

    @Update
    suspend fun update(perusahaan: Perusahaan)

    @Delete
    suspend fun delete(perusahaan: Perusahaan)

    @Query("DELETE FROM perusahaan WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM perusahaan")
    suspend fun deleteAll()

    @Query("SELECT * FROM perusahaan WHERE isDummy = 1")
    suspend fun getDummy(): List<Perusahaan>

    @Query("DELETE FROM perusahaan WHERE isDummy = 1")
    suspend fun deleteDummy()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Perusahaan>)

    @Query("SELECT DISTINCT negara FROM perusahaan ORDER BY negara")
    fun observeAllNegara(): Flow<List<String>>
}