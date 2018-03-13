package org.iskopasi.tictactoe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.MotionEvent
import org.iskopasi.tictactoe.R
import org.iskopasi.tictactoe.interfaces.ICallback
import org.iskopasi.tictactoe.interfaces.IGameLogic


class GridCardView : CardView {
    private lateinit var logic: IGameLogic
    private val gridPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val linePaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var viewWidth = 0
    private var viewHeight = 0
    private var xStep = 0
    private var yStep = 0
    private var playerBitmap: Bitmap? = null
    private var androidBitmap: Bitmap? = null
    private var playerCallable: ICallback<String>? = null
    private var androidCallable: ICallback<String>? = null
    private var restartCallback: ICallback<String>? = null
    private var lineToDraw = FloatArray(0)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        post {
            viewWidth = measuredWidth
            viewHeight = measuredHeight

            linePaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
            linePaint.strokeWidth = context.resources.getDimension(R.dimen.stroke_width)
            linePaint.strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawTurns(canvas)

        if (lineToDraw.isNotEmpty()) {
            val left = lineToDraw[0] * xStep + xStep / 2
            val top = lineToDraw[1] * yStep + yStep / 2
            val right = lineToDraw[2] * xStep + xStep / 2
            val bottom = lineToDraw[3] * yStep + yStep / 2
            canvas.drawLine(left, top, right, bottom, linePaint)
        }
    }

    private fun drawTurns(canvas: Canvas?) {
        val matrix = logic.matrix
        matrix.forEachIndexed { x, y, i ->
            val xStartBlock = x.toFloat() * xStep
            val yStartBlock = y.toFloat() * yStep
            val xCenterBlock = xStartBlock + xStep / 2
            val yCenterBlock = yStartBlock + yStep / 2
            if (i == logic.player) {
                val xCenter = xCenterBlock - playerBitmap!!.width / 2
                val yCenter = yCenterBlock - playerBitmap!!.height / 2
                canvas?.drawBitmap(playerBitmap, xCenter, yCenter, gridPaint)
            } else if (i == logic.android) {
                val xCenter = xCenterBlock - androidBitmap!!.width / 2
                val yCenter = yCenterBlock - androidBitmap!!.height / 2
                canvas?.drawBitmap(androidBitmap, xCenter, yCenter, gridPaint)
            }
        }
    }

    private fun drawGrid(canvas: Canvas?) {
        for (i in 1 until logic.rows) {
            val x = xStep * i.toFloat()
            val y = yStep * i.toFloat()
            canvas?.drawLine(x, 0f, x, viewHeight.toFloat(), gridPaint)
            canvas?.drawLine(0f, y, viewWidth.toFloat(), y, gridPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (lineToDraw.isNotEmpty())
            return super.onTouchEvent(event)

        //Find cell
        val coordPair = findMatrixXY(event)
        if (logic.isEmpty(coordPair.first, coordPair.second)) {
            //Player turn
            logic.makeMove(coordPair, true)

            //Opponent turn
            logic.makeMove(null, false)

            //Redrawing canvas rect
            val left = coordPair.first * xStep
            val top = coordPair.second * yStep
            val right = coordPair.first * xStep + xStep
            val bottom = coordPair.second * yStep + yStep
            invalidate(left, top, right, bottom)

            val victory = logic.checkVictory()
            if (victory.first != IGameLogic.Player.NONE) {
                if (victory.first == logic.player) {
                    logic.playerScore++
                    playerCallable?.call(logic.playerScore.toString())
                } else {
                    logic.androidScore++
                    androidCallable?.call(logic.androidScore.toString())
                }
                lineToDraw = victory.second
                invalidate()
                restartCallback?.call("")
            }
            if (logic.isFull())
                restartCallback?.call("")
        }

        return super.onTouchEvent(event)
    }

    private fun findMatrixXY(event: MotionEvent): Pair<Int, Int> {
        var x = (event.x / xStep).toInt()
        var y = (event.y / yStep).toInt()
        if (x >= logic.columns) x = logic.columns - 1
        if (y >= logic.rows) y = logic.rows - 1
        if (x < 0) x = 0
        if (y < 0) y = 0
        return Pair(x, y)
    }

    fun setLogic(logic: IGameLogic, xCallback: ICallback<String>, oCallback: ICallback<String>) {
        this.logic = logic
        xStep = viewWidth / logic.columns
        yStep = viewHeight / logic.rows

        setPlayerSide(logic.player, xCallback, oCallback)
    }

    private fun setPlayerSide(side: IGameLogic.Player,
                              xCallable: ICallback<String>,
                              oCallable: ICallback<String>) {
        post {
            //Preparing bitmaps
            xStep = viewWidth / logic.columns
            yStep = viewWidth / logic.rows
            val bitmapWidth = xStep / 2
            val bitmapHeight = yStep / 2
            val tempX = Utils.getBitmapFromVectorDrawable(context, R.drawable.ic_x)
            val xBitmap = Bitmap.createScaledBitmap(tempX, bitmapWidth, bitmapHeight, false)
            tempX.recycle()
            val tempO = Utils.getBitmapFromVectorDrawable(context, R.drawable.ic_o)
            val oBitmap = Bitmap.createScaledBitmap(tempO, bitmapWidth, bitmapHeight, false)
            tempO.recycle()

            //Setting up players' bitmaps
            if (side == IGameLogic.Player.CROSS) {
                playerBitmap = xBitmap
                androidBitmap = oBitmap
                playerCallable = xCallable
                androidCallable = oCallable
            } else {
                playerBitmap = oBitmap
                androidBitmap = xBitmap
                playerCallable = oCallable
                androidCallable = xCallable
            }
        }
    }

    fun setRestartCallback(restartCallback: ICallback<String>) {
        this.restartCallback = restartCallback
    }

    fun restart() {
        logic.restart()
        lineToDraw = FloatArray(0)
        invalidate()
    }
}