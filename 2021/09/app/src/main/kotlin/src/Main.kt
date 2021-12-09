package src

import java.util.*

fun main() {
    val input = generateSequence(::readLine).toList()
    val heightMap = input.map { line -> line.split("").filter { it.isNotBlank() }.map { h -> h.toInt() } }
    val colSize = heightMap[0].size

    solveP1(heightMap, colSize).also { println("P1 - The sum of 1 and to all low points is: $it") }
    solveP2(heightMap, colSize).also { println("P2 - the multiplication of all basin sizes is: $it") }
}

fun solveP1(heightMap: List<List<Int>>, colSize: Int): Int =
    heightMap.withIndex().sumOf { (row, rowList) ->
        rowList.withIndex().sumOf { (col, value) ->
            val allHigher = allAdjacentHigher(row, col, heightMap, colSize)
            if (allHigher) 1 + heightMap[row][col] else 0
        }
    }

fun solveP2(heightMap: List<List<Int>>, colSize: Int): Long =
    findAllLowPoints(heightMap, colSize).map { basinSize(heightMap, it) }
        .sortedDescending()
        .slice(0 until 3)
        .reduce { acc, i -> acc * i }

fun basinSize(heightMap: List<List<Int>>, lowPoint: Node): Long {
    var count = 1L
    val queue: Queue<Node> = LinkedList()
    val visited = mutableSetOf<Node>()
    queue.add(lowPoint)
    visited.add(lowPoint)

    while (queue.isNotEmpty()) {
        val exploreNode = queue.poll()
        val adjacentPoints = generateAllAdjacentPoints(exploreNode.pos, heightMap.size, heightMap[0].size)
        for (adj in adjacentPoints)
            if (!visited.contains(adj) && isValidPosition(heightMap, exploreNode, adj)) {
                count++
                queue.add(adj)
                visited.add(adj)
            }
    }

    return count
}

fun isValidPosition(heightMap: List<List<Int>>, currentNode: Node, adjNode: Node): Boolean {
    return heightMap[currentNode.pos.first][currentNode.pos.second] < heightMap[adjNode.pos.first][adjNode.pos.second]
            && heightMap[adjNode.pos.first][adjNode.pos.second] != 9

}

fun generateAllAdjacentPoints(pos: Pair<Int, Int>, rows: Int, columns: Int): List<Node> {
    return listOf(Pair(0, 1), Pair(0, -1), Pair(-1, 0), Pair(1, 0)).flatMap {
        val adjacentPos = Pair(pos.first + it.first, pos.second + it.second)
        if (adjacentPos.first in 0 until rows && adjacentPos.second in 0 until columns) {
            listOf(Node(Pair(adjacentPos.first, adjacentPos.second)))
        } else {
            listOf()
        }
    }
}

fun findAllLowPoints(heightMap: List<List<Int>>, colSize: Int): List<Node> =
    heightMap.withIndex().flatMap { (row, rowList) ->
        rowList.withIndex().flatMap { (col, value) ->
            if (allAdjacentHigher(row, col, heightMap, colSize))
                listOf(Node(Pair(row, col)))
            else
                listOf()
        }
    }

private fun allAdjacentHigher(
    row: Int,
    col: Int,
    heightMap: List<List<Int>>,
    colSize: Int
): Boolean {
    return listOf(Pair(0, 1), Pair(0, -1), Pair(-1, 0), Pair(1, 0)).all { posShift ->
        val adjacentPos = Pair(row + posShift.first, col + posShift.second)
        if (adjacentPos.first in heightMap.indices && adjacentPos.second in 0 until colSize) {
            heightMap[row][col] < heightMap[adjacentPos.first][adjacentPos.second]
        } else {
            true
        }
    }
}

data class Node(val pos: Pair<Int, Int>)
