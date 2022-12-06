package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(6).first()

    println("Found start of packet at index '${findStart(input, 4)}'")
    println("Found start of message at index '${findStart(input, 14)}'")
}

fun findStart(input: String, charLength: Int): Int {
    val deque = ArrayDeque(input.takeAndRemove(charLength).toCharArray().toList())
    input.toCharArray().forEachIndexed { index, char ->
        if (deque.toSet().size == charLength) {
            return index
        } else {
            deque.removeFirst()
            deque.addLast(char)
        }
    }
    return -1
}

fun String.takeAndRemove(n: Int): String {
    val taken = this.take(n)
    this.removeRange(0..n)
    return taken
}
