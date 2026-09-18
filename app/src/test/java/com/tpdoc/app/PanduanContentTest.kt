package com.tpdoc.app

import com.tpdoc.app.data.content.PanduanContent
import org.junit.Assert.*
import org.junit.Test

/**
 * Test tutorial/panduan content (STEP 5.B Tahap 2.6):
 * 9 secties + FAQ 5..10 vragen, alle inhoud niet-leeg en consistent.
 */
class PanduanContentTest {

    @Test
    fun `panduan heeft 9 secties`() {
        assertEquals(9, PanduanContent.sections.size)
    }

    @Test
    fun `elke sectie heeft id judul en body niet leeg`() {
        PanduanContent.sections.forEach { s ->
            assertFalse(s.id.isBlank())
            assertFalse(s.judul.isBlank())
            assertFalse(s.body.isBlank())
        }
    }

    @Test
    fun `sectie titels uniek`() {
        assertEquals(
            PanduanContent.sections.size,
            PanduanContent.sections.map { it.judul }.distinct().size,
        )
    }

    @Test
    fun `alle 9 thema's aanwezig`() {
        val all = PanduanContent.sections.map { it.id }
        listOf("intro", "tambah", "hierarki", "kalkulator", "apikey", "ai", "export", "backup", "faq").forEach { id ->
            assertTrue("sectie $id ontbreekt", id in all)
        }
    }

    @Test
    fun `FAQ bevat 5 tot 10 vragen`() {
        assertTrue(PanduanContent.faq.size >= 5)
        assertTrue(PanduanContent.faq.size <= 10)
    }

    @Test
    fun `FAQ antwoorden niet leeg`() {
        PanduanContent.faq.forEach { f ->
            assertFalse(f.pertanyaan.isBlank())
            assertFalse(f.jawaban.isBlank())
        }
    }

    @Test
    fun `FAQ bevat veelgestelde onderwerpen`() {
        val q = PanduanContent.faq.map { it.pertanyaan.lowercase() }.joinToString("\n")
        assertTrue(q.contains("api"))
        assertTrue(q.contains("biya") || q.contains("kost"))
        assertTrue(q.contains("data"))
    }

    @Test
    fun `link openrouter aanwezig in apikey sectie`() {
        val apikeySection = PanduanContent.sections.find { it.id == "apikey" }
        assertNotNull(apikeySection)
        assertTrue(apikeySection?.bullets.joinToString("\n").contains("openrouter.ai"))
        assertTrue(PanduanContent.URL_OPENROUTER_KEYS.contains("openrouter.ai"))
    }
}