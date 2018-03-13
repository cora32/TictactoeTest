package org.iskopasi.tictactoe.interfaces

interface ICallback<in T> {
    fun call(value: T)
}