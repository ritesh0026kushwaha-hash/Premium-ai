package com.yourapp.assistant.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import com.yourapp.assistant.service.AssistantAccessibilityService

class SystemTools(
    private val context: Context
) : ToolExecutor {

    override suspend fun execute(
        toolName: String,
        arguments: Map<String, String>
    ): String {

        return try {

            when (toolName) {

                "open_app" -> {

                    val name =
                        arguments["app_name"]
                            ?.trim()
                            .orEmpty()

                    if (name.isEmpty()) {
                        return "App name missing."
                    }

                    openApp(name)
                }

                "go_home" -> {

                    val service =
                        AssistantAccessibilityService.instance

                    if (service == null) {
                        "Accessibility service ON karo."
                    } else {
                        service.performGlobalAction(
                            android.accessibilityservice
                                .AccessibilityService
                                .GLOBAL_ACTION_HOME
                        )

                        "Home screen opened."
                    }
                }

                "go_back" -> {

                    val service =
                        AssistantAccessibilityService.instance

                    if (service == null) {
                        "Accessibility service ON karo."
                    } else {

                        service.performGlobalAction(
                            android.accessibilityservice
                                .AccessibilityService
                                .GLOBAL_ACTION_BACK
                        )

                        "Back action performed."
                    }
                }

                "scroll_down" -> {

                    val service =
                        AssistantAccessibilityService.instance

                    if (service == null) {
                        "Accessibility service ON karo."
                    } else {

                        service.scrollDown()

                        "Scrolled down."
                    }
                }

                "scroll_up" -> {

                    val service =
                        AssistantAccessibilityService.instance

                    if (service == null) {
                        "Accessibility service ON karo."
                    } else {

                        service.scrollUp()

                        "Scrolled up."
                    }
                }

                "click_text" -> {

                    val text =
                        arguments["text"]
                            ?.trim()
                            .orEmpty()

                    if (text.isEmpty()) {
                        "Text missing."
                    } else {

                        val service =
                            AssistantAccessibilityService.instance

                        if (service == null) {
                            "Accessibility service ON karo."
                        } else {

                            if (
                                service.clickText(text)
                            ) {
                                "Clicked $text."
                            } else {
                                "Text nahi mila: $text"
                            }
                        }
                    }
                }

                else -> {
                    "Unknown tool: $toolName"
                }
            }

        } catch (e: Exception) {

            "Tool failed: ${
                e.message ?: "unknown error"
            }"
        }
    }

    private fun openApp(
        appName: String
    ): String {

        val pm =
            context.packageManager

        val apps =
            pm.getInstalledApplications(
                PackageManager.GET_META_DATA
            )

        val match =
            apps.firstOrNull { app ->

                val label =
                    pm.getApplicationLabel(app)
                        .toString()

                label.equals(
                    appName,
                    ignoreCase = true
                )
            }

        if (match == null) {
            return "App nahi mila: $appName"
        }

        val intent =
            pm.getLaunchIntentForPackage(
                match.packageName
            )

        if (intent == null) {
            return "App open nahi ho sakta."
        }

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(intent)

        return "${appName} opened."
    }
}
