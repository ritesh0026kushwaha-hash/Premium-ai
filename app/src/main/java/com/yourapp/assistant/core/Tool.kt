package com.yourapp.assistant.core

data class Tool(
    val name: String,
    val description: String,
    val parameters: List<String> = emptyList()
)

interface ToolExecutor {
    suspend fun execute(
        toolName: String,
        arguments: Map<String, String>
    ): String
}
