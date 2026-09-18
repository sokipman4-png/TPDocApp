package com.tpdoc.app

import com.tpdoc.app.data.export.JsonCodec
import com.tpdoc.app.data.room.Perusahaan
import org.junit.Assert.*
import org.junit.Test

class JsonCodecTest {

    private fun sample(): List<Perusahaan> = listOf(
        Perusahaan(id = 1, nama = "PT Alpha", npwp = "01.111.222.3-444.555", alamat = "Jl. Industri 5", negara = "Indonesia", status = Perusahaan.STATUS_INDUK, tahunPajak = 2024, logoPath = null),
        Perusahaan(id = 2, nama = "PT Beta", npwp = "02.222.333.4-555.666", alamat = "Jl. Komers 9", negara = "Singapur", status = Perusahaan.STATUS_ANAK, parentId = 1L, tahunPajak = 2025, logoPath = "/tmp/logo.png"),
    )

    @Test
    fun `encode puis decode round-trip`() {
        val encoded = JsonCodec.encode(sample())
        val decoded = JsonCodec.decode(encoded)
        assertEquals(2, decoded.size)
        val a = decoded[0]
        assertEquals(1, a.id)
        assertEquals("PT Alpha", a.nama)
        assertEquals("01.111.222.3-444.555", a.npwp)
        assertEquals(Perusahaan.STATUS_INDUK, a.status)
        assertNull(a.parentId)
        assertEquals(2024, a.tahunPajak)
        assertNull(a.logoPath)

        val b = decoded[1]
        assertEquals(2, b.id)
        assertEquals("PT Beta", b.nama)
        assertEquals(Perusahaan.STATUS_ANAK, b.status)
        assertEquals(1L, b.parentId)
        assertEquals(2025, b.tahunPajak)
        assertEquals("/tmp/logo.png", b.logoPath)
    }

    @Test
    fun `encode empty list`() {
        assertEquals("[]", JsonCodec.encode(emptyList()))
        assertTrue(JsonCodec.decode("[]").isEmpty())
    }

    @Test
    fun `json string escaping`() {
        val s = "PT \"Besar\" \\ & Otemba"
        val encoded = JsonCodec.jsonString(s)
        val decoded = JsonCodec.decode("[$encoded]")[0]
        assertEquals(s, decoded.nama)
    }

    @Test
    fun `decode invalid json throws`() {
        try {
            JsonCodec.decode("not json")
            fail("Wajib throw exception")
        } catch (e: RuntimeException) {
            assertTrue(true)
        }
    }

    @Test
    fun `decode missing fields usai default`() {
        val decoded = JsonCodec.decode("""[{"nama": "PT Solo"}]""")
        assertEquals(1, decoded.size)
        val p = decoded[0]
        assertEquals("PT Solo", p.nama)
        assertEquals("", p.npwp)
        assertEquals("Indonesia", p.negara)
        assertEquals(Perusahaan.STATUS_INDUK, p.status)
        assertEquals(2024, p.tahunPajak)
    }

    @Test
    fun `nama dengan karakter unicode`() {
        val original = Perusahaan(nama = "PT Máju Jaya", npwp = "1")
        val roundTrip = JsonCodec.decode(JsonCodec.encode(listOf(original)))[0]
        assertEquals("PT Máju Jaya", roundTrip.nama)
    }
}