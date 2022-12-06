package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(6).first()

    println("Found start of packet at index '${findStart(input, 4)}'")
    println("Found start of message at index '${findStart(input, 14)}'")
}

fun findStart(input: String, charLength: Int): Int {
    val deque: ArrayDeque<Char> = ArrayDeque()
    input.toCharArray().forEachIndexed { index, char ->
        if (deque.size != charLength) {
            deque.addLast(char)
            return@forEachIndexed
        }

        if (deque.toSet().size == charLength) {
            return index
        } else {
            deque.removeFirst()
            deque.addLast(char)
        }
    }
    return -1
}
