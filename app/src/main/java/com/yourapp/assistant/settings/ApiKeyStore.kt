package com.yourapp.assistant.settings

import android.content.Context

object ApiKeyStore {

    private const val PREFS_NAME = "assistant_settings"
    private const val API_KEY = "gemini_api_key"

    fun save(
        context: Context,
        value: String
    ) {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                API_KEY,
                value.trim()
            )
            .apply()
    }

    fun get(context: Context): String {
        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString(API_KEY, "")
            .orEmpty()
    }

    fun clear(context: Context) {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(API_KEY)
            .apply()
    }

    fun exists(context: Context): Boolean {
        return get(context).isNotBlank()
    }
}
