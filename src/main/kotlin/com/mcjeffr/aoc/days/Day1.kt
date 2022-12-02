package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(1)

    var elf = 0
    val elves: List<Int> = input
        .groupBy { line ->
            if (line.isBlank()) {
                elf++
                return@groupBy -1
            }
            elf
        }
        .filterKeys { it != -1 }
        .mapValues { entry -> entry.value.sumOf { it.toInt() } }
        .values
        .sortedDescending()

    println("Top elf carries: ${elves.first()}")
    println("Top three elves carry a total of: ${elves.take(3).sum()}")
}
