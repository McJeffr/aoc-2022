package com.mcjeffr.aoc.common

data class Vector2(val x: Int, val y: Int) {
    override fun toString(): String = "($x,$y)"

    operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector2) = Vector2(this.x - other.x, this.y - other.y)
}
