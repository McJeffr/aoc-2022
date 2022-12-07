package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput
import java.lang.IllegalStateException

fun main() {
    val input = PuzzleInput.getInput(7)
    val commands = parseConsoleLog(input)
    val fsRoot = buildFileSystem(commands)

    part1(fsRoot)
    part2(fsRoot)
}

fun part1(fsRoot: Node) {
    val visitor = DirectorySizeVisitor()
    fsRoot.visit(visitor)
    val size = visitor.collector.filter { it.size < 100000 }.sumOf { it.size }

    println("The combined size of all directories with at most 100000 is '$size'.")
}

fun part2(fsRoot: Node) {
    val fsRootSize = fsRoot.size()
    val unusedSpace = 70000000 - fsRootSize
    val spaceToFreeUp = 30000000 - unusedSpace

    val visitor = DirectorySizeVisitor()
    fsRoot.visit(visitor)
    val directory = visitor.collector.filter { it.size > spaceToFreeUp }.minByOrNull { it.size }!!

    println("The size of the smallest directory to delete is '${directory.size}', which is directory '${directory.name}'.")
}

// ---------------------------------------------------------------------
// COMMAND PARSING
// ---------------------------------------------------------------------
fun parseConsoleLog(lines: List<String>): List<Command> {
    val commands: MutableList<Command> = mutableListOf()
    lines.forEach { line ->
        if (line.startsWith("$")) {
            commands.add(parseCommand(line))
        } else {
            val command = commands.last()
            if (command !is LsCommand) {
                throw IllegalStateException("Encountered unexpected command type")
            }
            command.output.add(parseLsLog(line))
        }
    }
    return commands
}

fun parseCommand(line: String): Command {
    val cmd = line.substring(2)
    return if (cmd.startsWith("ls")) {
        LsCommand()
    } else if (cmd.startsWith("cd")) {
        val target = cmd.substring(3)
        CdCommand(target)
    } else {
        throw IllegalStateException("Unknown command: '$cmd'")
    }
}

fun parseLsLog(line: String): Data {
    return if (line.startsWith("dir")) {
        Directory(line.substring(4))
    } else {
        val fileParts = line.split(" ")
        val size = fileParts[0].toInt()
        val name = fileParts[1]
        File(name, size)
    }
}

interface Command
data class CdCommand(val target: String) : Command
data class LsCommand(val output: MutableList<Data> = mutableListOf()) : Command

interface Data
data class Directory(val name: String) : Data
data class File(val name: String, val size: Int) : Data

// ---------------------------------------------------------------------
// FILE SYSTEM
// ---------------------------------------------------------------------
data class Node(
    val name: String,
    val parent: Node? = null,
    val dirs: MutableList<Node> = mutableListOf(),
    val files: MutableList<File> = mutableListOf()
) {

    private fun addDirs(dirs: List<Directory>) {
        this.dirs.addAll(dirs.map { Node(it.name, this) })
    }

    private fun addFiles(files: List<File>) {
        this.files.addAll(files)
    }

    fun addData(data: List<Data>) {
        val dirs = data.filterIsInstance<Directory>()
        val files = data.filterIsInstance<File>()
        addDirs(dirs)
        addFiles(files)
    }

    fun size(): Int = files.sumOf { it.size } + dirs.sumOf { it.size() }

    fun visit(visitor: Visitor) {
        dirs.forEach { it.visit(visitor) }
        visitor.apply(this)
    }

    override fun toString(): String {
        return "Node(name=$name,parent=${parent?.name},files=$files,dirs=$dirs)"
    }

}

fun buildFileSystem(commands: List<Command>): Node {
    val fsRoot = Node("/")
    var currentNode = fsRoot
    commands.forEach { command ->
        when (command) {
            is CdCommand -> {
                currentNode = when (val target = command.target) {
                    "/" -> {
                        fsRoot
                    }

                    ".." -> {
                        currentNode.parent ?: throw IllegalStateException("Trying to leave root node")
                    }

                    else -> {
                        currentNode.dirs.find { it.name == command.target }
                            ?: throw IllegalStateException("Trying to access undiscovered directory '$target'")
                    }
                }
            }

            is LsCommand -> {
                currentNode.addData(command.output)
            }
        }
    }
    return fsRoot
}

interface Visitor {
    fun apply(node: Node)
}

class DirectorySizeVisitor(val collector: MutableList<DirectorySize> = mutableListOf()) : Visitor {

    override fun apply(node: Node) {
        collector.add(DirectorySize(node.name, node.size()))
    }

    data class DirectorySize(val name: String, val size: Int)

}
