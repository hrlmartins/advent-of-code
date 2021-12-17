package src

import kotlin.math.abs
import kotlin.math.max

fun main() {
    val trenchXmin = 211
    val trenchXmax = 232
    val trenchYmin = -124
    val trenchYmax = -69

    // Imma brute force... but maybe only reasonable values
    // We know the trench is ahead of us and that when x reaches 0 velocity it doesnt change again
    // we know it decreases by 1 until 0 (sum of integers anyone?!?! :D)
    // use the sum of integers and resolve for n such that the sum is at least x_min
    // the max is x_max. It reaches the x_max edge in one step

    val minXvel = findMinVelocityX(trenchXmin)
    println("minx: $minXvel - maxx: $trenchXmax")

    // now the y.... we know it's below us... so the minimal speed would be y_min. Why?
    // Because that is the value that will for sure be on the bottom edge on the first step
    // Now the max.. I have no clue :D Gonna put the absolute value of min.... why? because before it starts
    // descending it will reach sumOf(abs(y_min))... and since it keeps decreasing as it comes down the gap will increase...
    // so at most it should be the abs value of min ... right? :D. With this we will be overshooting on the upper bound
    // but.. oh well :D

    // P1
    var maxY = Int.MIN_VALUE
    (minXvel..trenchXmax).forEach { xVelocity ->
        (trenchYmin..abs(trenchYmin)).forEach { yVelocity ->
            if (simulate(trenchXmax, trenchXmin, trenchYmin, trenchYmax, xVelocity, yVelocity)) {
                maxY = max(maxY, yVelocity)
            }
        }
    }

    println("P1 - maxY is ${sumOf(maxY)}")

    var count = 0
    (minXvel..trenchXmax).forEach { xVelocity ->
        (trenchYmin..abs(trenchYmin)).forEach { yVelocity ->
            if (simulate(trenchXmax, trenchXmin, trenchYmin, trenchYmax, xVelocity, yVelocity)) {
                count++
            }
        }
    }

    println("p2 - Init velocities in the trench: $count")

}

private fun simulate(
    trenchXmax: Int,
    trenchXmin: Int,
    trenchYmin: Int,
    trenchYmax: Int,
    xVelocity: Int,
    yVelocity: Int
): Boolean {
    var newXvelocity = xVelocity
    var newYvelocity = yVelocity
    var (x, y) = Pair(0, 0)
    while (x <= trenchXmax && y >= trenchYmin) {
        if (newXvelocity == 0 && x < trenchXmin) return false// so x wont change value so it's impossible
        if (x in trenchXmin..trenchXmax && y in trenchYmin..trenchYmax) return true

        x += newXvelocity
        y += newYvelocity

        newXvelocity -= if (newXvelocity > 0) 1 else 0
        newYvelocity -= 1
    }

    return false
}

fun findMinVelocityX(targetMinimum: Int): Int {
    var count = 1
    while (true) {
        if (sumOf(count) >= targetMinimum) {
            return count
        }
        count++
    }
}

fun sumOf(n: Int): Int = (n * (n+1))/2
