package com.mcjeffr.aoc.days

import kotlin.math.floor

fun main() {
    val monkeys = input

    for (round in 1..20) {
        monkeys.forEach { monkey ->
            if (!monkey.hasItems()) {
                return@forEach
            }

            repeat(monkey.items.size) {
                val worryLevel = monkey.inspectItem()
                val recipient = monkeys[monkey.test(worryLevel)]
                monkey.passItem(recipient)
            }
        }
    }

    val rankedMonkeys = monkeys.sortedByDescending { it.inspectedItems }
    val first = rankedMonkeys[0].inspectedItems
    val second = rankedMonkeys[1].inspectedItems
    println("The factor of the number of inspections by the two top monkeys is '${first * second}'")
}

data class Monkey(
    val id: Int,
    val items: MutableList<Int>,
    val operation: (Int) -> Int,
    val test: (Int) -> Int
) {

    var inspectedItems = 0

    fun hasItems() = items.isNotEmpty()

    fun inspectItem(): Int {
        inspectedItems++
        val newItem = floor(operation(this.items[0]) / 3.0).toInt()
        items[0] = newItem
        return newItem
    }

    fun passItem(recipient: Monkey) {
        val item = this.items.removeFirst()
        recipient.items.add(item)
    }

    override fun toString() = "Monkey(id=$id,items=$items,inspected=$inspectedItems)"

}

// ---------------------------------------------------------------------------------------------
// Puzzle input
// ---------------------------------------------------------------------------------------------
val example = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(79, 98),
        operation = { old -> old * 19 },
        test = { item -> if (item % 23 == 0) 2 else 3 }
    ),
    Monkey(
        id = 1,
        items = mutableListOf(54, 65, 75, 74),
        operation = { old -> old + 6 },
        test = { item -> if (item % 19 == 0) 2 else 0 }
    ),
    Monkey(
        id = 2,
        items = mutableListOf(79, 60, 97),
        operation = { old -> old * old },
        test = { item -> if (item % 13 == 0) 1 else 3 }
    ),
    Monkey(
        id = 3,
        items = mutableListOf(74),
        operation = { old -> old + 3 },
        test = { item -> if (item % 17 == 0) 0 else 1 }
    )
)

val input = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(96, 60, 68, 91, 83, 57, 85),
        operation = { old -> old * 2 },
        test = { item -> if (item % 17 == 0) 2 else 5 }
    ),
    Monkey(
        id = 1,
        items = mutableListOf(75, 78, 68, 81, 73, 99),
        operation = { old -> old + 3 },
        test = { item -> if (item % 13 == 0) 7 else 4 }
    ),
    Monkey(
        id = 2,
        items = mutableListOf(69, 86, 67, 55, 96, 69, 94, 85),
        operation = { old -> old + 6 },
        test = { item -> if (item % 19 == 0) 6 else 5 }
    ),
    Monkey(
        id = 3,
        items = mutableListOf(88, 75, 74, 98, 80),
        operation = { old -> old + 5 },
        test = { item -> if (item % 7 == 0) 7 else 1 }
    ),
    Monkey(
        id = 4,
        items = mutableListOf(82),
        operation = { old -> old + 8 },
        test = { item -> if (item % 11 == 0) 0 else 2 }
    ),
    Monkey(
        id = 5,
        items = mutableListOf(72, 92, 92),
        operation = { old -> old * 5 },
        test = { item -> if (item % 3 == 0) 6 else 3 }
    ),
    Monkey(
        id = 6,
        items = mutableListOf(74, 61),
        operation = { old -> old * old },
        test = { item -> if (item % 2 == 0) 3 else 1 }
    ),
    Monkey(
        id = 7,
        items = mutableListOf(76, 86, 83, 55),
        operation = { old -> old + 4 },
        test = { item -> if (item % 5 == 0) 4 else 0 }
    ),
)
