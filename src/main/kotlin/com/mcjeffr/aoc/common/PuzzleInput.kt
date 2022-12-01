package com.mcjeffr.aoc.common

import java.io.File

object PuzzleInput {

    fun getInput(day: Int): List<String> =
        File("src/main/resources/input/day-$day/input.txt").readLines()

    fun getExampleInput(day: Int): List<String> =
        File("src/main/resources/input/day-$day/example.txt").readLines()

}
