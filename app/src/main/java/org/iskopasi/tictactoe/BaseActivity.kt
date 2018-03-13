package org.iskopasi.tictactoe

import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import org.iskopasi.tictactoe.utils.Logger

open class BaseActivity : AppCompatActivity() {
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP)
            Logger.save(this)
        else
            Logger.append(localClassName, event.x, event.y)

        return super.dispatchTouchEvent(event)
    }
}