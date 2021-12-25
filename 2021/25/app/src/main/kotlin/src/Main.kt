package src

fun main() {
    val input = generateSequence(::readLine).toList()
    val trenchMap = readInput(input)

    solveP1(trenchMap, input.size, input[0].length)
}

fun solveP1(trenchMap: Array<CharArray>, rows: Int, cols: Int) {
    var trenchMapAux = trenchMap
    (1..Int.MAX_VALUE).forEach { step ->
        val toCmp = Array(rows) { row -> trenchMapAux[row].copyOf() }
        val tmpMap = Array(rows) { row -> trenchMapAux[row].copyOf() }
        moveEast(rows, cols, trenchMapAux, tmpMap)

        trenchMapAux = Array(rows) { row -> tmpMap[row].copyOf() }
        moveSouth(rows, cols, trenchMapAux, tmpMap)
        
        if (boardsAreEqual(tmpMap, toCmp)) {
            println("P1 - $step")
            return
        }

        trenchMapAux = tmpMap
    }
}

private fun moveSouth(
    rows: Int,
    cols: Int,
    trenchMapAux: Array<CharArray>,
    tmpMap: Array<CharArray>
) {
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            if (trenchMapAux[row][col] == 'v') {
                val newRow = (row + 1) % rows
                if (trenchMapAux[newRow][col] == '.') {
                    tmpMap[newRow][col] = trenchMapAux[row][col]
                    tmpMap[row][col] = '.'
                }
            }
        }
    }
}

private fun moveEast(
    rows: Int,
    cols: Int,
    trenchMapAux: Array<CharArray>,
    tmpMap: Array<CharArray>
) {
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            if (trenchMapAux[row][col] == '>') {
                val newCol = (col + 1) % cols
                if (trenchMapAux[row][newCol] == '.') {
                    tmpMap[row][newCol] = trenchMapAux[row][col]
                    tmpMap[row][col] = '.'
                }
            }
        }
    }
}

fun boardsAreEqual(tmpMap: Array<CharArray>, trenchMapAux: Array<CharArray>): Boolean {
    return tmpMap.withIndex().all { (row, cols) ->
        cols.withIndex().all { (col, cbr) -> trenchMapAux[row][col] == cbr }
    }
}

private fun readInput(input: List<String>) =
    input.withIndex().fold(Array(input.size) { CharArray(input[0].length) }) { acc, (row, line) ->
        line.withIndex().forEach { (col, cbr) ->
            acc[row][col] = cbr
        }

        acc
    }
