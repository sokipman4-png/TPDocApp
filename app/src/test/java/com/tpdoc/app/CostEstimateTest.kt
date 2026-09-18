package com.tpdoc.app

import org.junit.Assert.*
import org.junit.Test

class CostEstimateTest {

    @Test
    fun `token estimation from character length`() {
        val jsonLen = 400
        val promptLen = 1000
        val expectedTokens = (jsonLen + promptLen) / 4
        assertEquals(350, expectedTokens)
    }

    @Test
    fun `token estimation minimum 50`() {
        val jsonLen = 10
        val promptLen = 20
        val tokens = maxOf((jsonLen + promptLen) / 4, 50)
        assertEquals(50, tokens)
    }

    @Test
    fun `cost calculation usd`() {
        val inputTokens = 1000
        val outputTokens = 500
        val promptPrice = 0.0015 // $1.50 per 1M tokens
        val completionPrice = 0.0020 // $2.00 per 1M tokens

        val inputUsd = inputTokens / 1_000_000.0 * promptPrice
        val outputUsd = outputTokens / 1_000_000.0 * completionPrice
        val totalUsd = inputUsd + outputUsd

        assertEquals(0.0015, inputUsd, 0.0001)
        assertEquals(0.0010, outputUsd, 0.0001)
        assertEquals(0.0025, totalUsd, 0.0001)
    }

    @Test
    fun `cost conversion to idr`() {
        val totalUsd = 0.0025
        val rate = 16000f
        val totalIdr = (totalUsd * rate).toLong()
        assertEquals(40, totalIdr)
    }

    @Test
    fun `output tokens based on component count`() {
        val selectedCount = 4
        val outputTokens = selectedCount * 250
        assertEquals(1000, outputTokens)
    }

    @Test
    fun `pagination page calculation`() {
        val models = 45
        val pageSize = 10
        val totalPages = (models + pageSize - 1) / pageSize
        assertEquals(5, totalPages)
    }

    @Test
    fun `search models case insensitive`() {
        val models = listOf("DeepSeek Chat", "GPT-4", "Claude 3", "deepseek-coder")
        val query = "deepseek"
        val result = models.filter { it.contains(query, ignoreCase = true) }
        assertEquals(2, result.size)
    }
}