package src

import java.util.*

// Each
fun main() {
    val input = generateSequence(::readLine).toList()
    val octopuses =
        input.map {
            it.split("")
                .filter { p -> p.isNotBlank() }
                .map { o -> DumbOctopus(o.toInt()) }
        }


    solveP1(octopuses.map { it.map { o -> o.copy() } }, 100)
    solveP2(octopuses)

}

fun solveP1(octopuses: List<List<DumbOctopus>>, steps: Int) {
    (0 until steps).sumOf {
        octopuses.withIndex().forEach { (row, octuposesLine) ->
            octuposesLine.withIndex().forEach { (col, octo) ->
                octo.powerLevel++
                if (octo.powerLevel > 9 && !octo.flashed) {
                    octo.flashed = true
                    visitNearOctuposes(octopuses, row, col)
                }

            }
        }

        resetFlashedOctuposes(octopuses)
    }.also { println("P1 - Sum of flashes $it") }
}


fun solveP2(octupuses: List<List<DumbOctopus>>) {
    (0 until Int.MAX_VALUE).sumOf { step ->
        octupuses.withIndex().forEach { (row, octoposesLine) ->
            octoposesLine.withIndex().forEach { (col, octo) ->
                octo.powerLevel++
                if (octo.powerLevel > 9 && !octo.flashed) {
                    octo.flashed = true
                    visitNearOctuposes(octupuses, row, col)
                }

            }
        }

        if (areAllFlashing(octupuses)) {
            println("P2 - the step that everyone is flashing is ${step + 1}")
            return
        }
        resetFlashedOctuposes(octupuses)
    }
}

fun visitNearOctuposes(octupuses: List<List<DumbOctopus>>, row: Int, col: Int) {
    val queue: Queue<Position> = LinkedList()
    val visited = mutableSetOf<Position>()
    val root = Position(row, col)
    queue.add(root)
    visited.add(root)

    while (queue.isNotEmpty()) {
        val explorePos = queue.poll()
        for (adj in generatePositions(explorePos.row, explorePos.col, octupuses.size, octupuses[0].size)) {
            if (!visited.contains(adj)) {
                val adjOcto = octupuses[adj.row][adj.col]
                adjOcto.powerLevel++
                if (adjOcto.powerLevel > 9 && !adjOcto.flashed) {
                    adjOcto.flashed = true
                    queue.add(adj)
                    visited.add(adj)
                }

            }
        }
    }
}

fun generatePositions(row: Int, col: Int, maxRows: Int, maxCols: Int): List<Position> {
    return listOf(
        Position(row + 1, col),
        Position(row, col + 1),
        Position(row - 1, col),
        Position(row, col - 1),
        Position(row + 1, col + 1),
        Position(row - 1, col - 1),
        Position(row - 1, col + 1),
        Position(row + 1, col - 1)
    ).filter { it.row in 0 until maxRows && it.col in 0 until maxCols }
}

fun resetFlashedOctuposes(octupuses: List<List<DumbOctopus>>): Int {
    return octupuses.sumOf { row ->
        row.count { octo ->
            if (octo.powerLevel > 9) {
                octo.powerLevel = 0
                octo.flashed = false
                return@count true
            }

            false
        }
    }
}

fun areAllFlashing(octupuses: List<List<DumbOctopus>>) = octupuses.all { it.all { octo -> octo.flashed } }

data class DumbOctopus(var powerLevel: Int, var flashed: Boolean = false)
data class Position(val row: Int, val col: Int)

// Fancy print
fun printOctuposes(octupuses: List<List<DumbOctopus>>) {
    octupuses.forEach { row ->
        row.forEach { col ->
            if (col.powerLevel > 9) {
                print("\u001B[33m*\u001B[0m")
            } else {
                print("*")
            }
        }
        println()
    }
    println()
}
