package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(10)
    val instructions = parseInstructions(input)

    part1(instructions)
    part2(instructions)
}

fun part1(instructions: List<Instruction>) {
    val signalStrengths: MutableList<Int> = mutableListOf()
    val atStartTick = { cycle: Int, x: Int ->
        if ((cycle + 20) % 40 == 0) {
            signalStrengths.add(cycle * x)
        }
    }

    val cpu = CPU(instructions, atStartTick)
    cpu.run()

    println("The sum of signal strengths is '${signalStrengths.sum()}'.")
}

fun part2(instructions: List<Instruction>) {
    println("The display prints (roughly, the display appears to be a bit broken):")
    val atStartTick = { cycle: Int, x: Int ->
        val lineCycle = cycle % 40
        if (lineCycle in x - 1..x + 1) {
            print("#")
        } else {
            print(".")
        }
        if (lineCycle == 0) {
            print("\n")
        }
    }

    val cpu = CPU(instructions, atStartTick)
    cpu.run()
}

fun parseInstructions(input: List<String>): List<Instruction> =
    input.map { line ->
        val parts = line.split(" ")
        if (parts.size == 1) {
            Noop
        } else {
            Addx(parts[1].toInt())
        }
    }

sealed interface Instruction

object Noop : Instruction
data class Addx(val x: Int) : Instruction

class CPU(
    instructions: List<Instruction>,
    private val atStartTick: ((cycle: Int, x: Int) -> Unit)? = null,
    private val atEndTick: ((cycle: Int, x: Int) -> Unit)? = null
) {

    private val remainingInstructions: MutableList<Instruction> = instructions.toMutableList()
    private var activeInstruction: Instruction = remainingInstructions.removeFirst()
    private var busy = activeInstruction is Addx
    private var x = 1

    fun run() {
        for (cycle in 1..240) {
            atStartTick?.let { it(cycle, x) }

            /* When the CPU is busy, we simply skip this cycle */
            if (busy) {
                busy = false
                continue
            }

            /* Otherwise, we move to the next instruction */
            val activeInstruction = this.activeInstruction
            val nextInstruction = remainingInstructions.removeFirstOrNull() ?: break
            when (activeInstruction) {
                is Noop -> {}
                is Addx -> {
                    x += activeInstruction.x
                    busy = true
                }
            }
            this.activeInstruction = nextInstruction

            atEndTick?.let { it(cycle, x) }
        }
    }

}
