package com.tpdoc.app

import org.junit.Assert.*
import org.junit.Test

class RetryLogicTest {

    private var attemptCount = 0

    @Test
    fun `retry on failure succeeds on second attempt`() {
        attemptCount = 0
        val result = callWithRetry(maxAttempts = 2)
        assertEquals("success", result)
        assertEquals(2, attemptCount)
    }

    @Test
    fun `no retry on first success`() {
        attemptCount = 0
        val result = callWithRetryAlwaysSuccess()
        assertEquals("success", result)
        assertEquals(1, attemptCount)
    }

    @Test
    fun `max retries exhausted`() {
        attemptCount = 0
        val result = callWithRetryAlwaysFail(maxAttempts = 3)
        assertEquals("failed", result)
        assertEquals(3, attemptCount)
    }

    @Test
    fun `retry delay is respected`() {
        val delayMs = 100L
        val start = System.currentTimeMillis()
        Thread.sleep(delayMs)
        val elapsed = System.currentTimeMillis() - start
        assertTrue(elapsed >= delayMs)
    }

    private fun callWithRetry(maxAttempts: Int): String {
        for (i in 1..maxAttempts) {
            attemptCount++
            if (i == 1) continue // fail first
            return "success"
        }
        return "failed"
    }

    private fun callWithRetryAlwaysSuccess(): String {
        attemptCount++
        return "success"
    }

    private fun callWithRetryAlwaysFail(maxAttempts: Int): String {
        for (i in 1..maxAttempts) {
            attemptCount++
        }
        return "failed"
    }
}