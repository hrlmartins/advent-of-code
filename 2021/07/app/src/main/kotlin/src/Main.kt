package src

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

fun main() {
    val input = generateSequence(::readLine).toList()
    val crabPositions = input.flatMap { it.split(",") }.map { it.toInt() }

    val medianPoint = calcMedian(crabPositions.sorted())
    val fuelCost = crabPositions.map { abs(it - medianPoint) }.sum()
    println("P1 best position $medianPoint for all is: $fuelCost -> ${fuelCost.toInt()}")

    val average = crabPositions.average()
    val nextPosFuel = crabPositions.map { calcSum(abs(it - ceil(average).toInt())) }.sum()
    val previousPosFuel = crabPositions.map { calcSum(abs(it - floor(average).toInt())) }.sum()
    println(
        "p2 has two possible pos: ${ceil(average)} and ${floor(average)} the minimum cost of both is: ${
            min(
                nextPosFuel,
                previousPosFuel
            )
        }"
    )
}

fun calcMedian(positions: List<Int>) =
    if (positions.size % 2 == 0) {
        (positions[positions.size / 2] + positions[(positions.size - 1) / 2]) / 2.0
    } else {
        positions[positions.size / 2].toDouble()
    }

fun calcSum(n: Int): Int {
    return (n * (n + 1)) / 2
}
