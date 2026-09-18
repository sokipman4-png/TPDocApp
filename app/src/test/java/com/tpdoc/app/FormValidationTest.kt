package com.tpdoc.app

import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.validation.FormFieldError
import com.tpdoc.app.validation.FormValidation
import org.junit.Assert.*
import org.junit.Test

/**
 * Test form-validation (BUG 1 fix Tahap 2.6):
 * simpen met volledige/lege/gedeeltelijke invoer produceert GEEN crash maar
 * gecontroleerde fouten; duplicaat-NPWP wordt afgewezen vóór DB-schrijven.
 */
class FormValidationTest {

    private val VALID_NPWP = "01.234.567.8-901.000"

    private fun errors(
        nama: String = "PT Test",
        npwp: String = VALID_NPWP,
        status: String = Perusahaan.STATUS_INDUK,
        tahunPajak: String = "2024",
    ): List<FormFieldError> =
        FormValidation.validatePerusahaan(nama, npwp, status, tahunPajak)

    @Test
    fun `simpan dengan input lengkap - geen error`() {
        assertTrue(errors().isEmpty())
    }

    @Test
    fun `simpan dengan input kosong - error terkontrol nama en tahun`() {
        val errs = errors(nama = "", npwp = "", tahunPajak = "")
        assertTrue("wajib nama error", errs.any { it.field == FormFieldError.FIELD_NAMA })
        assertTrue("wajib tahun error", errs.any { it.field == FormFieldError.FIELD_TAHUN_PAJAK })
        // NPWP leeg = optioneel, geen npwp error
        assertFalse(errs.any { it.field == FormFieldError.FIELD_NPWP })
    }

    @Test
    fun `simpan dengan input partial - alleen ontbrekende velden error`() {
        // Nama aanwezig, jaar ontbreekt -> alleen jaar-error
        val errs = errors(tahunPajak = "")
        assertEquals(1, errs.size)
        assertEquals(FormFieldError.FIELD_TAHUN_PAJAK, errs.first().field)
    }

    @Test
    fun `npwp format fout toont error duidelijk`() {
        val errs = errors(npwp = "ABC-DEF")
        assertTrue(errs.any { it.field == FormFieldError.FIELD_NPWP })
        assertTrue(errs.any { it.message.contains("NPWP") })
    }

    @Test
    fun `npwp te weinig sifra toont error`() {
        val errs = errors(npwp = "12")
        assertTrue(errs.any { it.field == FormFieldError.FIELD_NPWP })
    }

    @Test
    fun `tahun pajak geen sifra toont error`() {
        assertNotNull(FormValidation.validateTahunPajak("abc"))
    }

    @Test
    fun `tahun pajak buiten bereik toont error`() {
        assertNotNull(FormValidation.validateTahunPajak("1999"))
        assertNotNull(FormValidation.validateTahunPajak("2101"))
        assertNotNull(FormValidation.validateTahunPajak(""))
    }

    @Test
    fun `tahun pajak geldig geen error`() {
        assertNull(FormValidation.validateTahunPajak("2025"))
        assertNull(FormValidation.validateTahunPajak(" 2026 ")) // trim
    }

    @Test
    fun `status ongeldig toont error`() {
        val errs = errors(status = "niet-bestaat")
        assertTrue(errs.any { it.field == FormFieldError.FIELD_STATUS })
    }

    @Test
    fun `naam te lang toont error`() {
        val errs = errors(nama = "X".repeat(121))
        assertTrue(errs.any { it.field == FormFieldError.FIELD_NAMA })
    }

    // ---- NPWP duplicaat (gecontroleerde afwijzing, geen crash) ----

    @Test
    fun `npwp duplicaat gevonden in bestaande lijst`() {
        val existing = listOf(
            Perusahaan(id = 1, nama = "PT Bestaand", npwp = VALID_NPWP),
            Perusahaan(id = 2, nama = "PT Ander", npwp = "-"),
        )
        val dup = FormValidation.findDuplicateNpwp(VALID_NPWP, 0, existing)
        assertNotNull(dup)
        assertEquals("PT Bestaand", dup?.nama)
    }

    @Test
    fun `npwp duplicaat wordt genegeerd voor dezelfde id (edit)`() {
        val existing = listOf(Perusahaan(id = 7, nama = "PT Zelfde", npwp = VALID_NPWP))
        assertNull(FormValidation.findDuplicateNpwp(VALID_NPWP, 7, existing))
    }

    @Test
    fun `npwp placeholder leeg en streepje nooit duplicaat`() {
        val existing = listOf(Perusahaan(id = 1, nama = "PT A", npwp = "-"))
        assertNull(FormValidation.findDuplicateNpwp("", 0, existing))
        assertNull(FormValidation.findDuplicateNpwp("-", 0, existing))
    }
}