/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package src

fun main() {
    val input = generateSequence(::readLine).toList()

    val columnSize = input[0].length
    solveP1(input, columnSize)
    solveP2(input, columnSize)

}

fun solveP1(input: List<String>, columnSize: Int) {
    println("simple consumption calc: " +
            "${calcConsumptionRate(input,0, columnSize, true).toInt(2) * 
                    calcConsumptionRate(input,0, columnSize, false).toInt(2)}")
}

fun solveP2(input: List<String>, columnSize: Int) {
    val oxygenRating = calcRating(input, columnSize, 0, true).first().toInt(2)
    val scrubberRating = calcRating(input, columnSize, 0, false).first().toInt(2)

    println("life support rating: ${scrubberRating * oxygenRating}")
}

fun calcRating(numbers: List<String>, columnSize: Int, currentCol: Int, mostCommon: Boolean): List<String> {
    if (numbers.size == 1) return numbers

    val bitOneCount = calcColumnOccurrence(numbers, currentCol, 1)
    val bitZeroCount = calcColumnOccurrence(numbers, currentCol, 0)

    val possibleRatings =
        when(mostCommon) {
            true -> {
                if (bitOneCount >= bitZeroCount) {
                    numbers.filter { it[currentCol].digitToInt() == 1 }
                } else {
                    numbers.filter { it[currentCol].digitToInt() == 0 }
                }
            }
            false -> {
                if (bitOneCount < bitZeroCount) {
                    numbers.filter { it[currentCol].digitToInt() == 1 }
                } else {
                    numbers.filter { it[currentCol].digitToInt() == 0 }
                }
            }
        }

    return calcRating(possibleRatings, columnSize, currentCol + 1, mostCommon)
}

fun calcConsumptionRate(input: List<String>, currentCol: Int, columnSize: Int, mostCommon: Boolean): String {
    if (currentCol >= columnSize) return ""

    val bitOneCount = calcColumnOccurrence(input, currentCol, 1)
    val bitZeroCount = calcColumnOccurrence(input, currentCol, 0)

    val result =
        when(mostCommon) {
            true -> if (bitZeroCount > bitOneCount) "0" else "1"
            false -> if (bitZeroCount < bitOneCount) "0" else "1"
        }

    return result + calcConsumptionRate(input, currentCol + 1, columnSize, mostCommon)
}

fun calcColumnOccurrence(numbers: List<String>, col: Int, value: Int): Int =
    numbers.count { it[col].digitToInt() == value }