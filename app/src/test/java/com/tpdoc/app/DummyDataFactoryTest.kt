package com.tpdoc.app

import com.tpdoc.app.data.dummy.DummyDataFactory
import com.tpdoc.app.data.room.Perusahaan
import org.junit.Assert.*
import org.junit.Test

/**
 * Test data dummy (STEP 4 Tahap 2.6):
 * generate -> 3 bedrijven in hiërarchie induk-anak-cucu, elk 2 belastingjaren
 * (2024, 2025), alle records isDummy=true, velden volledig ingevuld,
 * logo placeholder is een geldig BMP.
 */
class DummyDataFactoryTest {

    private val rows = DummyDataFactory.build()

    @Test
    fun `generate levert 3 bedrijven met 2 jaar - 6 records`() {
        assertEquals(6, rows.size)
        assertEquals(3, DummyDataFactory.countDistinctNama(rows))
        assertEquals(
            listOf(2024, 2025),
            rows.map { it.perusahaan.tahunPajak }.distinct().sorted(),
        )
    }

    @Test
    fun `alle records hebben isDummy flag true`() {
        assertTrue(rows.all { it.perusahaan.isDummy })
    }

    @Test
    fun `hiërarchie keys correct - parent voor child in lijst`() {
        assertTrue(DummyDataFactory.isHierarchyValid(rows))
        // Induk heeft geen parent; anak wijst naar holding; cucu naar jaya
        val holding2024 = rows.find { it.key == "holding|2024" }
        val jaya2024 = rows.find { it.key == "jaya|2024" }
        val intl2024 = rows.find { it.key == "internasional|2024" }
        assertNull(holding2024?.parentKey)
        assertEquals("holding|2024", jaya2024?.parentKey)
        assertEquals("jaya|2024", intl2024?.parentKey)
    }

    @Test
    fun `status van elke rij klopt met de hiërarchie`() {
        rows.filter { it.perusahaan.nama == DummyDataFactory.HOLDING }
            .forEach { assertTrue(it.perusahaan.status == Perusahaan.STATUS_INDUK) }
        rows.filter { it.perusahaan.nama == DummyDataFactory.JAYA }
            .forEach { assertTrue(it.perusahaan.status == Perusahaan.STATUS_ANAK) }
        rows.filter { it.perusahaan.nama == DummyDataFactory.INTERNASIONAL }
            .forEach { assertTrue(it.perusahaan.status == Perusahaan.STATUS_CUCU) }
    }

    @Test
    fun `velden ingevuld - nama npwp alamat negara niet leeg`() {
        rows.forEach { r ->
            assertFalse(r.perusahaan.nama.isBlank())
            assertFalse(r.perusahaan.npwp.isBlank())
            assertFalse(r.perusahaan.alamat.isBlank())
            assertFalse(r.perusahaan.negara.isBlank())
        }
        // NPWP uniek per bedrijf
        assertEquals(3, rows.map { it.perusahaan.npwp }.distinct().size)
    }

    @Test
    fun `logo placeholder is geldig BMP bestand`() {
        val bmp = DummyDataFactory.logoBmp(DummyDataFactory.HOLDING)
        assertTrue(DummyDataFactory.isBmp(bmp))
        assertEquals(0x42, bmp[0].toInt()) // 'B'
        assertEquals(0x4D, bmp[1].toInt()) // 'M'
        // 16x16x3 + 54 header
        assertTrue(bmp.size >= 54 + 16 * 16 * 3)
    }
}