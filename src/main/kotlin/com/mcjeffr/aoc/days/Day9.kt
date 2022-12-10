package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.math.abs

fun main() {
    val input = PuzzleInput.getInput(9)
    val steps = parseSteps(input)

    part1(steps)
    part2(steps)
}

fun part1(steps: List<Direction>) {
    val tailCoordinates = simulateRope(steps, 2)
    println("The tail of a rope with 2 knots has visited '${tailCoordinates.toSet().size}' unique positions")
}

fun part2(steps: List<Direction>) {
    val tailCoordinates = simulateRope(steps, 10)
    println("The tail of a rope with 10 knots has visited '${tailCoordinates.toSet().size}' unique positions")
}

fun simulateRope(steps: List<Direction>, knots: Int): List<Vector2> {
    val knotCoordinates = MutableList(knots) { mutableListOf(Vector2(0, 0)) }
    steps.forEachIndexed { stepIndex, step ->
        knotCoordinates.forEachIndexed { knotIndex, _ ->
            /* If we are at the head, we simply move the first knot (the "head" if the rope) */
            if (knotIndex == 0) {
                val prevHead = knotCoordinates[0][stepIndex]
                val nextHead = prevHead + step.diff
                knotCoordinates[0].add(nextHead)
            }

            /* Else, we need to move the knot based on where the head was at */
            else {
                val prevHead = knotCoordinates[knotIndex - 1][stepIndex]
                val nextHead = knotCoordinates[knotIndex - 1][stepIndex + 1]
                val prevTail = knotCoordinates[knotIndex][stepIndex]
                val nextTail = moveKnot(prevHead, nextHead, prevTail)
                knotCoordinates[knotIndex].add(nextTail)
            }
        }
    }
    return knotCoordinates.last()
}

fun moveKnot(prevHead: Vector2, nextHead: Vector2, prevTail: Vector2): Vector2 {
    val diff = nextHead - prevTail

    /* If we are still connected to the head, we can simply stay put */
    if (abs(diff.x) <= 1 && abs(diff.y) <= 1) {
        return prevTail
    }

    /* If we go around the corner, both x and y in diff are not 0 */
    if (abs(diff.x) > 0 && abs(diff.y) > 0) {
        /* If the head moved diagonally, so should we */
        val headDiff = nextHead - prevHead
        if (abs(headDiff.x) > 0 && abs(headDiff.y) > 0) {
            return prevTail + headDiff
        }
        /* If the head didn't move diagonally, we simply move to the head's past location */
        return prevHead
    }

    /* If we didn't go around a corner, we move in the direction that connects us back to the head */
    val direction = Direction.values().find {
        val newDiff = diff - it.diff
        abs(newDiff.x) <= 1 && abs(newDiff.y) <= 1
    } ?: throw IllegalStateException("Unexpected state encountered")
    return prevTail + direction.diff
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

// Debug functions
fun drawRope(knots: List<List<Vector2>>) {
    val minX = knots.minOf { knot -> knot.minOf { it.x } }
    val maxX = knots.maxOf { knot -> knot.maxOf { it.x } }
    val minY = knots.minOf { knot -> knot.minOf { it.y } }
    val maxY = knots.maxOf { knot -> knot.maxOf { it.y } }

    for (y in maxY downTo minY) {
        for (x in minX..maxX) {
            val lastKnotCoords = knots.map { it.last() }
            when (val knot = lastKnotCoords.indexOf(Vector2(x, y))) {
                -1 -> if (Vector2(x, y) == Vector2(0, 0)) print("s") else print(".")
                0 -> print("H")
                else -> print("$knot")
            }
        }
        print("\n")
    }
}

fun drawVisited(coords: List<Vector2>) {
    val minX = coords.minOf { it.x }
    val maxX = coords.maxOf { it.x }
    val minY = coords.minOf { it.y }
    val maxY = coords.maxOf { it.y }

    for (y in maxY downTo minY) {
        for (x in minX..maxX) {
            if (Vector2(x, y) == Vector2(0, 0)) {
                print("s")
                continue
            }
            if (coords.contains(Vector2(x, y))) {
                print("#")
            } else {
                print(".")
            }
        }
        print("\n")
    }
}
