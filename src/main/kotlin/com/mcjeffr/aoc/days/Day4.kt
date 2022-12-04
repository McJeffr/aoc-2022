package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(4)
    val sectors = extractSectors(input)

    part1(sectors)
    part2(sectors)
}

fun part1(sectors: List<Pair<Sector, Sector>>) {
    val containedSectors = sectors.filter { (sectorA, sectorB) ->
        sectorA.contains(sectorB) || sectorB.contains(sectorA)
    }

    println("There are '${containedSectors.size}' sectors fully containing other sectors")
}

fun part2(sectors: List<Pair<Sector, Sector>>) {
    val overlappingSectors = sectors.filter { (sectorA, sectorB) ->
        sectorA.overlaps(sectorB) || sectorB.overlaps(sectorA)
    }

    println("There are '${overlappingSectors.size}' overlapping sectors")
}

fun extractSectors(input: List<String>) =
    input.map { line ->
        val elves = line.split(",")
        val sectors = elves.map { elf ->
            val sectionIds = elf.split("-")
            Sector(sectionIds[0].toInt(), sectionIds[1].toInt())
        }
        Pair(sectors[0], sectors[1])
    }

data class Sector(val min: Int, val max: Int) {

    fun contains(sector: Sector) = min <= sector.min && max >= sector.max

    fun overlaps(sector: Sector) = max >= sector.min && min <= sector.max

}
