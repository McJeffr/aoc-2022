package com.mcjeffr.aoc.days

import java.math.BigInteger

fun main() {
    val monkeys = input

    // Only select one at a time due to mutating the monkeys!
//    part1(monkeys)
    part2(monkeys)
}

fun part1(monkeys: List<Monkey>) {
    val game = Game(monkeys, 20, true)
    val rankedMonkeys = game.play()
        .map { it.inspectedItems }
        .sortedByDescending { it }
    println(rankedMonkeys)
    println("The factor of the number of inspections by the two top monkeys is '${rankedMonkeys[0] * rankedMonkeys[1]}'")
}

fun part2(monkeys: List<Monkey>) {
    val game = Game(monkeys, 10000, false)
    val rankedMonkeys = game.play()
        .map { it.inspectedItems }
        .sortedByDescending { it }
    println(rankedMonkeys)
    println("The factor of the number of inspections by the two top monkeys is '${rankedMonkeys[0] * rankedMonkeys[1]}'")
}

class Game(private val monkeys: List<Monkey>, private val rounds: Int, private val calmed: Boolean) {
    private val commonMultiplier: BigInteger = monkeys
        .map { BigInteger.valueOf(it.testDivider.toLong()) }
        .reduce { acc, i -> acc * i }

    fun play(): List<Monkey> {
        for (round in 1..rounds) {
            monkeys.forEach { monkey ->
                if (!monkey.hasItems()) {
                    return@forEach
                }

                repeat(monkey.items.size) {
                    val worryLevel = monkey.inspectItem(calmed, commonMultiplier)
                    val recipient = monkeys[monkey.test(worryLevel)]
                    monkey.passItem(recipient)
                }
            }
        }

        return monkeys
    }
}

data class Monkey(
    val id: Int,
    val items: MutableList<BigInteger>,
    val operation: (BigInteger) -> BigInteger,
    val testDivider: Int,
    val testTrue: Int,
    val testFalse: Int
) {
    var inspectedItems: BigInteger = BigInteger.ZERO

    fun hasItems() = items.isNotEmpty()

    fun inspectItem(calmed: Boolean, commonMultiplier: BigInteger): BigInteger {
        inspectedItems++
        var item = this.items[0]
        item = operation(item)
        item %= commonMultiplier
        if (calmed) {
            item /= BigInteger.valueOf(3)
        }
        items[0] = item
        return item
    }

    fun passItem(recipient: Monkey) {
        val item = this.items.removeFirst()
        recipient.items.add(item)
    }

    fun test(item: BigInteger) =
        if (item % BigInteger.valueOf(testDivider.toLong()) == BigInteger.ZERO) testTrue else testFalse

    override fun toString() = "Monkey(id=$id,items=$items,inspected=$inspectedItems)"

}

// ---------------------------------------------------------------------------------------------
// Puzzle input
// ---------------------------------------------------------------------------------------------
val example = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(
            BigInteger.valueOf(79),
            BigInteger.valueOf(98)
        ),
        operation = { old -> old * BigInteger.valueOf(19) },
        testDivider = 23,
        testTrue = 2,
        testFalse = 3,
    ),
    Monkey(
        id = 1,
        items = mutableListOf(
            BigInteger.valueOf(54),
            BigInteger.valueOf(65),
            BigInteger.valueOf(75),
            BigInteger.valueOf(74)
        ),
        operation = { old -> old + BigInteger.valueOf(6) },
        testDivider = 19,
        testTrue = 2,
        testFalse = 0,
    ),
    Monkey(
        id = 2,
        items = mutableListOf(
            BigInteger.valueOf(79),
            BigInteger.valueOf(60),
            BigInteger.valueOf(97)
        ),
        operation = { old -> old * old },
        testDivider = 13,
        testTrue = 1,
        testFalse = 3,
    ),
    Monkey(
        id = 3,
        items = mutableListOf(
            BigInteger.valueOf(74)
        ),
        operation = { old -> old + BigInteger.valueOf(3) },
        testDivider = 17,
        testTrue = 0,
        testFalse = 1,
    )
)

val input = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(
            BigInteger.valueOf(96),
            BigInteger.valueOf(60),
            BigInteger.valueOf(68),
            BigInteger.valueOf(91),
            BigInteger.valueOf(83),
            BigInteger.valueOf(57),
            BigInteger.valueOf(85)
        ),
        operation = { old -> old * BigInteger.valueOf(2) },
        testDivider = 17,
        testTrue = 2,
        testFalse = 5,
    ),
    Monkey(
        id = 1,
        items = mutableListOf(
            BigInteger.valueOf(75),
            BigInteger.valueOf(78),
            BigInteger.valueOf(68),
            BigInteger.valueOf(81),
            BigInteger.valueOf(73),
            BigInteger.valueOf(99)
        ),
        operation = { old -> old + BigInteger.valueOf(3) },
        testDivider = 13,
        testTrue = 7,
        testFalse = 4,
    ),
    Monkey(
        id = 2,
        items = mutableListOf(
            BigInteger.valueOf(69),
            BigInteger.valueOf(86),
            BigInteger.valueOf(67),
            BigInteger.valueOf(55),
            BigInteger.valueOf(96),
            BigInteger.valueOf(69),
            BigInteger.valueOf(94),
            BigInteger.valueOf(85)
        ),
        operation = { old -> old + BigInteger.valueOf(6) },
        testDivider = 19,
        testTrue = 6,
        testFalse = 5,
    ),
    Monkey(
        id = 3,
        items = mutableListOf(
            BigInteger.valueOf(88),
            BigInteger.valueOf(75),
            BigInteger.valueOf(74),
            BigInteger.valueOf(98),
            BigInteger.valueOf(80)
        ),
        operation = { old -> old + BigInteger.valueOf(5) },
        testDivider = 7,
        testTrue = 7,
        testFalse = 1,
    ),
    Monkey(
        id = 4,
        items = mutableListOf(
            BigInteger.valueOf(82)
        ),
        operation = { old -> old + BigInteger.valueOf(8) },
        testDivider = 11,
        testTrue = 0,
        testFalse = 2,
    ),
    Monkey(
        id = 5,
        items = mutableListOf(
            BigInteger.valueOf(72),
            BigInteger.valueOf(92),
            BigInteger.valueOf(92)
        ),
        operation = { old -> old * BigInteger.valueOf(5) },
        testDivider = 3,
        testTrue = 6,
        testFalse = 3,
    ),
    Monkey(
        id = 6,
        items = mutableListOf(
            BigInteger.valueOf(74),
            BigInteger.valueOf(61)
        ),
        operation = { old -> old * old },
        testDivider = 2,
        testTrue = 3,
        testFalse = 1,
    ),
    Monkey(
        id = 7,
        items = mutableListOf(
            BigInteger.valueOf(76),
            BigInteger.valueOf(86),
            BigInteger.valueOf(83),
            BigInteger.valueOf(55)
        ),
        operation = { old -> old + BigInteger.valueOf(4) },
        testDivider = 5,
        testTrue = 4,
        testFalse = 0,
    ),
)
