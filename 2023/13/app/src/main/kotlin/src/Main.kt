/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package src

fun main() {
    val input = generateSequence(::readLine).toList()
    solveP1(input)
    solveP2(input)
}
fun solveP1(lines: List<String>) {
    val rawPatterns = processRawPattern(lines)

    var globalIdx = 0
    rawPatterns.sumOf { firstPattern ->
        globalIdx++
        if (firstPattern.isEmpty()) {
            return@sumOf 0L
        }

        val firstPatternMatrix = createMatrix(firstPattern)
        // check verticl
        val (vertMirrors: Reflection?, horMirrors: Reflection?) = calculate(firstPatternMatrix)
        val pReflection = if (vertMirrors == null) { horMirrors!! } else vertMirrors!!
        if (pReflection.isVertical) pReflection.pos.toLong() else (100L * pReflection.pos.toLong())
    }.also { println("P1: $it") }
}

fun solveP2(lines: List<String>) {
    val rawPatterns = processRawPattern(lines)

    var globalIdx = 0
    rawPatterns.sumOf { firstPattern ->
        globalIdx++
        if (firstPattern.isEmpty()) {
            return@sumOf 0L
        }

        val firstPatternMatrix = createMatrix(firstPattern)
        val (originalVertMirror: Reflection?, originalHorMirror: Reflection?) = calculate(firstPatternMatrix)
        val origPerfReflection = originalVertMirror ?: originalHorMirror

        // check vertical
        var vertMirrors: Reflection? = null
        var horMirrors: Reflection? = null
        run testSmudge@ {
            for (row in 0 until firstPatternMatrix.rows) {
                for (col in 0 until firstPatternMatrix.cols) {
                    val copyPattern = Array(firstPatternMatrix.rows) { firstPatternMatrix.matrix[it].clone() }
                    if (firstPatternMatrix.matrix[row][col] == '.') copyPattern[row][col] = '#' else copyPattern[row][col] = '.'

                    val (newVertReflection, newHorReflection) = calculate(MatrixData(firstPatternMatrix.rows, firstPatternMatrix.cols, copyPattern), origPerfReflection)

                    if (newHorReflection != null && newHorReflection != originalHorMirror) {
                        horMirrors = newHorReflection
                        return@testSmudge
                    }

                    if (newVertReflection != null && newVertReflection !== originalVertMirror) {
                        vertMirrors = newVertReflection
                        return@testSmudge
                    }
                }
            }
        }

        val pReflection = if (vertMirrors == null) { horMirrors!! } else vertMirrors!!
        if (pReflection.isVertical) pReflection.pos.toLong() else (100L * pReflection.pos.toLong())
    }.also { println("P2: $it") }
}

private fun calculate(firstPatternMatrix: MatrixData, originalReflection: Reflection? = null): Pair<Reflection?, Reflection?> {
    var vertMirrors: Reflection? = null
    run vertCheck@{
        (1 until firstPatternMatrix.cols).forEach { x ->
            val origColumn = produceVertCompString(firstPatternMatrix, x - 1, x)

            if (origColumn.first == origColumn.second) {
                if (x >= (firstPatternMatrix.cols - 1) || x <= 1) {
                    val tmp = Reflection(1, x, true)
                    if (tmp == originalReflection) return@forEach
                    vertMirrors = tmp
                    return@vertCheck
                } else {
                    var count = 0
                    var frontIdx = x
                    var backIdx = x - 1

                    var surroundingColumn: Pair<String, String>
                    do {
                        surroundingColumn = produceVertCompString(firstPatternMatrix, backIdx, frontIdx)
                        if (surroundingColumn.first == surroundingColumn.second) count++
                        frontIdx += 1
                        backIdx -= 1
                    } while (surroundingColumn.first == surroundingColumn.second &&
                            frontIdx <= (firstPatternMatrix.cols - 1) &&
                            backIdx >= 0)

                    if ((frontIdx >= firstPatternMatrix.cols || backIdx < 0) &&
                            surroundingColumn.first == surroundingColumn.second) {
                        val tmp = Reflection(count, x, true)
                        if (tmp == originalReflection) return@forEach
                        vertMirrors = tmp
                        return@vertCheck
                    }
                }
            }
        }
    }

    // check horizontal
    var horMirrors: Reflection? = null
    run horCheck@{
        (1 until firstPatternMatrix.rows).forEach { y ->

            val origRow = produceHorizontalCompString(firstPatternMatrix, y - 1, y)

            if (origRow.first == origRow.second) {
                if (y >= (firstPatternMatrix.rows - 1) || y <= 1) {
                    val tmp =  Reflection(1, y, false)
                    if (tmp == originalReflection) return@forEach
                    horMirrors = tmp
                    return@horCheck
                } else {
                    var count = 0
                    var frontIdx = y
                    var backIdx = y - 1

                    var surroundingRows: Pair<String, String>
                    do {
                        surroundingRows = produceHorizontalCompString(firstPatternMatrix, backIdx, frontIdx)
                        if (surroundingRows.first == surroundingRows.second) count++
                        frontIdx += 1
                        backIdx -= 1
                    } while (surroundingRows.first == surroundingRows.second &&
                            frontIdx <= (firstPatternMatrix.rows - 1) &&
                            backIdx >= 0)

                    if ((frontIdx >= firstPatternMatrix.rows || backIdx < 0) &&
                            surroundingRows.first == surroundingRows.second) {
                        val tmp = Reflection(count, y, false)
                        if (tmp == originalReflection) return@forEach
                        horMirrors = tmp
                        return@horCheck
                    }
                }
            }
        }
    }
    return Pair(vertMirrors, horMirrors)
}

private fun produceHorizontalCompString(firstPatternMatrix: MatrixData, previousY: Int, currentY: Int): Pair<String, String> {
    val previous = firstPatternMatrix.matrix.get(previousY).joinToString("")
    val current = firstPatternMatrix.matrix.get(currentY).joinToString("")
    return Pair(previous, current)
}

private fun produceVertCompString(firstPatternMatrix: MatrixData, previousX: Int, currentX: Int): Pair<String, String> {
    var previous = ""
    var current = ""
    (0 until firstPatternMatrix.rows).forEach { y ->
        previous += firstPatternMatrix.matrix[y][previousX]
        current += firstPatternMatrix.matrix[y][currentX]
    }
    return Pair(previous, current)
}

private fun createMatrix(pattern: List<String>): MatrixData {
    val nRows = pattern.size
    val nCols = pattern.first().length

    val matrix = Array(nRows) { CharArray(nCols) }

    (0 until nRows).forEach { y ->
        (0 until nCols).forEach { x ->
            matrix[y][x] = pattern.get(y).get(x)
        }
    }
    return MatrixData(nRows, nCols, matrix)
}

data class MatrixData(val rows: Int, val cols: Int, val matrix: Array<CharArray>)
data class Reflection(val size: Int, val pos: Int, val isVertical: Boolean)

private fun processRawPattern(lines: List<String>): List<List<String>> {
    val patternsRaw = mutableListOf<List<String>>()
    val tempList = mutableListOf<String>()
    lines.forEach {
        if (it.isNullOrEmpty()) {
            patternsRaw.add(tempList.toList())
            tempList.clear()
        } else {
            tempList.add(it)
        }
    }

    patternsRaw.add(tempList)

    return patternsRaw
}
