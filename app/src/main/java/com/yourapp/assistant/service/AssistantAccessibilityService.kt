package com.yourapp.assistant.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AssistantAccessibilityService :
    AccessibilityService() {

    companion object {
        var instance: AssistantAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        // Future assistant actions can be handled here.
    }

    override fun onInterrupt() {
        // Service interrupted.
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun clickText(text: String): Boolean {

        val root = rootInActiveWindow
            ?: return false

        return clickNodeWithText(
            root,
            text
        )
    }

    private fun clickNodeWithText(
        node: AccessibilityNodeInfo,
        text: String
    ): Boolean {

        if (
            node.text
                ?.toString()
                ?.equals(
                    text,
                    ignoreCase = true
                ) == true
        ) {
            return node.performAction(
                AccessibilityNodeInfo.ACTION_CLICK
            )
        }

        for (i in 0 until node.childCount) {

            val child = node.getChild(i)
                ?: continue

            if (
                clickNodeWithText(
                    child,
                    text
                )
            ) {
                return true
            }
        }

        return false
    }
    }
