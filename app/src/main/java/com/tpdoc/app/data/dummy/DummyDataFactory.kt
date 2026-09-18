package com.tpdoc.app.data.dummy

import com.tpdoc.app.data.room.Perusahaan

/**
 * Factory data contoh (dummy) — pure, JVM-testable.
 *
 * Tujuan (STEP 4 Tahap 2.6): memudahkan user test aplikasi.
 * Generate 3 perusahaan dalam hierarki induk-anak-cucu, masing-masing met
 * 2 tahun pajak (2024, 2025):
 *   - PT Nusantara Holding (induk, Jakarta, omzet grup Rp60 M)
 *   - PT Nusantara Jaya (anak dari Holding, Surabaya)
 *   - PT Nusantara Internasional (cucu dari Jaya, Singapura)
 *
 * Data tipluis isDummy=true zodat het nooit met echte userdata gemengd wordt
 * (Hapus Data Contoh verwijdert alleen isDummy=true rijen).
 *
 * Parent-koppelingen gaan via key in plaats van database-id zodat deze fabriek
 * unit-testbaar is; de ViewModel zet keys om naar echte id's tijdens insert.
 */
data class DummyRow(
    val key: String,
    val parentKey: String?,
    val perusahaan: Perusahaan,
)

object DummyDataFactory {

    const val HOLDING = "PT Nusantara Holding"
    const val JAYA = "PT Nusantara Jaya"
    const val INTERNASIONAL = "PT Nusantara Internasional"

    const val NPWP_HOLDING = "01.234.567.8-901.000"
    const val NPWP_JAYA = "02.345.678.9-012.000"
    const val NPWP_INTERNASIONAL = "03.456.789.0-123.000"

    const val ALAMAT_HOLDING = "Jl. Kewajaran No. 1, Distrik Bisnis, Jakarta"
    const val ALAMAT_JAYA = "Jl. Afiliasi No. 2, Surabaya, Indonesia"
    const val ALAMAT_INTERNASIONAL = "1 Marina Boulevard, Singapore"

    const val NEGARA_HOLDING = "Indonesia"
    const val NEGARA_JAYA = "Indonesia"
    const val NEGARA_INTERNASIONAL = "Singapura"

    /** 2 tahun pajak per perusahaan (STEP 4.b). */
    val TAHUN_LIST = listOf(2024, 2025)

    /**
     * Build 6 rijen geordend (parent vóór child) zodat de ViewModel de echte
     * id's kan doorgeven. Gewaarborgd: parentKey van elke rij verwijst naar een
     * key die eerder in de lijst staat.
     */
    fun build(): List<DummyRow> {
        val rows = ArrayList<DummyRow>()
        TAHUN_LIST.forEach { tahun ->
            rows.add(
                DummyRow(
                    key = "holding|$tahun",
                    parentKey = null,
                    perusahaan = Perusahaan(
                        nama = HOLDING,
                        npwp = NPWP_HOLDING,
                        alamat = ALAMAT_HOLDING,
                        negara = NEGARA_HOLDING,
                        status = Perusahaan.STATUS_INDUK,
                        parentId = null,
                        tahunPajak = tahun,
                        isDummy = true,
                    ),
                ),
            )
            rows.add(
                DummyRow(
                    key = "jaya|$tahun",
                    parentKey = "holding|$tahun",
                    perusahaan = Perusahaan(
                        nama = JAYA,
                        npwp = NPWP_JAYA,
                        alamat = ALAMAT_JAYA,
                        negara = NEGARA_JAYA,
                        status = Perusahaan.STATUS_ANAK,
                        parentId = null, // set via parentKey
                        tahunPajak = tahun,
                        isDummy = true,
                    ),
                ),
            )
            rows.add(
                DummyRow(
                    key = "internasional|$tahun",
                    parentKey = "jaya|$tahun",
                    perusahaan = Perusahaan(
                        nama = INTERNASIONAL,
                        npwp = NPWP_INTERNASIONAL,
                        alamat = ALAMAT_INTERNASIONAL,
                        negara = NEGARA_INTERNASIONAL,
                        status = Perusahaan.STATUS_CUCU,
                        parentId = null, // set via parentKey
                        tahunPajak = tahun,
                        isDummy = true,
                    ),
                ),
            )
        }
        return rows
    }

    /** Aantal verschillende bedrijven (niet rijen): 3. */
    fun countDistinctNama(rows: List<DummyRow>): Int =
        rows.map { it.perusahaan.nama }.distinct().size

    /** Alle parentKeys verwijzen naar bestaande keys eerder in de lijst. */
    fun isHierarchyValid(rows: List<DummyRow>): Boolean {
        val known = HashSet<String>()
        for (row in rows) {
            if (row.parentKey != null && row.parentKey !in known) {
                return false
            }
            known.add(row.key)
        }
        return true
    }

    // ---- Logo placeholder (BMP 16x16, kleur gehasht uit naam) ----

    private const val BMP_SIZE = 16

    /**
     * Genereer een eenvoudig gekleurd vierkant bitmap (BMP 24-bit) als logo
     * placeholder. Pure functie: geen I/O, wel unit-testbaar.
     */
    fun logoBmp(nama: String): ByteArray {
        val dataLen = BMP_SIZE * BMP_SIZE * 3
        val total = 54 + dataLen
        val bytes = ByteArray(total)

        // BMP header
        bytes[0] = 0x42.toByte() // 'B'
        bytes[1] = 0x4D.toByte() // 'M'
        putIntLE(bytes, 2, total)
        putIntLE(bytes, 6, 0)
        putIntLE(bytes, 10, 54)
        putIntLE(bytes, 14, 40)
        putIntLE(bytes, 18, BMP_SIZE)
        putIntLE(bytes, 22, BMP_SIZE)
        putShortLE(bytes, 26, 1)
        putShortLE(bytes, 28, 24)
        putIntLE(bytes, 30, 0)
        putIntLE(bytes, 34, dataLen)
        putIntLE(bytes, 38, 2835)
        putIntLE(bytes, 42, 2835)

        // Kleur gehasht uit naam -> altijd zelfde kleur per bedrijf
        val hash = nama.fold(0) { acc, c -> acc * 31 + c.code }
        var r = (hash * 7) % 256
        var g = (hash * 13 + 80) % 256
        var b = (hash * 29 + 40) % 256
        if (r < 0) r += 256
        if (g < 0) g += 256
        if (b < 0) b += 256

        // Pixel data: BMP = BGR, rijen van onder naar boven
        for (i in 0 until dataLen step 3) {
            val idx = 54 + i
            bytes[idx] = b.toByte()
            bytes[idx + 1] = g.toByte()
            bytes[idx + 2] = r.toByte()
        }
        return bytes
    }

    fun isBmp(bytes: ByteArray): Boolean =
        bytes.size >= 54 && bytes[0].toInt() == 0x42 && bytes[1].toInt() == 0x4D

    private fun putIntLE(bytes: ByteArray, offset: Int, value: Int) {
        for (i in 0..3) {
            bytes[offset + i] = ((value >>> (8 * i)) & 0xFF).toByte()
        }
    }

    private fun putShortLE(bytes: ByteArray, offset: Int, value: Int) {
        bytes[offset] = (value & 0xFF).toByte()
        bytes[offset + 1] = ((value >>> 8) & 0xFF).toByte()
    }
}