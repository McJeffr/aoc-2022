package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(5)
    val steps = extractSteps(input)

    part1(extractStartingStacks(input), steps)
    part2(extractStartingStacks(input), steps)
}

fun part1(stacks: List<ArrayDeque<Char>>, steps: List<Step>) {
    val ship = Ship(stacks)
    steps.forEach { step ->
        ship.moveOne(step)
    }
    println("The top crates of the CrateMover 9000 are: '${String(ship.getTopCrates().toCharArray())}'")
}

fun part2(stacks: List<ArrayDeque<Char>>, steps: List<Step>) {
    val ship = Ship(stacks)
    steps.forEach { step ->
        ship.moveMultiple(step)
    }
    println("The top crates of the CrateMover 9001 are: '${String(ship.getTopCrates().toCharArray())}'")
}

fun extractStartingStacks(input: List<String>): List<ArrayDeque<Char>> {
    /* First we grab all the lines from the input that form the starting stack */
    val startingStacksInput: MutableList<String> = mutableListOf()
    for (line in input) {
        if (line.isBlank()) {
            break
        }
        startingStacksInput.add(line)
    }

    /* Then, we fetch the number of stacks from the last line */
    val stackNumbersLine = startingStacksInput.removeLast()
    val numberOfStacks = Regex("[0-9]+").findAll(stackNumbersLine).toList().size

    /* Build the initial stacks */
    val stacks: List<ArrayDeque<Char>> = List(numberOfStacks) { ArrayDeque() }
    startingStacksInput.forEach { line ->
        line
            .chunked(4)
            .map { item ->
                item.filter { !it.isWhitespace() && !(it == '[' || it == ']') }
            }.forEachIndexed { idx, item ->
                if (item.isNotBlank()) {
                    stacks[idx].addFirst(item.toCharArray()[0])
                }
            }
    }
    return stacks
}

fun extractSteps(input: List<String>): List<Step> =
    input
        .filter { it.startsWith("move") }
        .map { line ->
            val components = line.split(" ")
            val amount = components[1].toInt()
            val from = components[3].toInt()
            val to = components[5].toInt()
            Step(from, to, amount)
        }

data class Step(val from: Int, val to: Int, val amount: Int)

class Ship(private val stacks: List<ArrayDeque<Char>>) {

    fun moveOne(step: Step) {
        repeat(step.amount) {
            val crate = stacks[step.from - 1].removeLast()
            stacks[step.to - 1].addLast(crate)
        }
    }

    fun moveMultiple(step: Step) {
        val crates = stacks[step.from - 1].removeLast(step.amount)
        stacks[step.to - 1].addAll(crates)
    }

    fun getTopCrates(): List<Char> = stacks.map { it.last() }

}

fun <E> ArrayDeque<E>.removeLast(n: Int): Collection<E> {
    val removed = mutableListOf<E>()
    repeat(n) {
        removed.add(this.removeLast())
    }
    return removed.reversed()
}
