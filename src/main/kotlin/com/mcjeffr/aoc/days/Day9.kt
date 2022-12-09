package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput
import java.lang.IllegalArgumentException
import kotlin.math.abs

fun main() {
    val input = PuzzleInput.getExampleInput(9)
    val steps = parseSteps(input)

    part1(steps)
    part2(steps)
}

fun part1(steps: List<Direction>) {
    val headCoordinates = mutableListOf(Vector2(0, 0))
    val tailCoordinates = mutableListOf(Vector2(0, 0))
    steps.forEachIndexed { index, step ->
        val prevHead = headCoordinates[index]
        val prevTail = tailCoordinates[index]
        val nextHead = prevHead + step.diff
        val nextTail = moveTail(prevHead, nextHead, prevTail)
        headCoordinates.add(nextHead)
        tailCoordinates.add(nextTail)
    }
    println("The tail has visited '${tailCoordinates.toSet().size}' unique positions")
}

fun part2(steps: List<Direction>) {

}

fun moveTail(prevHead: Vector2, nextHead: Vector2, prevTail: Vector2): Vector2 {
    val diff = nextHead - prevTail

    /* If we go around the corner, both x and y in diff are not 0 */
    if (abs(diff.x) > 0 && abs(diff.y) > 0) {
        /* If the diff of either x or y is 2, we are disconnected from the head and move to the prev head location */
        if (abs(diff.x) > 1 || abs(diff.y) > 1) {
            return prevHead
        }
    }

    /* If we are disconnected from the head, we simply move linearly */
    if (diff.x > 1) {
        return Vector2(prevTail.x + 1, prevTail.y)
    }
    if (diff.x < -1) {
        return Vector2(prevTail.x - 1, prevTail.y)
    }
    if (diff.y > 1) {
        return Vector2(prevTail.x, prevTail.y + 1)
    }
    if (diff.y < -1) {
        return Vector2(prevTail.x, prevTail.y - 1)
    }

    /* Else, we simply return where we are now */
    return prevTail
}

fun parseSteps(input: List<String>): List<Direction> =
    input.map { line ->
        val parts = line.split(" ")
        List(parts[1].toInt()) { Direction.fromCode(parts[0]) }
    }.flatten()

enum class Direction(private val code: String, val diff: Vector2) {
    UP("U", Vector2(0, 1)),
    DOWN("D", Vector2(0, -1)),
    LEFT("L", Vector2(-1, 0)),
    RIGHT("R", Vector2(1, 0));

    companion object {
        fun fromCode(code: String) = values().find { it.code == code }
            ?: throw IllegalArgumentException("Unknown code '$code'")
    }
}

data class Vector2(val x: Int, val y: Int) {
    override fun toString(): String = "($x,$y)"

    operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector2) = Vector2(this.x - other.x, this.y - other.y)
}
