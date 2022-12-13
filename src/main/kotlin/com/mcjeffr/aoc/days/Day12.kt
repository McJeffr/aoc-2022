package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput
import com.mcjeffr.aoc.common.Vector2
import java.lang.IllegalStateException

fun main() {
    val input = PuzzleInput.getInput(12)

    val start = getStartCoordinate(input)
    val end = getEndCoordinate(input)
    val heightmap = getHeightMap(input)
    val grid = buildGraph(heightmap)

    part1(grid, start, end)
    part2(grid, start, end)
}

fun part1(grid: PathGrid, start: Vector2, end: Vector2) {
    val steps = grid.calculateShortestPath(start, end, false)
    println("It takes '$steps' steps to reach the highest point.")
}

fun part2(grid: PathGrid, start: Vector2, end: Vector2) {
    val steps = grid.calculateShortestPath(start, end, true)
    println("It takes '$steps' steps to reach the highest point, starting at any height of 1.")
}

fun buildGraph(heightmap: List<List<Int>>): PathGrid {
    val nodes: MutableList<MutableList<PathNode>> = mutableListOf()
    heightmap.forEachIndexed { y, row ->
        nodes.add(mutableListOf())
        row.forEachIndexed { x, col ->
            val node = PathNode(Vector2(x, y), col)
            val nodesInRow = nodes[y]
            nodesInRow.add(node)
        }
    }

    val grid = PathGrid(nodes)
    grid.forEachNode { node ->
        grid.getNeighbouringCrossNodes(node)
            .forEach { neighbour ->
                if (neighbour.height <= node.height + 1) {
                    node.connections.add(neighbour)
                }
            }
    }

    return grid
}

data class PathGrid(val nodes: List<List<PathNode>>) {
    private val maxX = nodes.first().last().coordinate.x
    private val maxY = nodes.last().first().coordinate.y

    private fun getNode(x: Int, y: Int): PathNode? {
        if (x < 0 || x > maxX || y < 0 || y > maxY) {
            return null
        }
        return nodes[y][x]
    }

    fun getNeighbouringCrossNodes(node: PathNode): List<PathNode> =
        getNeighbouringCrossNodes(node.coordinate.x, node.coordinate.y)

    private fun getNeighbouringCrossNodes(x: Int, y: Int): List<PathNode> {
        val up = getNode(x, y - 1)
        val down = getNode(x, y + 1)
        val left = getNode(x - 1, y)
        val right = getNode(x + 1, y)
        return listOfNotNull(up, down, left, right)
    }

    fun forEachNode(func: (PathNode) -> Unit) {
        nodes.forEach { row ->
            row.forEach { col ->
                func(col)
            }
        }
    }

    fun calculateShortestPath(start: Vector2, end: Vector2, startAtAnyA: Boolean): Int {
        /* Setup */
        val distTable = mutableMapOf<Vector2, Int>()
        val unvisitedNodes = mutableListOf<PathNode>()
        forEachNode {
            unvisitedNodes.add(it)
            distTable[it.coordinate] = Int.MAX_VALUE
        }
        distTable[start] = 0

        /* Build distTable */
        while (unvisitedNodes.isNotEmpty()) {
            /* Get next closest node */
            var node = unvisitedNodes.first()
            unvisitedNodes.forEach { next ->
                if (distTable[next.coordinate]!! < distTable[node.coordinate]!!) {
                    node = next
                }
            }
            unvisitedNodes.remove(node)

            /* Set the distance for each connected node, if it's closer than what we currently have */
            node.connections.forEach { connectedNode ->
                if (startAtAnyA && connectedNode.height == 1) {
                    distTable[connectedNode.coordinate] = 0
                } else if (distTable[connectedNode.coordinate]!! > distTable[node.coordinate]!! + 1) {
                    distTable[connectedNode.coordinate] = distTable[node.coordinate]!! + 1
                }
            }
        }

        return distTable.getOrElse(end) { -1 }
    }

}

data class PathNode(
    val coordinate: Vector2,
    val height: Int,
    val connections: MutableList<PathNode> = mutableListOf()
) {
    override fun toString() =
        "PathNode(coordinate=$coordinate,height=$height,connections=${connections.map { it.coordinate }}"
}

fun getHeightMap(input: List<String>): List<List<Int>> = input.map { line ->
    line.toCharArray().map { char ->
        when (char) {
            'S' -> 1
            'E' -> 26
            else -> char.code - 96
        }
    }
}

fun getStartCoordinate(input: List<String>) = findChar(input, 'S')

fun getEndCoordinate(input: List<String>) = findChar(input, 'E')

fun findChar(input: List<String>, charToFind: Char): Vector2 {
    input.forEachIndexed { y, line ->
        line.toCharArray().forEachIndexed { x, char ->
            if (char == charToFind) {
                return Vector2(x, y)
            }
        }
    }
    throw IllegalStateException("Input has no character '$charToFind'.")
}
