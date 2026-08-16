package com.yourapp.assistant.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AssistantAccessibilityService :
    AccessibilityService() {

    companion object {

        var instance:
            AssistantAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {

        super.onServiceConnected()

        instance = this
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        // Screen events are intentionally not
        // stored or uploaded.
    }

    override fun onInterrupt() {
        // Nothing required.
    }

    override fun onDestroy() {

        instance = null

        super.onDestroy()
    }

    fun clickText(
        text: String
    ): Boolean {

        val root =
            rootInActiveWindow
                ?: return false

        return findAndClick(
            root,
            text
        )
    }

    private fun findAndClick(
        node: AccessibilityNodeInfo,
        text: String
    ): Boolean {

        val nodeText =
            node.text
                ?.toString()
                ?.trim()

        if (
            nodeText.equals(
                text,
                ignoreCase = true
            )
        ) {

            if (
                node.isClickable &&
                node.isEnabled
            ) {

                return node.performAction(
                    AccessibilityNodeInfo
                        .ACTION_CLICK
                )
            }
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            if (
                findAndClick(
                    child,
                    text
                )
            ) {
                return true
            }
        }

        return false
    }

    fun scrollDown(): Boolean {

        val root =
            rootInActiveWindow
                ?: return false

        return performScroll(
            root,
            AccessibilityNodeInfo
                .ACTION_SCROLL_FORWARD
        )
    }

    fun scrollUp(): Boolean {

        val root =
            rootInActiveWindow
                ?: return false

        return performScroll(
            root,
            AccessibilityNodeInfo
                .ACTION_SCROLL_BACKWARD
        )
    }

    private fun performScroll(
        node: AccessibilityNodeInfo,
        action: Int
    ): Boolean {

        if (
            node.isScrollable &&
            node.isEnabled
        ) {

            if (
                node.performAction(action)
            ) {
                return true
            }
        }

        for (
            index in 0 until node.childCount
        ) {

            val child =
                node.getChild(index)
                    ?: continue

            if (
                performScroll(
                    child,
                    action
                )
            ) {
                return true
            }
        }

        return false
    }
    }
