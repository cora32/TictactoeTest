package org.iskopasi.tictactoe

import android.app.Application
import org.iskopasi.tictactoe.utils.Logger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger(System.currentTimeMillis())
    }
}