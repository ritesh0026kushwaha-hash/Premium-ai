package com.yourapp.assistant.core

object ToolRegistry {

    val tools = listOf(

        Tool(
            name = "open_app",
            description = "Open an installed Android app.",
            parameters = listOf("app_name")
        ),

        Tool(
            name = "go_home",
            description = "Go to the Android home screen."
        ),

        Tool(
            name = "go_back",
            description = "Press the Android back action."
        ),

        Tool(
            name = "scroll_down",
            description = "Scroll the current screen downward."
        ),

        Tool(
            name = "scroll_up",
            description = "Scroll the current screen upward."
        ),

        Tool(
            name = "click_text",
            description = "Click visible text on the current screen.",
            parameters = listOf("text")
        )
    )

    fun description(): String {

        return buildString {

            appendLine(
                "Available tools:"
            )

            for (tool in tools) {

                append("- ")
                append(tool.name)
                append(": ")
                appendLine(tool.description)
            }
        }
    }
}
