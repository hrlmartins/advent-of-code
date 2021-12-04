/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package src

fun main() {
    val input = generateSequence(::readLine).toList()
    val drawList = input[0].split(",").map { it.toInt() }
    // index 0 and 1 are the draw list and the new line. So te board input starts at line 2

    val rawBoards = input.slice(2 until input.size).filter { it.isNotBlank() }
    solveP1(readBoards(rawBoards), drawList)
    solveP2(readBoards(rawBoards), drawList)
}

fun solveP1(boards: List<BingoBoard>, drawList: List<Int>) {
    for (number in drawList) {
        for (board in boards) {
            board.mark(number)
            if (board.isPrizedBoard()) {
                println("Sum of unmarked multiply by last number drawn: ${number * board.sumUnmarked()}")
                return
            }
        }
    }
}

fun solveP2(boards: List<BingoBoard>, drawList: List<Int>) {
    for (number in drawList) {
        for (board in boards) {
            board.mark(number)
            if (!board.prized && board.isPrizedBoard()) {
                board.prized = true
                println("P2 - Sum of unmarked multiply by last number drawn: ${number * board.sumUnmarked()}")
            }
        }
    }
}

fun readBoards(boards: List<String>): List<BingoBoard> {
    return boards.chunked(5).map { readRawBoard(it) }
}

fun readRawBoard(rawBoard: List<String>): BingoBoard {
    val board: Array<Array<BingoNumber>> = Array(5) { Array(5) { BingoNumber(0) } }

    for (row in 0 until 5) {
        val pattern = """\d+""".toRegex()
        val values = pattern.findAll(rawBoard[row]).toList().map { it.value.toInt() }
        for (column in 0 until 5) {
            board[row][column] = BingoNumber(values[column])
        }
    }

    return BingoBoard(board)
}

data class BingoNumber(val number: Int, var marked: Boolean = false)

// The functions to understand the winning situation to find a number and all that are all in dumb form
// and thus do unnecessary n^2 scans repeatedly. This could be counter-acted keeping more state like the position
// of a given number if it exists directly on a map. More things could be done here like that but this works for 5x5
// boards :D
class BingoBoard(val board: Array<Array<BingoNumber>> = arrayOf(), var prized: Boolean = false) {
    fun mark(num: Int) {
        val (row, column) = findPos(num)
        if (row != -1 && column != -1) {
            this.board[row][column].marked = true
        }
    }

    fun isPrizedBoard(): Boolean {
        return checkHorizontal() || checkVertical()
    }

    fun sumUnmarked(): Int {
       return board.sumOf { row -> row.sumOf { if (!it.marked) it.number else 0 } }
    }

    private fun findPos(num: Int): Pair<Int, Int> {
        for (row in this.board.indices) {
            for (column in this.board[row].indices) {
                if (this.board[row][column].number == num) {
                    return Pair(row, column)
                }
            }
        }

        return Pair(-1, -1)
    }

    private fun checkHorizontal(): Boolean {
        for (i in this.board.indices) {
            var won = true
            for (j in this.board[i].indices) {
                won = won && this.board[i][j].marked
            }

            if (won) return true
        }

        return false
    }

    private fun checkVertical(): Boolean {
        for (i in this.board.indices) {
            var won = true
            for (j in this.board[i].indices) {
                won = won && this.board[j][i].marked
            }

            if (won) return true
        }

        return false
    }
}