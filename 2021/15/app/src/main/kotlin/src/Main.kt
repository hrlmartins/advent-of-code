package src

import java.util.*
import kotlin.math.sqrt

fun main() {
    val input = generateSequence(::readLine).toList()
    val nodes = readNodes(input)
    val source = Position(0, 0)
    val destination = Position(nodes.maxOf { it.pos.row }, nodes.maxOf { it.pos.col })

    solve(source, destination, nodes)
    println("========== expanded version =============== ")
    solve(source, destination, nodes, true)
}

fun solve(source: Position, destination: Position, nodes: List<Node>, expand: Boolean = false) {
    val prioQueue = PriorityQueue<Node>(compareBy { it.risk })
    val visited = mutableSetOf<Position>()
    val graph = Graph(sqrt(nodes.size.toDouble()).toInt())
    nodes.forEach { graph.addNode(it, expand) }

    val finalDestination =
        if (!expand) {
            destination
        } else {
            val shift = sqrt(nodes.size.toDouble()).toInt() * 4
            Position(destination.row + shift, destination.col + shift)
        }


    prioQueue.offer(Node(source, 0))
    visited.add(source)

    while (prioQueue.isNotEmpty()) {
        val posShortestRisk = prioQueue.poll()
        if (posShortestRisk.pos == finalDestination) {
            println("you've reached your destination with ${posShortestRisk.risk}")
            break
        }

        for (adj in graph.getAdj(posShortestRisk)) {
            if (!visited.contains(adj.pos)) {
                prioQueue.offer(Node(adj.pos, posShortestRisk.risk + adj.risk))
                visited.add(adj.pos)
            }
        }
    }
}

private fun readNodes(input: List<String>) =
    input.withIndex().flatMap { (row, columns) ->
        columns.withIndex().map { (col, node) ->
            Node(Position(row, col), node.digitToInt())
        }
    }

data class Position(val row: Int, val col: Int)
data class Node(val pos: Position, val risk: Int)

class Graph(val sideLen: Int) {
    val nodes: MutableMap<Position, Node> = mutableMapOf()
    val adjList: MutableMap<Position, MutableList<Node>> = mutableMapOf()

    fun addNode(source: Node, expand: Boolean = false) {
        nodes.putIfAbsent(source.pos, source)
        adjList.putIfAbsent(source.pos, mutableListOf())

        addAdjancentNodes(source)

        if (expand) {
            var dynamicSource = source
            for (rowFactor in 0 until 5) {
                val newRowPos = dynamicSource.pos.row + (rowFactor * this.sideLen)
                for (colFactor in 0 until 5) {
                    val extendedPosRight = Position(newRowPos, dynamicSource.pos.col + (colFactor * this.sideLen))
                    val extendedNodeRight = Node(extendedPosRight, ((dynamicSource.risk + (1 * colFactor)) - 1) % 9 + 1)
                    nodes.putIfAbsent(extendedNodeRight.pos, extendedNodeRight)
                    adjList.putIfAbsent(extendedNodeRight.pos, mutableListOf())
                    addAdjancentNodes(extendedNodeRight)
                }

                dynamicSource = Node(Position(dynamicSource.pos.row, dynamicSource.pos.col), dynamicSource.risk % 9 + 1)
            }
        }
    }

    fun getAdj(source: Node) = adjList[source.pos]!!
    fun getNode(pos: Position) = nodes[pos]!!

    private fun addAdjancentNodes(source: Node) {
        listOf(
            Position(0, 1),
            Position(1, 0),
            Position(-1, 0),
            Position(0, -1)
        ).forEach { pos ->
            val adjPos = Position(pos.row + source.pos.row, pos.col + source.pos.col)
            if (nodes.contains(adjPos)) {
                adjList[source.pos]!!.add(nodes[adjPos]!!)
                adjList[adjPos]!!.add(nodes[source.pos]!!)
            }
        }
    }
}
