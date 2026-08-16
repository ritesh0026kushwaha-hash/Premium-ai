package com.yourapp.assistant.ai

import android.content.Context
import com.yourapp.assistant.settings.ApiKeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiClient(
    private val context: Context
) {

    private val client =
        OkHttpClient()

    suspend fun generate(
        prompt: String
    ): String = withContext(Dispatchers.IO) {

        val apiKey =
            ApiKeyStore.get(context)

        if (apiKey.isBlank()) {
            return@withContext (
                "Pehle Settings me Gemini API key add karo."
            )
        }

        val url =
            "https://generativelanguage.googleapis.com/" +
            "v1beta/models/gemini-2.0-flash:generateContent" +
            "?key=$apiKey"

        val parts =
            JSONArray()
                .put(
                    JSONObject()
                        .put(
                            "text",
                            prompt
                        )
                )

        val contents =
            JSONArray()
                .put(
                    JSONObject()
                        .put(
                            "parts",
                            parts
                        )
                )

        val json =
            JSONObject()
                .put(
                    "contents",
                    contents
                )

        val body =
            json.toString()
                .toRequestBody(
                    "application/json".toMediaType()
                )

        val request =
            Request.Builder()
                .url(url)
                .post(body)
                .build()

        try {

            client
                .newCall(request)
                .execute()
                .use { response ->

                    val text =
                        response.body
                            ?.string()
                            .orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext (
                            "Gemini API error: ${response.code}"
                        )
                    }

                    val root =
                        JSONObject(text)

                    val candidates =
                        root.optJSONArray(
                            "candidates"
                        )

                    if (
                        candidates == null ||
                        candidates.length() == 0
                    ) {
                        return@withContext (
                            "Gemini ne koi response nahi diya."
                        )
                    }

                    val candidate =
                        candidates
                            .optJSONObject(0)

                    val content =
                        candidate
                            ?.optJSONObject("content")

                    val responseParts =
                        content
                            ?.optJSONArray("parts")

                    if (
                        responseParts == null ||
                        responseParts.length() == 0
                    ) {
                        return@withContext (
                            "Gemini response empty hai."
                        )
                    }

                    responseParts
                        .optJSONObject(0)
                        ?.optString("text")
                        .orEmpty()
                }

        } catch (e: Exception) {

            "API request failed: ${
                e.message ?: "unknown error"
            }"
        }
    }
}
