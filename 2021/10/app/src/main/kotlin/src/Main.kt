package src

import java.util.*

// Each
fun main() {
    val input = generateSequence(::readLine).toList()

    val mapOpenClosing = mapOf(
        "{" to "}",
        "[" to "]",
        "(" to ")",
        "<" to ">"
    )
    val mapIllegalToPoints = mapOf(
        ")" to 3L,
        "]" to 57L,
        "}" to 1197L,
        ">" to 25137L
    )

    solveP1(input, mapOpenClosing, mapIllegalToPoints)

    val mapCompleteToPoints = mapOf(
        ")" to 1L,
        "]" to 2L,
        "}" to 3L,
        ">" to 4L
    )

    solveP2(input, mapOpenClosing, mapCompleteToPoints, mapIllegalToPoints)
}

fun solveP1(lines: List<String>, mapOpenClosing: Map<String, String>, mapIllegalToPoints: Map<String, Long>) {
    lines.sumOf { processLine(it, mapOpenClosing, mapIllegalToPoints) }.also { println("P1 - Sum of errors is $it") }
}

fun solveP2(
    lines: List<String>,
    mapOpenClosing: Map<String, String>,
    mapCompleteToPoints: Map<String, Long>,
    mapIllegalToPoints: Map<String, Long>
) {
    lines
        // filter out the corrupted lines. Leaves us with complete and incomplete lines
        .filter { processLine(it, mapOpenClosing, mapIllegalToPoints) == 0L }
        .map { processAutoCompleteLines(it, mapOpenClosing, mapCompleteToPoints) }
        .sorted()
        .also { println("P2 - Autocomplete score is ${it[it.size / 2]}") }
}

fun processAutoCompleteLines(
    line: String,
    mapOpenClosing: Map<String, String>,
    mapCompleteToPoints: Map<String, Long>
): Long {
    val symbolStack: Deque<String> = ArrayDeque()
    line.forEach {
        val symbolStr = it.toString()
        if (symbolStr in mapOpenClosing.keys) {
            symbolStack.push(symbolStr)
        } else {
            symbolStack.pop()
        }
    }

    return symbolStack.fold(0L) { acc, symbol ->
        val matchingClosing = mapOpenClosing[symbol]!!
        acc * 5 + mapCompleteToPoints[matchingClosing]!!
    }
}

fun processLine(line: String, mapOpenClosing: Map<String, String>, mapIllegalToPoints: Map<String, Long>): Long {
    val symbolStack: Deque<String> = ArrayDeque()
    line.forEach {
        val symbolStr = it.toString()
        if (symbolStr in mapOpenClosing.keys) {
            // it's an open symbol let's push it in the stack
            symbolStack.push(symbolStr)
        } else {
            // it's a closing symbol let's check if ot matches the stack
            val symbolToClose = symbolStack.pop()
            if (mapOpenClosing[symbolToClose] != symbolStr) return mapIllegalToPoints[symbolStr]!!
        }

    }

    return 0
}
