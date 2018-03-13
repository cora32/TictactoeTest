package org.iskopasi.tictactoe.utils

import android.content.Context

object Logger {
    private val stringBuilder: StringBuilder by lazy { StringBuilder() }
    private var timestamp = 0L

    operator fun invoke(currentTimeMillis: Long) {
        timestamp = currentTimeMillis
    }

    fun append(tag: String?, x: Float, y: Float) {
        stringBuilder.append(tag)
        stringBuilder.append(", ")
        stringBuilder.append(x)
        stringBuilder.append(", ")
        stringBuilder.append(y)
        stringBuilder.append("\n")
    }

    fun save(context: Context) {
        val filename = "logs_" + timestamp
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(stringBuilder.toString().toByteArray())
        }
    }
}