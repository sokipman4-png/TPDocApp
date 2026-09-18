package com.tpdoc.app.data.ai

import com.tpdoc.app.data.ai.openRouterJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

class OpenRouterClient(private val apiKey: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(openRouterJson)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
        }
    }

    suspend fun fetchModels(): List<OpenRouterModel> {
        val response: HttpResponse = client.get("https://openrouter.ai/api/v1/models") {
            header("Authorization", "Bearer $apiKey")
        }
        val body: ModelsResponse = response.body()
        return body.data
    }

    suspend fun chat(
        model: String,
        messages: List<ChatMessage>,
    ): Result<ChatResponse> {
        return try {
            val response: HttpResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(ChatRequest(model = model, messages = messages))
            }
            if (response.status.isSuccess()) {
                val body: ChatResponse = response.body()
                Result.success(body)
            } else {
                val code = response.status.value
                if (code == 429) {
                    Result.failure(RateLimitException("Rate limit tercapai (429). Coba lagi nanti."))
                } else {
                    val errorBody = try { response.body<String>() } catch (e: Exception) { "unknown error" }
                    Result.failure(OpenRouterException("HTTP $code: $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(OpenRouterException(e.message ?: "Network error"))
        }
    }

    suspend fun testConnection(): Result<Long> {
        val start = System.currentTimeMillis()
        return try {
            val response: HttpResponse = client.get("https://openrouter.ai/api/v1/models") {
                header("Authorization", "Bearer $apiKey")
            }
            if (response.status.isSuccess()) {
                val latency = System.currentTimeMillis() - start
                Result.success(latency)
            } else {
                Result.failure(OpenRouterException("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(OpenRouterException(e.message ?: "Connection failed"))
        }
    }

    fun close() {
        client.close()
    }
}

class OpenRouterException(message: String) : Exception(message)
class RateLimitException(message: String) : Exception(message)