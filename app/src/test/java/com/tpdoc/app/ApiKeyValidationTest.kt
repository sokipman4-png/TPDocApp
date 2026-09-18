package com.tpdoc.app

import org.junit.Assert.*
import org.junit.Test

class ApiKeyValidationTest {

    @Test
    fun `valid api key format sk-or-v1`() {
        assertTrue(isValidFormat("sk-or-v1-abc123def456"))
    }

    @Test
    fun `invalid api key format random`() {
        assertFalse(isValidFormat("random-key"))
    }

    @Test
    fun `empty api key is invalid`() {
        assertFalse(isValidFormat(""))
    }

    @Test
    fun `short api key is invalid`() {
        assertFalse(isValidFormat("sk-or"))
    }

    @Test
    fun `case sensitive check`() {
        assertFalse(isValidFormat("SK-OR-V1-abc"))
    }

    private fun isValidFormat(key: String): Boolean {
        return key.startsWith("sk-or-v1-") && key.length > 10
    }
}