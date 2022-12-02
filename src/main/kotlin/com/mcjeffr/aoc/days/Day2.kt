package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput
import kotlin.IllegalStateException

fun main() {
    val input = PuzzleInput.getInput(2)
    val rounds = extractRounds(input)

    part1(rounds)
    part2(rounds)
}

fun extractRounds(rounds: List<String>): List<Pair<String, String>> =
    rounds.map { round ->
        val symbols = round.split(" ")
        val opponentSymbol = symbols[0]
        val playerSymbol = symbols[1]
        Pair(opponentSymbol, playerSymbol)
    }

fun part1(symbols: List<Pair<String, String>>) {
    val score = symbols
        .map { (opponentChar, playerChar) ->
            val opponentSymbol = Symbol.getByOpponentChar(opponentChar)
            val playerSymbol = Symbol.getByPlayerChar(playerChar)
            Pair(opponentSymbol, playerSymbol)
        }
        .sumOf { (opponentSymbol, playerSymbol) -> Round.getScore(opponentSymbol, playerSymbol) }

    println("First score of playing rounds of RPS: $score")
}

fun part2(symbols: List<Pair<String, String>>) {
    val score = symbols
        .map { (opponentChar, gameResultChar) ->
            val opponentSymbol = Symbol.getByOpponentChar(opponentChar)
            val gameResult = GameResult.getByResultChar(gameResultChar)
            val playerSymbol = Round.getSymbolToAchieveGameResult(opponentSymbol, gameResult)
            Pair(opponentSymbol, playerSymbol)
        }
        .sumOf { (opponentSymbol, playerSymbol) -> Round.getScore(opponentSymbol, playerSymbol) }

    println("Second score of playing rounds of RPS: $score")
}

enum class Symbol(private val opponentChar: String, private val playerChar: String, val score: Int) {
    ROCK("A", "X", 1),
    PAPER("B", "Y", 2),
    SCISSORS("C", "Z", 3);

    companion object {
        fun getByOpponentChar(char: String) =
            values().find { it.opponentChar == char }
                ?: throw IllegalStateException("Invalid opponent char '$char'")

        fun getByPlayerChar(char: String) =
            values().find { it.playerChar == char }
                ?: throw IllegalStateException("Invalid player char '$char'")
    }
}

enum class GameResult(val resultChar: String, val score: Int) {
    WON("Z", 6),
    DRAW("Y", 3),
    LOST("X", 0);

    companion object {
        fun getByResultChar(char: String) =
            values().find { it.resultChar == char }
                ?: throw IllegalStateException("Invalid game result char '$char'")
    }
}

object Round {
    private val states: Map<Pair<Symbol, Symbol>, GameResult> = mapOf(
        Pair(Symbol.ROCK, Symbol.ROCK) to GameResult.DRAW,
        Pair(Symbol.ROCK, Symbol.PAPER) to GameResult.WON,
        Pair(Symbol.ROCK, Symbol.SCISSORS) to GameResult.LOST,
        Pair(Symbol.PAPER, Symbol.ROCK) to GameResult.LOST,
        Pair(Symbol.PAPER, Symbol.PAPER) to GameResult.DRAW,
        Pair(Symbol.PAPER, Symbol.SCISSORS) to GameResult.WON,
        Pair(Symbol.SCISSORS, Symbol.ROCK) to GameResult.WON,
        Pair(Symbol.SCISSORS, Symbol.PAPER) to GameResult.LOST,
        Pair(Symbol.SCISSORS, Symbol.SCISSORS) to GameResult.DRAW
    )

    private val statesToAchieveResult: Map<Pair<Symbol, GameResult>, Symbol> = states
        .map { state ->
            val opponentSymbol = state.key.first
            val playerSymbol = state.key.second
            val gameResult = state.value
            Pair(opponentSymbol, gameResult) to playerSymbol
        }
        .associate { it }

    fun getScore(opponentSymbol: Symbol, playerSymbol: Symbol): Int {
        val result = states[Pair(opponentSymbol, playerSymbol)]
            ?: throw IllegalStateException("Invalid game state")
        return result.score + playerSymbol.score
    }

    fun getSymbolToAchieveGameResult(opponentSymbol: Symbol, expectedResult: GameResult) =
        statesToAchieveResult[Pair(opponentSymbol, expectedResult)]
            ?: throw IllegalStateException("Invalid game state")
}
