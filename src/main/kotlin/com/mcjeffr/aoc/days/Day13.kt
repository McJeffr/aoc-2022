package com.mcjeffr.aoc.days

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mcjeffr.aoc.common.PuzzleInput

val mapper = jacksonObjectMapper()

fun main() {
    val input = PuzzleInput.getInput(13)
    val pairs = parsePairs(input)

    part1(pairs)
}

fun part1(pairs: List<Pair<JsonNode, JsonNode>>) {
    val sumOfCorrectPairs = pairs.mapIndexed { index, pair ->
        if (check(pair)) {
            index + 1
        } else {
            0
        }
    }.sum()
    println("The sum of the correct pairs' indices is '$sumOfCorrectPairs'")
}

enum class Result {
    TRUE,
    FALSE,
    CONTINUE
}

fun check(pair: Pair<JsonNode, JsonNode>): Boolean {
    val left = pair.first
    val right = pair.second

    return when (compare(left, right)) {
        Result.TRUE -> true
        Result.FALSE -> false
        else -> true
    }
}

fun compare(left: JsonNode, right: JsonNode): Result {
    /* If the left and right are not of similar types, we convert to that type and check again */
    if (left.isInt && right.isArrayOfInts()) {
        return compare(mapper.createArrayNode().add(left), right)
    }
    if (left.isArrayOfInts() && right.isInt) {
        return compare(left, mapper.createArrayNode().add(right))
    }

    /* Extract the first element of left and right, returning true/false if one of the two runs out before the other */
    val firstLeft = left.firstOrNull()
    val firstRight = right.firstOrNull()
    if (firstLeft == null && firstRight == null) {
        return Result.CONTINUE
    } else if (firstLeft == null) {
        return Result.TRUE
    } else if (firstRight == null) {
        return Result.FALSE
    }

    if (left.isArrayOfInts() && right.isArrayOfInts()) {
        /* If both are arrays of ints, we start checking each of the ints */
        if (firstLeft.asInt() < firstRight.asInt()) {
            return Result.TRUE
        }
        if (firstLeft.asInt() > firstRight.asInt()) {
            return Result.FALSE
        }

        /* If the elements are equal, we simply check the next ones */
        val result = compare(left.dropFirst(), right.dropFirst())
        if (result == Result.TRUE || result == Result.FALSE) {
            return result
        }
    } else {
        /* If both are single integers, we simply compare them and return the result */
        val result = compare(firstLeft, firstRight)
        if (result == Result.TRUE || result == Result.FALSE) {
            return result
        }
        compare(left.dropFirst(), right.dropFirst())
    }

    /* If we hit this point, we simply continue on to the next element in the lists */
    return Result.CONTINUE
}

fun parsePairs(input: List<String>): List<Pair<JsonNode, JsonNode>> {
    return input.fold(mutableListOf<MutableList<JsonNode>>(mutableListOf())) { acc, line ->
        if (line.isBlank()) {
            return@fold acc
        }

        val json = mapper.readTree(line)
        val pair = acc.last()
        if (pair.size == 2) {
            acc.add(mutableListOf(json))
        } else {
            pair.add(json)
        }
        return@fold acc
    }.map { Pair(it[0], it[1]) }
}

fun JsonNode.isArrayOfInts(): Boolean {
    if (!this.isArray) {
        return false
    }
    val first = this.firstOrNull() ?: return true
    return first.isInt
}

fun JsonNode.dropFirst(): JsonNode {
    if (this !is ArrayNode) {
        return this
    }
    this.remove(0)
    return this
}
