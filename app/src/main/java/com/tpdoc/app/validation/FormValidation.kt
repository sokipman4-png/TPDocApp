package com.tpdoc.app.validation

import com.tpdoc.app.data.room.Perusahaan

/**
 * Validasi form pure (JVM-testable) — pelajaran Tahap 2.6 BUG 1.
 *
 * RULE: setiap form wajib validasi input SEPERIAN save ke database:
 *  - Field kosong -> error (terkontrol, bukan crash)
 *  - Format salah (email, NPWP, angka) -> error jelas
 *  - Field wajib vs opsional ditandai dengan jelas
 * ViewModel memanggil fungsi ni; error ditampilkan inline di bawah field.
 */
data class FormFieldError(
    val field: String,
    val message: String,
) {
    companion object {
        const val FIELD_NAMA = "nama"
        const val FIELD_NPWP = "npwp"
        const val FIELD_STATUS = "status"
        const val FIELD_TAHUN_PAJAK = "tahunPajak"
        const val FIELD_PARENT = "parentId"
    }
}

object FormValidation {

    const val TAHUN_MIN = 2000
    const val TAHUN_MAX = 2100
    const val NAMA_MAX = 120
    const val NPWP_UNKNOWN = "-"

    /** NPWP opsional; jika diisi, sifra + pemisah '.' atau '-' (tanda tahun pajak). */
    private val NPWP_REGEX = Regex("^[0-9.\\-– ]+$")

    fun validatePerusahaan(
        nama: String,
        npwp: String,
        status: String,
        tahunPajakText: String,
    ): List<FormFieldError> {
        val errors = ArrayList<FormFieldError>()

        val namaTrim = nama.trim()
        if (namaTrim.isBlank()) {
            errors.add(FormFieldError(FormFieldError.FIELD_NAMA, "Nama perusahaan wajib diisi"))
        } else if (namaTrim.length > NAMA_MAX) {
            errors.add(FormFieldError(FormFieldError.FIELD_NAMA, "Nama terlalu panjang (maksimal $NAMA_MAX karakter)"))
        }

        val npwpTrim = npwp.trim()
        if (npwpTrim.isNotBlank() && npwpTrim != NPWP_UNKNOWN) {
            if (!NPWP_REGEX.containsMatchIn(npwpTrim)) {
                errors.add(
                    FormFieldError(
                        FormFieldError.FIELD_NPWP,
                        "Format NPWP tidak valid. Contoh: 01.234.567.8-901.000",
                    ),
                )
            } else if (npwpTrim.count { it.isDigit() } < 4) {
                errors.add(
                    FormFieldError(
                        FormFieldError.FIELD_NPWP,
                        "NPWP harus mengandung minimal 4 sifra",
                    ),
                )
            }
        }

        if (status.isBlank() || status !in Perusahaan.STATUSES) {
            errors.add(FormFieldError(FormFieldError.FIELD_STATUS, "Status tidak valid"))
        }

        val tahunErr = validateTahunPajak(tahunPajakText)
        if (tahunErr != null) {
            errors.add(FormFieldError(FormFieldError.FIELD_TAHUN_PAJAK, tahunErr))
        }

        return errors
    }

    fun validateTahunPajak(text: String): String? {
        val t = text.trim()
        if (t.isBlank()) return "Tahun pajak wajib diisi"
        val year = t.toIntOrNull()
        if (year == null) return "Tahun pajak harus sifra (mis.: 2024)"
        if (year < TAHUN_MIN || year > TAHUN_MAX) {
            return "Tahun pajak antara $TAHUN_MIN–$TAHUN_MAX"
        }
        return null
    }

    /** NPWP merupakan nilai unik kelewati --id; null = OK (tersedia), Perusahaan = duplikat. */
    fun findDuplicateNpwp(npwp: String, excludeId: Long, existing: List<Perusahaan>): Perusahaan? {
        val npwpTrim = npwp.trim()
        if (npwpTrim.isBlank() || npwpTrim == NPWP_UNKNOWN) return null
        return existing.firstOrNull { it.npwp.trim() == npwpTrim && it.id != excludeId }
    }

    fun isWajib(nama: String): Boolean = nama == FormFieldError.FIELD_NAMA || nama == FormFieldError.FIELD_TAHUN_PAJAK
}