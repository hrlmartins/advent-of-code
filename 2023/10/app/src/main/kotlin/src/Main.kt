/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package src

import java.util.*

fun main() {
    val input = generateSequence(::readLine).toList()
    solveP1(input)
    solveP2(input)
}
fun solveP1(lines: List<String>) {
    val nodes = generateNodes(lines)
    val startingPos = nodes.entries.filter { it.value.type == Pipe.START }.first().key

    transformStartingNode(nodes, startingPos)
    val distMap = mutableMapOf<Node, Long>()
    search(nodes, startingPos, distMap)

    println("P1: ${distMap.values.max()}")

}

fun solveP2(lines: List<String>) {
    val nodes = generateNodes(lines)
    val floorNodes = generateOtherNodes(lines)
    val startingPos = nodes.entries.first { it.value.type == Pipe.START }.key
    transformStartingNode(nodes, startingPos)

    val distMap = mutableMapOf<Node, Long>()
    val loopNodes = search(nodes, startingPos, distMap)

    val rowOutside = distMap.keys.maxOf { it.pos.row } + 1
    val colOutside = distMap.keys.maxOf { it.pos.col } + 1

    var count = 0L
    (0..rowOutside).forEach { row ->
        var isInLoop = false
        (0..colOutside).forEach { col ->
            val currPos = Position(col, row)
            if (loopNodes.containsKey(currPos)) {
                if (connectNorth(loopNodes[currPos]!!)) {
                    isInLoop = !isInLoop
                }
            } else if ((floorNodes.containsKey(currPos) || nodes.containsKey(currPos)) && isInLoop) {
                count++
            }
        }
    }

    println("P2: $count")
}

private fun search(nodes: Map<Position, Node>, startingPos: Position, distMap: MutableMap<Node, Long>): Map<Position, Node> {
    val queue: Queue<Node> = LinkedList()
    val startingNode = nodes[startingPos]!!
    val visited = mutableSetOf(nodes[startingPos]!!)
    val loopNodes = mutableMapOf<Position, Node>()

    queue.add(startingNode)
    distMap[startingNode] = 0L

    while(queue.isNotEmpty()) {
        val nodeToVisit = queue.poll()
        val neighbours = generateConnections(nodeToVisit, nodeToVisit.pos)
        loopNodes[nodeToVisit.pos] = nodeToVisit

        neighbours.forEach { nearNode ->
            nodes[nearNode]?.let {
                if (!visited.contains(it)) {
                    queue.add(it)
                    distMap[it] = distMap[nodeToVisit]!! + 1L
                }
            }
        }

        visited.add(nodeToVisit)
    }

    return loopNodes
}


data class Position(val col: Int, val row: Int)
data class Node(val pos: Position, val type: Pipe)
enum class Pipe(val value: String) {
    START("S"),
    VERTICAL("|"),
    HORIZONTAL("-"),
    NORTHANDEAST("L"),
    NORTHANDWEST("J"),
    SOUTHANDWEST("7"),
    SOUTHANDEAST("F"),
    FLOOR(".");

    companion object {
        fun fromString(s: String): Pipe? {
            return when(s) {
                "|" -> VERTICAL
                "-" -> HORIZONTAL
                "L" -> NORTHANDEAST
                "J" -> NORTHANDWEST
                "7" -> SOUTHANDWEST
                "F" -> SOUTHANDEAST
                "S" -> START
                "." -> FLOOR
                else -> null
            }
        }
    }
}

private fun generateNodes(lines: List<String>): MutableMap<Position, Node> {
    return lines.foldIndexed(mutableMapOf<Position, Node>()) { row, acc, line ->
        line.forEachIndexed { col, value ->
            if (value != '.') {
                acc[Position(col, row)] = Node(Position(col, row), Pipe.fromString(value.toString())!!)
            }
        }
        acc
    }
}

private fun generateOtherNodes(lines: List<String>): MutableMap<Position, Node> {
    return lines.foldIndexed(mutableMapOf<Position, Node>()) { row, acc, line ->
        line.forEachIndexed { col, value ->
            if (value == '.') {
                acc[Position(col, row)] = Node(Position(col, row), Pipe.fromString(value.toString())!!)
            }
        }
        acc
    }
}

private fun transformStartingNode(nodes: MutableMap<Position, Node>, startingPos: Position) {
    val northNode = nodes[Position(startingPos.col, startingPos.row - 1)]
    val southNode = nodes[Position(startingPos.col, startingPos.row + 1)]
    val eastNode = nodes[Position(startingPos.col + 1, startingPos.row)]
    val westNode = nodes[Position(startingPos.col - 1, startingPos.row)]
    if (northNode != null && connectSouth(northNode)) {
        // ok so it connect's to the pipe above
        // What is the second pipe connection?
        if (southNode != null && connectNorth(southNode)) {
            // We now know the starting node is a vertical pipe... and so on and so forth
            nodes[startingPos] = Node(startingPos, Pipe.VERTICAL)
        } else if (eastNode != null && connectWest(eastNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.NORTHANDEAST)
        } else {
            nodes[startingPos] = Node(startingPos, Pipe.NORTHANDWEST)
        }
    } else if (southNode != null && connectNorth(southNode)) {
        if (northNode != null && connectSouth(northNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.VERTICAL)
        } else if (eastNode != null && connectWest(eastNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.SOUTHANDEAST)
        } else {
            nodes[startingPos] = Node(startingPos, Pipe.SOUTHANDWEST)
        }
    } else if (eastNode != null && connectWest(eastNode)) {
        if (northNode != null && connectSouth(northNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.NORTHANDEAST)
        } else if (southNode != null && connectNorth(southNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.SOUTHANDEAST)
        } else {
            nodes[startingPos] = Node(startingPos, Pipe.HORIZONTAL)
        }
    } else if (westNode != null && connectEast(westNode)) {
        if (northNode != null && connectSouth(northNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.NORTHANDWEST)
        } else if (southNode != null && connectNorth(southNode)) {
            nodes[startingPos] = Node(startingPos, Pipe.SOUTHANDWEST)
        } else {
            nodes[startingPos] = Node(startingPos, Pipe.HORIZONTAL)
        }
    }
}

private fun generateConnections(startingNode: Node, startingPos: Position) = when (startingNode.type) {
    Pipe.START -> TODO() // should not happen
    Pipe.VERTICAL -> listOf(Position(startingPos.col, startingPos.row + 1), Position(startingPos.col, startingPos.row - 1))
    Pipe.HORIZONTAL -> listOf(Position(startingPos.col - 1, startingPos.row), Position(startingPos.col + 1, startingPos.row))
    Pipe.NORTHANDEAST -> listOf(Position(startingPos.col, startingPos.row - 1), Position(startingPos.col + 1, startingPos.row))
    Pipe.NORTHANDWEST -> listOf(Position(startingPos.col, startingPos.row - 1), Position(startingPos.col - 1, startingPos.row))
    Pipe.SOUTHANDWEST -> listOf(Position(startingPos.col, startingPos.row + 1), Position(startingPos.col - 1, startingPos.row))
    Pipe.SOUTHANDEAST -> listOf(Position(startingPos.col, startingPos.row + 1), Position(startingPos.col + 1, startingPos.row))
    Pipe.FLOOR -> TODO()
}


fun connectSouth(node: Node): Boolean {
    return listOf(Pipe.SOUTHANDEAST, Pipe.SOUTHANDWEST, Pipe.VERTICAL).contains(node.type)
}

fun connectNorth(node: Node): Boolean {
    return listOf(Pipe.NORTHANDEAST, Pipe.NORTHANDWEST, Pipe.VERTICAL).contains(node.type)
}

fun connectEast(node: Node): Boolean {
    return listOf(Pipe.NORTHANDEAST, Pipe.SOUTHANDEAST, Pipe.HORIZONTAL).contains(node.type)
}

fun connectWest(node: Node): Boolean {
    return listOf(Pipe.NORTHANDWEST, Pipe.SOUTHANDWEST, Pipe.HORIZONTAL).contains(node.type)
}