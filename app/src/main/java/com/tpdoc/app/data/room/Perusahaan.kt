package com.tpdoc.app.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "perusahaan",
    foreignKeys = [
        ForeignKey(
            entity = Perusahaan::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("parentId"), Index("npwp"), Index("nama")],
)
data class Perusahaan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val npwp: String,
    val alamat: String = "",
    val negara: String = "Indonesia",
    val status: String = STATUS_INDUK,  // induk, anak, cucu, cabang
    val parentId: Long? = null,
    val tahunPajak: Int = 2024,
    val logoPath: String? = null,
) {
    companion object {
        const val STATUS_INDUK = "induk"
        const val STATUS_ANAK = "anak"
        const val STATUS_CUCU = "cucu"
        const val STATUS_CABANG = "cabang"

        val STATUSES = listOf(STATUS_INDUK, STATUS_ANAK, STATUS_CUCU, STATUS_CABANG)
    }
}