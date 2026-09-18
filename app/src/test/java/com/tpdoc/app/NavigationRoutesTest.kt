package com.tpdoc.app

import com.tpdoc.app.navigation.Routes
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class NavigationRoutesTest {

    @Test
    fun `semua tab routes didaflar di statics`() {
        Routes.TABS.forEach { route ->
            assertTrue("Tab $route wajib didaflar", route in Routes.STATICS)
        }
    }

    @Test
    fun `semua tab routes ada di all patterns`() {
        Routes.TABS.forEach { route ->
            assertTrue("Tab $route wajib di ALL_PATTERNS", route in Routes.ALL_PATTERNS)
        }
    }

    @Test
    fun `rute patterns unik - tidak ada duplikat`() {
        val patterns = Routes.ALL_PATTERNS.toList()
        assertEquals("Pattern wajib unik", patterns.size, patterns.distinct().size)
    }

    @Test
    fun `start destination adalah daftar perusahaan`() {
        // Pastikan start route didaflar di NavHost (DaftarPerusahaan adalah tab)
        assertTrue("Start route wajib tab", Routes.DAFTAR_PERUSAHAAN in Routes.TABS)
    }

    @Test
    fun `builder rute detail perusahaan matches pattern`() {
        val route = Routes.detailPerusahaan(42)
        assertEquals("detail_perusahaan/42", route)
        assertTrue(isPatternMatch(Routes.DETAIL_PERUSAHAAN, route))
    }

    @Test
    fun `builder rute form perusahaan matches pattern`() {
        val route = Routes.formPerusahaan(7)
        assertTrue(isPatternMatch(Routes.FORM_PERUSAHAAN, route))
        // Form kosong = create
        assertTrue(isPatternMatch(Routes.FORM_PERUSAHAAN, Routes.formPerusahaan(null)))
    }

    @Test
    fun `builder rute analisis matches pattern`() {
        val route = Routes.analisis(3)
        assertEquals("analisis/3", route)
        assertTrue(isPatternMatch(Routes.ANALISIS, route))
    }

    @Test
    fun `isTab berfungsi untuk tab en dah untuk detail`() {
        assertTrue(Routes.isTab(Routes.DAFTAR_PERUSAHAAN))
        assertTrue(Routes.isTab(Routes.HOME))
        assertFalse(Routes.isTab(Routes.DASHBOARD))
        assertFalse(Routes.isTab("detail_perusahaan/1"))
    }

    @Test
    fun `setiap konstanta rute didaftarkan persis satu kali di NavHost source`() {
        val registered = buildSet {
            Regex("composable\\s*\\(\\s*(?:route\\s*=\\s*)?Routes\\.([A-Z_]+)")
                .findAll(navHostSource())
                .forEach { m -> add(m.groupValues[1]) }
        }.toSortedSet()

        val defined = buildSet {
            Regex("const\\s+val\\s+([A-Z_]+)\\s*=")
                .findAll(routesSource())
                .forEach { m -> add(m.groupValues[1]) }
        }.toSortedSet()

        // AppNavHost wajib registreri TUTTI konstanta rute Routes, dan tidak ada registrasi asing.
        assertEquals(
            "Semua konstanta Routes wajib didaftarkan di NavHost (regresi navigasi)",
            defined,
            registered,
        )
    }

    @Test
    fun `start destination adalah rute statis yang didaftarkan`() {
        val navHost = navHostSource()
        assertTrue(
            "Start destination wajib didaftarkan lewat composable()",
            Regex("composable\\s*\\(\\s*(?:route\\s*=\\s*)?Routes\\.(?:DAFTAR_PERUSAHAAN)").containsMatchIn(navHost),
        )
    }

    private fun navHostSource(): String {
        val file = sourceFile(
            listOf(
                "src/main/java/com/tpdoc/app/navigation/AppNavHost.kt",
                "app/src/main/java/com/tpdoc/app/navigation/AppNavHost.kt",
            ),
        )
        return file.readText()
    }

    private fun routesSource(): String {
        val file = sourceFile(
            listOf(
                "src/main/java/com/tpdoc/app/navigation/Routes.kt",
                "app/src/main/java/com/tpdoc/app/navigation/Routes.kt",
            ),
        )
        return file.readText()
    }

    private fun sourceFile(candidates: List<String>): File {
        val found = candidates.mapNotNull { File(it) }.firstOrNull { it.exists() }
        if (found != null) return found
        fail("File source tidak ditemukan: $candidates")
        throw IllegalStateException("unreachable")
    }

    private fun isPatternMatch(pattern: String, route: String): Boolean {
        val patternParts = pattern.split("/")
        val routeParts = route.split("/")
        if (patternParts.size != routeParts.size) return false
        for (i in 0..patternParts.size - 1) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) continue
            if (patternParts[i] != routeParts[i]) return false
        }
        return true
    }
}