package com.crobox.sdk.common

import io.github.oshai.kotlinlogging.KotlinLogging

internal object CroboxDebug {

    private val logger = KotlinLogging.logger { TAG }
    private val TAG = "croboxSDK"
    var loggingEnabled = false

    private fun printText(text: String) {
        if (loggingEnabled) {
            logger.debug { text }
        }
    }

    fun promotionError(msg: String) {
        printError("[Promotion]: $msg")
    }

    fun eventError(msg: String) {
        printError("[Event]: $msg")
    }

    private fun printError(msg: String) {
        printText("[Error]: $msg")
    }
}