package com.yourapp.assistant.settings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourapp.assistant.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val apiKeyInput =
            findViewById<EditText>(R.id.apiKeyInput)

        val saveButton =
            findViewById<Button>(R.id.saveApiKeyButton)

        val clearButton =
            findViewById<Button>(R.id.clearApiKeyButton)

        apiKeyInput.setText(
            ApiKeyStore.get(this)
        )

        saveButton.setOnClickListener {

            val key =
                apiKeyInput.text
                    .toString()
                    .trim()

            if (key.isEmpty()) {

                Toast.makeText(
                    this,
                    "Gemini API key enter karo",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            ApiKeyStore.save(
                this,
                key
            )

            Toast.makeText(
                this,
                "API key saved",
                Toast.LENGTH_SHORT
            ).show()
        }

        clearButton.setOnClickListener {

            ApiKeyStore.clear(this)

            apiKeyInput.text.clear()

            Toast.makeText(
                this,
                "API key deleted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
