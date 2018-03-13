package org.iskopasi.tictactoe.interfaces

import org.iskopasi.tictactoe.utils.Matrix

interface IGameLogic {
    enum class Player {
        CROSS, CIRCLE, NONE
    }

    var columns: Int
    var rows: Int
    var player: Player
    var android: Player
    var matrix: Matrix<Player>
    var playerScore: Int
    var androidScore: Int

    fun setCell(x: Int, y: Int, player: Player)
    fun makeMove(pair: Pair<Int, Int>?, isHuman: Boolean)
    fun checkVictory(): Pair<Player, FloatArray>
    fun isEmpty(x: Int, y: Int): Boolean
    fun restart()
    fun isFull(): Boolean
}