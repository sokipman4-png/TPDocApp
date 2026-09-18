package com.tpdoc.app.data.export

/** Validasi logo upload: JPG/PNG saja, max 5 MB. Pure & unit-testable. */
object LogoValidator {

    const val MAX_LOGO_BYTES = 5L * 1024 * 1024  // 5 MB

    private val allowedTypes = setOf("image/jpeg", "image/png")

    /** Periksa MIME type diizinkan. */
    fun isValidMime(mimeType: String?): Boolean {
        return mimeType != null && mimeType.lowercase() in allowedTypes
    }

    /** Periksa ukuran file. */
    fun isValidSize(sizeBytes: Long): Boolean {
        return sizeBytes in 1..MAX_LOGO_BYTES
    }

    fun validate(mimeType: String?, sizeBytes: Long): String? {
        if (mimeType == null || mimeType.isBlank()) {
            return "File tidak memiliki tipe. Pilih file JPG atau PNG."
        }
        if (!isValidMime(mimeType)) {
            return "Format file tidak didukung. Hanya JPG dan PNG yang diperbolehkan."
        }
        if (!isValidSize(sizeBytes)) {
            return "Ukuran file melebihi 5 MB. Pilih file yang lebih kecil."
        }
        return null
    }
}