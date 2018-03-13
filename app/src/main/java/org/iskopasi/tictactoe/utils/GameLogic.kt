package org.iskopasi.tictactoe.utils

import org.iskopasi.tictactoe.R
import org.iskopasi.tictactoe.interfaces.IGameLogic
import org.iskopasi.tictactoe.interfaces.IGameLogic.Player

class GameLogic(x: Int, y: Int, side: Int) : IGameLogic {
    override var columns = 3
    override var rows = 3
    override var player = Player.NONE
    override var android = Player.NONE
    override var matrix = Matrix(x, y) { x, y -> Player.NONE }
    override var playerScore = 0
    override var androidScore = 0
    private var priorityMatrix = Matrix(x, y) { x, y -> 0 }
    private var clickCounter = 1

    init {
        if (side == R.string.player_side_cross) {
            player = Player.CROSS
            android = Player.CIRCLE
        } else {
            player = Player.CIRCLE
            android = Player.CROSS
        }
        assert(x == y)
        columns = x
        rows = y

        priorityMatrix[columns / 2, rows / 2]++
    }

    override fun isFull(): Boolean {
        return clickCounter == rows * columns
    }

    override fun restart() {
        matrix = Matrix(columns, rows) { x, y -> Player.NONE }
        priorityMatrix = Matrix(columns, rows) { x, y -> 0 }
        clickCounter = 1
    }

    override fun setCell(x: Int, y: Int, player: Player) {
        if (matrix[x, y] == Player.NONE)
            matrix[x, y] = player
    }

    override fun makeMove(pair: Pair<Int, Int>?, isHuman: Boolean) {
        if (isHuman) {
            increaseAreaPriority(pair!!.first, pair.second, player)
            setCell(pair.first, pair.second, player)
        } else {
            val (x, y) = findCellWithMaxPriority()
            increaseAreaPriority(x, y, android)
            setCell(x, y, android)
        }
        clickCounter++
    }

    private fun findCellWithMaxPriority(): Pair<Int, Int> {
        var max = 0
        var (px, py) = Pair(1, 1)
        priorityMatrix.forEachIndexed { x, y, i ->
            if (i > max && matrix[x, y] == Player.NONE) {
                max = i
                px = x
                py = y
            }
        }
        return Pair(px, py)
    }

    private fun increaseAreaPriority(centerX: Int, centerY: Int, player: Player) {
        //Check diagonal
        if (centerX == centerY) {
            var pair0 = Pair(0, 0)
            var pair1 = Pair(1, 1)
            var pair2 = Pair(2, 2)
            tryIncreasePriority(pair0, pair1, pair2, player)
            if (centerX == 1 && centerY == 1) {
                pair0 = Pair(2, 0)
                pair1 = Pair(1, 1)
                pair2 = Pair(0, 2)
                tryIncreasePriority(pair0, pair1, pair2, player)
            }
        } else if ((centerX == 0 && centerY == rows - 1) ||
                (centerX == columns - 1 && centerY == 0)) {
            val pair0 = Pair(2, 0)
            val pair1 = Pair(1, 1)
            val pair2 = Pair(0, 2)
            tryIncreasePriority(pair0, pair1, pair2, player)
        }

        //Check vertical
        var pair0 = Pair(centerX, 0)
        var pair1 = Pair(centerX, 1)
        var pair2 = Pair(centerX, 2)
        tryIncreasePriority(pair0, pair1, pair2, player)

        //Check horizontal
        pair0 = Pair(0, centerY)
        pair1 = Pair(1, centerY)
        pair2 = Pair(2, centerY)
        tryIncreasePriority(pair0, pair1, pair2, player)
    }

    private fun tryIncreasePriority(pair0: Pair<Int, Int>, pair1: Pair<Int, Int>, pair2: Pair<Int, Int>,
                                    player: Player) {
        val value0 = matrix[pair0.first, pair0.second]
        val value1 = matrix[pair1.first, pair1.second]
        val value2 = matrix[pair2.first, pair2.second]
        if (value0 == player || value1 == player || value2 == player) {
            var max = priorityMatrix[pair0.first, pair0.second]
            if (priorityMatrix[pair1.first, pair1.second] > max) {
                max = priorityMatrix[pair1.first, pair1.second]
            }
            if (priorityMatrix[pair2.first, pair2.second] > max) {
                max = priorityMatrix[pair2.first, pair2.second]
            }
            priorityMatrix[pair0.first, pair0.second] += max
            priorityMatrix[pair1.first, pair1.second] += max
            priorityMatrix[pair2.first, pair2.second] += max
        }
        if (value0 != Player.NONE && value0 != player) {
            priorityMatrix[pair0.first, pair0.second] = 1
        }
        if (value1 != Player.NONE && value1 != player) {
            priorityMatrix[pair1.first, pair1.second] = 1
        }
        if (value2 != Player.NONE && value2 != player) {
            priorityMatrix[pair2.first, pair2.second] = 1
        }

        if ((value0 == Player.NONE || value0 == player)
                && (value1 == Player.NONE || value1 == player)
                && (value2 == Player.NONE || value2 == player)) {
            priorityMatrix[pair0.first, pair0.second]++
            priorityMatrix[pair1.first, pair1.second]++
            priorityMatrix[pair2.first, pair2.second]++
        }
    }

    override fun isEmpty(x: Int, y: Int): Boolean {
        return matrix[x, y] == Player.NONE
    }

    override fun checkVictory(): Pair<Player, FloatArray> {
        //Check rows and columns
        for (i in 0 until rows) {
            var value = checkLine(matrix[i, 0], matrix[i, 1], matrix[i, 2])
            if (value != Player.NONE)
                return Pair(value, floatArrayOf(i.toFloat(), 0f, i.toFloat(), 2f))

            value = checkLine(matrix[0, i], matrix[1, i], matrix[2, i])
            if (value != Player.NONE)
                return Pair(value, floatArrayOf(0f, i.toFloat(), 2f, i.toFloat()))
        }

        //Check diagonal
        var value = checkLine(matrix[0, 0], matrix[1, 1], matrix[2, 2])
        if (value != Player.NONE)
            return Pair(value, floatArrayOf(0f, 0f, 2f, 2f))

        value = checkLine(matrix[2, 0], matrix[1, 1], matrix[0, 2])
        if (value != Player.NONE)
            return Pair(value, floatArrayOf(2f, 0f, 0f, 2f))

        return Pair(Player.NONE, floatArrayOf(0f, 0f, 0f, 0f))
    }

    private fun checkLine(value0: Player, value1: Player, value2: Player): Player {
        if (value0 != Player.NONE && value1 != Player.NONE && value2 != Player.NONE) {
            if (value0 == value1) {
                if (value1 == value2) {
                    return value2
                }
            }
        }

        return Player.NONE
    }
}