package com.tpdoc.app

import com.tpdoc.app.data.export.LogoValidator
import org.junit.Assert.*
import org.junit.Test

/** Validasi tombol Upload Logo: format JPG/PNG + maksimum 5 MB. */
class LogoValidatorTest {

    @Test
    fun `jpg dan png diterima`() {
        assertNull(LogoValidator.validate("image/jpeg", 1024))
        assertNull(LogoValidator.validate("image/png", 1024))
    }

    @Test
    fun `format selain jpg dan png ditolak`() {
        assertNotNull(LogoValidator.validate("image/gif", 1024))
        assertNotNull(LogoValidator.validate("application/pdf", 1024))
        assertNotNull(LogoValidator.validate("text/plain", 1024))
        assertNotNull(LogoValidator.validate("image/webp", 1024))
    }

    @Test
    fun `ukuran lebih dari 5 MB ditolak`() {
        assertNotNull(LogoValidator.validate("image/jpeg", LogoValidator.MAX_LOGO_BYTES + 1))
    }

    @Test
    fun `ukuran persis 5 MB diterima`() {
        assertNull(LogoValidator.validate("image/jpeg", LogoValidator.MAX_LOGO_BYTES))
    }

    @Test
    fun `ukuran 0 atau negatif ditolak`() {
        assertNotNull(LogoValidator.validate("image/jpeg", 0))
        assertNotNull(LogoValidator.validate("image/jpeg", -1))
    }

    @Test
    fun `mime null atau kosong ditolak`() {
        assertNotNull(LogoValidator.validate(null, 1024))
        assertNotNull(LogoValidator.validate("", 1024))
        assertNotNull(LogoValidator.validate("   ", 1024))
    }

    @Test
    fun `mime tidak case sensitive`() {
        assertNull(LogoValidator.validate("IMAGE/PNG", 100))
        assertNull(LogoValidator.validate("Image/JPEG", 100))
    }
}