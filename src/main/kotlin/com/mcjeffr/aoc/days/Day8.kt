package com.mcjeffr.aoc.days

import com.mcjeffr.aoc.common.PuzzleInput

fun main() {
    val input = PuzzleInput.getInput(8)
    val grid = parseGrid(input)

    part1(grid)
    part2(grid)
}

fun part1(grid: Grid) {
    val visibleTrees = grid.filter { tree ->
        grid.isVisible(tree.x, tree.y)
    }.size
    println("The number of visible trees from outside the grid is '$visibleTrees'.")
}

fun part2(grid: Grid) {
    val highestScenicScore = grid.map { tree ->
        grid.getScenicScore(tree.x, tree.y)
    }.max()
    println("The highest possible scenic score is '$highestScenicScore'.")
}

fun parseGrid(input: List<String>) = Grid(
    input.map { line ->
        line.toCharArray().map { it.digitToInt() }
    }
)

data class Grid(private val trees: List<List<Int>>) {

    fun isVisible(x: Int, y: Int): Boolean {
        val treesInCross = treesInCross(x, y)
        if (treesInCross.size < 4) {
            return true // Tree is on an edge
        }
        return treesInCross.values.any { line ->
            line.all { tree -> tree.height < trees[y][x] }
        }
    }

    fun getScenicScore(x: Int, y: Int): Int {
        val treesInCross = treesInCross(x, y)
        if (treesInCross.size < 4) {
            return 0 // Tree is on an edge
        }
        return treesInCross
            .mapValues { (_, line) -> line.takeUntilInclusive { tree -> tree.height >= trees[y][x] } }
            .values
            .map { it.size }
            .reduce { acc, i -> acc * i }
    }

    private fun treesInCross(x: Int, y: Int): Map<String, List<Tree>> =
        filter { tree ->
            !(tree.x == x && tree.y == y) && (tree.x == x || tree.y == y)
        }.groupBy { tree ->
            if (tree.x < x) {
                "left"
            } else if (tree.x > x) {
                "right"
            } else if (tree.y < y) {
                "up"
            } else {
                "down"
            }
        }.mapValues { (direction, trees) ->
            when (direction) {
                "left" -> trees.sortedByDescending { it.x }
                "right" -> trees.sortedBy { it.x }
                "up" -> trees.sortedByDescending { it.y }
                "down" -> trees.sortedBy { it.y }
                else -> trees
            }
        }

    fun filter(func: (tree: Tree) -> Boolean): List<Tree> {
        val filteredTrees: MutableList<Tree> = mutableListOf()
        trees.forEachIndexed { y, row ->
            row.forEachIndexed { x, tree ->
                if (func(Tree(x, y, tree))) {
                    filteredTrees.add(Tree(x, y, tree))
                }
            }
        }
        return filteredTrees
    }

    fun <T> map(func: (tree: Tree) -> T): List<T> {
        val mappedTrees: MutableList<T> = mutableListOf()
        trees.forEachIndexed { y, row ->
            row.forEachIndexed { x, tree ->
                mappedTrees.add(func(Tree(x, y, tree)))
            }
        }
        return mappedTrees
    }

    override fun toString() =
        trees.joinToString(separator = "") { row ->
            "${row.joinToString(separator = "") { "$it" }}\n"
        }

    data class Tree(val x: Int, val y: Int, val height: Int)

}

fun <T> List<T>.takeUntilInclusive(stopFunc: (item: T) -> Boolean): List<T> {
    val collected: MutableList<T> = mutableListOf()
    for (item in this) {
        collected.add(item)
        if (stopFunc(item)) {
            break
        }
    }
    return collected
}
