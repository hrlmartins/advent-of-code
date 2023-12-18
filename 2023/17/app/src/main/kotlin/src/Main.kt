/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package src

import java.util.*
import kotlin.math.max

fun main() {
    val input = generateSequence(::readLine).toList()
    solveP1(input)
    solveP2(input)
}

fun solveP1(lines: List<String>) {
    val nRows = lines.filter { it.isNotEmpty() }.size
    val nCols = lines.first { it.isNotEmpty() }.length
    val matrix = processInput(lines)
    val distMap = search(matrix, Coords(0, 0), Coords(nCols - 1, nRows - 1), false)
    println("P1 ${distMap.filter { it.key.x == nCols - 1 && it.key.y == nRows - 1 }.minBy { it.value }.value}")
}

fun solveP2(lines: List<String>) {
    val nRows = lines.filter { it.isNotEmpty() }.size
    val nCols = lines.first { it.isNotEmpty() }.length
    val matrix = processInput(lines)
    val distMap = search(matrix, Coords(0, 0), Coords(nCols - 1, nRows - 1), true)
    println("P2 ${distMap.filter { it.key.x == nCols - 1 && it.key.y == nRows - 1 }.minBy { it.value }.value}")
}

private fun search(matrix: Array<IntArray>, startingPos: Coords, destination: Coords, isBig: Boolean): Map<Key, Long> {
    val queue: Queue<VisitInfo> = PriorityQueue { p1, p2 -> p1.cost.compareTo(p2.cost) }
    val distMap = mutableMapOf<Key, Long>()
    val visited = mutableSetOf<Key>()

    queue.add(VisitInfo(startingPos, mutableListOf(), 0))
    distMap[Key.fromCoords(startingPos, listOf())] = 0L

    while (queue.isNotEmpty()) {
        val nodeToVisit = queue.poll()
        val neighbours = if (nodeToVisit.coords == destination && nodeToVisit.dirList.size >= 4) {
            listOf()
        } else {
            generateConnections(nodeToVisit, matrix.size, matrix.first().size, isBig)
        }

        val currDist = distMap[Key.fromCoords(nodeToVisit.coords, nodeToVisit.dirList)]!!
        neighbours.forEach { neighbour ->
            val newCost = currDist + matrix[neighbour.y][neighbour.x]
            val forwardDir = neighbour.dir!!
            val newVisit =
                    if (nodeToVisit.dirList.isNotEmpty() && forwardDir == nodeToVisit.dirList.first()) {
                        val newDirs = nodeToVisit.dirList.toMutableList()
                        newDirs.add(forwardDir)

                        VisitInfo(neighbour, newDirs, newCost)
                    } else {
                        VisitInfo(neighbour, mutableListOf(forwardDir), newCost)
                    }

            if (!visited.contains(Key.fromCoords(neighbour, newVisit.dirList))) {
                if (!distMap.contains(Key.fromCoords(neighbour, newVisit.dirList)) || distMap[Key.fromCoords(neighbour, newVisit.dirList)]!! > newCost) {
                    distMap[Key.fromCoords(neighbour, newVisit.dirList)] = newCost
                    queue.add(newVisit)
                }
            }
        }

        visited.add(Key.fromCoords(nodeToVisit.coords, nodeToVisit.dirList))
    }

    return distMap
}

private fun generateConnections(nodeToVisit: VisitInfo, rows: Int, cols: Int, isBig: Boolean): List<Coords> {
    val coords = nodeToVisit.coords
    val res = mutableListOf<Coords>()
    val forbiddenDirs = if (nodeToVisit.dirList.isEmpty()) {
        mutableListOf()
    } else {
        when (nodeToVisit.dirList.first()) {
            Direction.EAST -> mutableListOf(Direction.WEST)
            Direction.WEST -> mutableListOf(Direction.EAST)
            Direction.NORTH -> mutableListOf(Direction.SOUTH)
            Direction.SOUTH -> mutableListOf(Direction.NORTH)
        }
    }

    if (isBig) {
        if (nodeToVisit.dirList.size < 4 && nodeToVisit.dirList.isNotEmpty()) {
            Direction.entries.filter { it != nodeToVisit.dirList.first() }.forEach { forbiddenDirs.add(it) }
        }
    }

    if ((nodeToVisit.dirList.size == 3 && !isBig) || (nodeToVisit.dirList.size == 10 && isBig)) {
        // moved 3 times in same direction, cannot do that again
        forbiddenDirs.add(nodeToVisit.dirList.first())
    }

    for (dir in Direction.entries) {
        if (forbiddenDirs.contains(dir)) continue

        val newCoords = when (dir) {
            Direction.EAST -> coords.copy(x = coords.x + 1, dir = Direction.EAST)
            Direction.WEST -> coords.copy(x = coords.x - 1, dir = Direction.WEST)
            Direction.NORTH -> coords.copy(y = coords.y - 1, dir = Direction.NORTH)
            Direction.SOUTH -> coords.copy(y = coords.y + 1, dir = Direction.SOUTH)
        }

        if (newCoords.x >= 0 && newCoords.y >= 0 && newCoords.x < cols && newCoords.y < rows) {
            res.add(newCoords)
        }
    }

    return res
}

// Probably unnecessary and could just use visit info as key from the visited
// TODO refactor later
data class Key(val x: Int, val y: Int, val path: List<Direction> = emptyList()) {
    companion object {
        fun fromCoords(coords: Coords, dirs: List<Direction>): Key {
            return Key(coords.x, coords.y, dirs.toList())
        }
    }
}

data class Coords(val x: Int, val y: Int, val dir: Direction? = null)
data class VisitInfo(val coords: Coords, val dirList: MutableList<Direction>, val cost: Long)

enum class Direction {
    EAST,
    WEST,
    NORTH,
    SOUTH,
}

private fun processInput(input: List<String>): Array<IntArray> {
    val nRows = input.size
    val nCols = input.first().length

    val matrix = Array(nRows) { IntArray(nCols) }

    (0 until nRows).forEach { y ->
        (0 until nCols).forEach { x ->
            matrix[y][x] = input[y][x].digitToInt()
        }
    }
    return matrix
}
