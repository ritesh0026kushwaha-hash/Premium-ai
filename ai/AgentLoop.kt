package com.yourapp.assistant.ai

import android.content.Context
import com.yourapp.assistant.core.SystemTools
import com.yourapp.assistant.core.ToolRegistry
import org.json.JSONObject

class AgentLoop(
    private val context: Context
) {

    private val gemini =
        GeminiClient(context)

    private val tools =
        SystemTools(context)

    suspend fun run(
        userCommand: String
    ): String {

        val prompt = buildPrompt(
            userCommand
        )

        val response =
            gemini.generate(prompt)

        return processResponse(
            response
        )
    }

    private fun buildPrompt(
        command: String
    ): String {

        return """
You are Mera Assistant.

Understand the user's command.

${ToolRegistry.description()}

If the user wants an action, return ONLY JSON.

Example:

{"tool":"go_home","arguments":{}}

Example:

{"tool":"go_back","arguments":{}}

Example:

{"tool":"open_app","arguments":{"app_name":"YouTube"}}

Example:

{"tool":"click_text","arguments":{"text":"Settings"}}

Example:

{"tool":"scroll_down","arguments":{}}

If no tool is needed, return:

{"answer":"your answer"}

User command:
$command

""".trimIndent()
    }

    private suspend fun processResponse(
        response: String
    ): String {

        return try {

            val clean =
                response
                    .trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

            val json =
                JSONObject(clean)

            if (
                json.has("answer")
            ) {

                return json
                    .optString("answer")
            }

            val tool =
                json.optString("tool")

            if (tool.isBlank()) {
                return response
            }

            val arguments =
                mutableMapOf<String, String>()

            val args =
                json.optJSONObject(
                    "arguments"
                )

            if (args != null) {

                val keys =
                    args.keys()

                while (keys.hasNext()) {

                    val key =
                        keys.next()

                    arguments[key] =
                        args.optString(key)
                }
            }

            tools.execute(
                tool,
                arguments
            )

        } catch (
            _: Exception
        ) {

            response
        }
    }
}
