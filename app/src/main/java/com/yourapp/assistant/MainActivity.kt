package com.yourapp.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yourapp.assistant.ai.GeminiClient
import com.yourapp.assistant.settings.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var output: TextView

    private val apiClient by lazy {
        GeminiClient(this)
    }

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (!it) {
                output.text =
                    "Microphone permission nahi mili."
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        input =
            findViewById(R.id.commandInput)

        output =
            findViewById(R.id.responseText)

        val sendButton =
            findViewById<Button>(R.id.sendButton)

        val voiceButton =
            findViewById<Button>(R.id.voiceButton)

        val settingsButton =
            findViewById<Button>(R.id.settingsButton)

        sendButton.setOnClickListener {

            val command =
                input.text
                    .toString()
                    .trim()

            if (command.isNotEmpty()) {
                askGemini(command)
            }
        }

        voiceButton.setOnClickListener {
            startVoiceInput()
        }

        settingsButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    private fun askGemini(
        command: String
    ) {

        output.text = "Soch raha hoon..."

        CoroutineScope(
            Dispatchers.Main
        ).launch {

            val result =
                withContext(
                    Dispatchers.IO
                ) {
                    apiClient.generate(
                        command
                    )
                }

            output.text = result
        }
    }

    private fun startVoiceInput() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            permissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )

            return
        }

        if (
            !SpeechRecognizer
                .isRecognitionAvailable(this)
        ) {

            output.text =
                "Is phone par voice recognition available nahi hai."

            return
        }

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
            }

        try {
            startActivityForResult(
                intent,
                VOICE_REQUEST
            )
        } catch (e: Exception) {

            output.text =
                "Voice input start nahi ho saka."
        }
    }

    @Deprecated("Android activity result compatibility")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == VOICE_REQUEST &&
            resultCode == RESULT_OK
        ) {

            val results =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

            val command =
                results
                    ?.firstOrNull()
                    .orEmpty()

            if (command.isNotBlank()) {

                input.setText(command)

                askGemini(command)
            }
        }
    }

    companion object {
        private const val VOICE_REQUEST = 1001
    }
}
