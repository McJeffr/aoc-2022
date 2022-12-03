package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(3)

    val rucksacks = input.map { contents ->
        Rucksack(contents.toCharArray().toList())
    }

    part1(rucksacks)
    part2(rucksacks)
}

fun part1(rucksacks: List<Rucksack>) {
    val sumOfPriorities = rucksacks.sumOf { getItemTypeScore(it.findCommonItemTypeBetweenCompartments()) }
    println("Sum of priorities of the common item types is '$sumOfPriorities'")
}

fun part2(rucksacks: List<Rucksack>) {
    val groups = rucksacks.chunked(3)
    val sumOfPriorities = groups.sumOf { group ->
        val firstRucksack = group[0].itemTypes.toSet()
        val secondRucksack = group[1].itemTypes.toSet()
        val thirdRucksack = group[2].itemTypes.toSet()
        val commonItemType = firstRucksack.intersect(secondRucksack).intersect(thirdRucksack).first()
        getItemTypeScore(commonItemType)
    }
    println("Sum of the priorities of the groups of elves is '$sumOfPriorities'")
}

fun getItemTypeScore(itemType: Char): Int =
    if (itemType.isUpperCase()) {
        itemType.code - 64 + 26 // A to Z == 65 to 91
    } else {
        itemType.code - 96 // a to z == 97 to 123
    }

data class Rucksack(val itemTypes: List<Char>) {

    fun findCommonItemTypeBetweenCompartments(): Char {
        val compartments = itemTypes.chunked(itemTypes.size / 2)
        val compartment1 = compartments[0].toCharArray().toSet()
        val compartment2 = compartments[1].toCharArray().toSet()
        return compartment1.intersect(compartment2).first()
    }

}
