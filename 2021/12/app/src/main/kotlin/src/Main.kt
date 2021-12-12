package src

val SOURCE = Node("start", false)
val SINK = Node("end", false)

fun main() {
    val input = generateSequence(::readLine).toList()
    val pathPattern = """(\w+)-(\w+)""".toRegex()

    solveP1(transformInput(input, pathPattern))
    solveP2(transformInput(input, pathPattern))
}

fun solveP1(nodeEdges: Map<Node, List<Node>>) {
    transverse(SOURCE, nodeEdges, mutableSetOf())
        .also { println("P1 - all paths to sink $it") }
}

fun solveP2(nodeEdges: Map<Node, List<Node>>) {
    transverseLudicrousRules(SOURCE, nodeEdges, mutableMapOf(), false)
        .also { println("P2 - all paths to sink $it") }
}

fun transverseLudicrousRules(
    startingPoint: Node,
    nodeEdges: Map<Node, List<Node>>,
    visited: MutableMap<Node, Int>,
    hasTransversedTwice: Boolean
): Long {
    if (startingPoint == SINK) {
        return 1L
    }

    var caveVisitedTwice = hasTransversedTwice
    if (!startingPoint.isBigCave) {
        if (!visited.contains(startingPoint)) {
            visited[startingPoint] = 0
        }

        visited[startingPoint] = visited[startingPoint]!! + 1
        caveVisitedTwice = caveVisitedTwice || visited[startingPoint] == 2
    }

    var count = 0L
    for (adj in nodeEdges[startingPoint]!!) {
        if (adj != SOURCE) {
            if (!visited.contains(adj) || (!caveVisitedTwice && visited[adj]!! < 2)) {
                count += transverseLudicrousRules(adj, nodeEdges, visited.toMutableMap(), caveVisitedTwice)
            }
        }
    }

    return count
}

fun transverse(startingPoint: Node, nodeEdges: Map<Node, List<Node>>, visited: MutableSet<Node>): Long {
    if (startingPoint == SINK) {
        return 1L
    }

    if (!startingPoint.isBigCave) {
        visited.add(startingPoint)
    }

    var count = 0L
    for (adj in nodeEdges[startingPoint]!!) {
        if (!visited.contains(adj)) {
            count += transverse(adj, nodeEdges, visited.toMutableSet())
        }
    }

    return count
}

private fun transformInput(
    input: List<String>,
    pathPattern: Regex
): Map<Node, List<Node>> {
    return input.fold(mutableMapOf<Node, MutableList<Node>>()) { acc, path ->
        val (first, second) = pathPattern.matchEntire(path)!!.destructured
        val firstNode = Node(first, first[0].isUpperCase())
        val secondNode = Node(second, second[0].isUpperCase())
        if (!acc.contains(firstNode)) {
            acc[firstNode] = mutableListOf()
        }

        if (!acc.contains(secondNode)) {
            acc[secondNode] = mutableListOf()
        }

        acc[firstNode]!!.add(secondNode)
        acc[secondNode]!!.add(firstNode)

        acc
    }
}

data class Node(val name: String, val isBigCave: Boolean = false)
