package src

import kotlin.math.ceil
import kotlin.math.max

fun main() {
    val input = generateSequence(::readLine).toList()

    val numbers = input.map { parseNumber(it, ParseInformation(0, null)).expression!! }

    solveP1(numbers.toMutableList())
    solveP2(numbers.toMutableList())
}

fun solveP1(numbers: MutableList<Number>) {
    val firstNumber = numbers.removeFirst()

    val reducedTotalNumber = numbers.fold(firstNumber) { acc, number ->
        applyReduce(Number.NumberPair(acc, number))
    }
    println("The magnitude of the number is: ${reducedTotalNumber.accept(MagnitudeVisitor())}")
}

fun solveP2(numbers: MutableList<Number>) {
    numbers.maxOf { i ->
        numbers.maxOf { j ->
            if (i != j) {
                applyReduce(Number.NumberPair(i, j)).accept(MagnitudeVisitor())
            } else {
                0L
            }
        }
    }.also { println("The maximum magnitude obtained is $it") }
}

fun applyReduce(number: Number): Number {
    var previousNumber: Number? = null
    var currentNumber = number

    while (previousNumber != currentNumber) {
        previousNumber = currentNumber
        val reducer =
            if (explosionExists(currentNumber, 1))
                ReduceProcessor(existsExplosion = true)
            else
                ReduceProcessor()

        currentNumber =
            reducer.process(currentNumber, 1).number
    }

    return currentNumber
}

fun explosionExists(currentNumber: Number, depth: Int): Boolean {
    return when (currentNumber) {
        is Number.Literal -> false
        is Number.NumberPair ->
            if (depth == 5) {
                true
            } else {
                explosionExists(currentNumber.left, depth + 1) ||
                        explosionExists(currentNumber.right, depth + 1)
            }
    }
}

fun parseNumber(input: String, parseInfo: ParseInformation): ParseInformation {
    if (parseInfo.idx >= input.length) return parseInfo

    return when (val codeChar = input[parseInfo.idx]) {
        '[' -> {
            parseBracket(input, parseInfo)
        }
        else -> ParseInformation(parseInfo.idx + 1, Number.Literal(codeChar.digitToInt().toLong()))
    }
}

fun parseBracket(input: String, parseInfo: ParseInformation): ParseInformation {
    val left = parseNumber(input, parseInfo.copy(idx = parseInfo.idx + 1))
    val right = parseNumber(input, left.copy(idx = left.idx + 1))

    return ParseInformation(right.idx + 1, Number.NumberPair(left.expression!!, right.expression!!))
}

/****************************************************************************************
 * *****************************************************
 * Tokens and Visitor
 * *****************************************************
 ****************************************************************************************/
data class ParseInformation(var idx: Int, val expression: Number?)

interface Visitor {
    fun visit(number: Number): Long
}

sealed class Number {
    fun accept(visitor: Visitor) = visitor.visit(this)

    data class Literal(val value: Long) : Number() {
        override fun toString(): String {
            return value.toString()
        }
    }

    data class NumberPair(val left: Number, val right: Number) : Number() {
        override fun toString(): String {
            return "[$left, $right]"
        }
    }
}

class MagnitudeVisitor : Visitor {
    override fun visit(number: Number): Long {
        return when (number) {
            is Number.Literal -> number.value
            is Number.NumberPair ->
                (3 * number.left.accept(this)) + (2 * number.right.accept(this))
        }
    }

}


/****************************************************************************************
 * *****************************************************
 * Reducer
 * *****************************************************
 ****************************************************************************************/
data class ReduceInformation(val explodedNumber: Boolean, val number: Number)

class ReduceProcessor(
    private val leftAddition: MutableList<Long> = mutableListOf(),
    private val rightAddition: MutableList<Long> = mutableListOf(),
    private val existsExplosion: Boolean = false,
    private var reducedHappened: Boolean = false
) {

    fun process(number: Number, depth: Int): ReduceInformation {
        return when (number) {
            is Number.Literal -> process(number, depth)
            is Number.NumberPair -> process(number, depth)
        }
    }

    fun process(literal: Number.Literal, depth: Int): ReduceInformation {
        if (existsExplosion || reducedHappened || literal.value < 10)
            return ReduceInformation(false, literal)

        reducedHappened = true
        return ReduceInformation(
            false,
            Number.NumberPair(
                Number.Literal(literal.value / 2),
                Number.Literal(ceil(literal.value / 2.0).toLong())
            )
        )
    }

    fun process(pair: Number.NumberPair, depth: Int): ReduceInformation {
        if (reducedHappened) return ReduceInformation(false, pair)

        if (depth == 5) {
            leftAddition.add((pair.left as Number.Literal).value)
            rightAddition.add((pair.right as Number.Literal).value)
            reducedHappened = true
            return ReduceInformation(true, Number.Literal(0))
        }

        val newLeft = process(pair.copy().left, depth + 1)
        if (rightAddition.isNotEmpty()) {
            return ReduceInformation(
                newLeft.explodedNumber,
                pair.copy(left = newLeft.number, right = incrementRightNeighbour(pair.right))
            )
        }

        val newRight = process(pair.copy().right, depth + 1)
        if (leftAddition.isNotEmpty() && !newLeft.explodedNumber) {
            return ReduceInformation(
                newRight.explodedNumber,
                pair.copy(left = incrementLeftNeighbour(newLeft.number), right = newRight.number)
            )
        }

        return ReduceInformation(
            newLeft.explodedNumber || newRight.explodedNumber,
            pair.copy(left = newLeft.number, right = newRight.number)
        )
    }


    // Follow the left path until we find a literal
    // use the list of right values to add to the literal
    // we are finding the neighbour of the right element of an explosion
    private fun incrementRightNeighbour(number: Number): Number {
        return when (number) {
            is Number.Literal -> number.copy(number.value + rightAddition.removeFirst())
            is Number.NumberPair -> number.copy(left = incrementRightNeighbour(number.left), right = number.right)
        }
    }

    private fun incrementLeftNeighbour(number: Number): Number {
        return when (number) {
            is Number.Literal -> number.copy(number.value + leftAddition.removeFirst())
            is Number.NumberPair -> number.copy(left = number.left, right = incrementLeftNeighbour(number.right))
        }
    }
}
