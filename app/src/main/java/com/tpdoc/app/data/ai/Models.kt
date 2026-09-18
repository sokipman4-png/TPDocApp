package com.tpdoc.app.data.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenRouterModel(
    val id: String,
    val name: String,
    val context_length: Int = 4096,
    val pricing: Pricing = Pricing(),
)

@Serializable
data class Pricing(
    val prompt: String = "0",
    val completion: String = "0",
)

@Serializable
data class ModelsResponse(
    val data: List<OpenRouterModel> = emptyList(),
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
)

@Serializable
data class ChatResponse(
    val id: String? = null,
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null,
    val model: String? = null,
)

@Serializable
data class Choice(
    val message: ChatMessage? = null,
    val finish_reason: String? = null,
)

@Serializable
data class Usage(
    val prompt_tokens: Int = 0,
    val completion_tokens: Int = 0,
    val total_tokens: Int = 0,
)

val openRouterJson = Json { ignoreUnknownKeys = true; isLenient = true }