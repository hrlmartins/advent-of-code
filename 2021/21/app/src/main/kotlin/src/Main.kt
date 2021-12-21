package src

import kotlin.math.min

data class Player(var position: Int, var points: Int)

fun main() {
//    val input = generateSequence(::readLine).toList()

// test players
//    val p1 = Player(4, 0)
//    val p2 = Player(8, 0)
    val p1 = Player(8, 0)
    val p2 = Player(9, 0)

    solveP1(p1.copy(), p2.copy())
    solveP2(p1.copy(), p2.copy())
}

fun solveP1(p1: Player, p2: Player) {
    var diceRolls = 0
    (1..1000000).chunked(6).map { chunk -> chunk.map { value -> ((value - 1) % 100) + 1 } }.forEach {
        val newPLayerOneSpot = (p1.position + (it.slice(0 until 3).sum()) - 1) % 10 + 1
        p1.points += newPLayerOneSpot
        p1.position = newPLayerOneSpot

        diceRolls += 3
        if (p1.points >= 1000) {
            val looserPoints = min(p1.points, p2.points)
            println("P1 - ${diceRolls * looserPoints.toLong()}")
            return
        }

        val newPLayerTwoSpot = (p2.position + (it.slice(3 until 6).sum()) - 1) % 10 + 1
        p2.points += newPLayerTwoSpot
        p2.position = newPLayerTwoSpot

        diceRolls += 3
        if (p2.points >= 1000) {
            val looserPoints = min(p1.points, p2.points)
            println("P1 - ${diceRolls * looserPoints.toLong()}")
            return
        }
    }
}

data class State(
    val firstPos: Int,
    val secondPos: Int,
    val firstPoints: Int,
    val secondPoints: Int,
    val firstTurn: Boolean
)

fun solveP2(p1: Player, p2: Player) {
    val memorized = mutableMapOf<State, Pair<Long, Long>>()
    println(universesWon(p1.position, p2.position, 0, 0, 21, true, memorized))
}

fun universesWon(
    firstPos: Int,
    secondPos: Int,
    firstPoints: Int,
    secondPoints: Int,
    target: Int,
    firstTurn: Boolean,
    memorized: MutableMap<State, Pair<Long, Long>>
): Pair<Long, Long> {
    if (memorized.containsKey(State(firstPos, secondPos, firstPoints, secondPoints, firstTurn)))
        return memorized[State(firstPos, secondPos, firstPoints, secondPoints, firstTurn)]!!

    if (firstPoints >= target) {
        memorized[State(firstPos, secondPos, firstPoints, secondPoints, firstTurn)] = Pair(1, 0)
        return Pair(1, 0)
    }

    if (secondPoints >= target) {
        memorized[State(firstPos, secondPos, firstPoints, secondPoints, firstTurn)] = Pair(0, 1)
        return Pair(0, 1)
    }

    var count = Pair(0L, 0L)
    for (di in 1..3) {
        for (dj in 1..3) {
            for (dk in 1..3) {
                val newFirstPos = (firstPos + di + dj + dk - 1) % 10 + 1
                val newSecondPos = (secondPos + di + dj + dk - 1) % 10 + 1
                val (firstCount, secondCount) =
                    if (firstTurn) {
                        universesWon(
                            newFirstPos,
                            secondPos,
                            firstPoints + newFirstPos,
                            secondPoints,
                            target,
                            !firstTurn,
                            memorized
                        )
                    } else {
                        universesWon(
                            firstPos,
                            newSecondPos,
                            firstPoints,
                            secondPoints + newSecondPos,
                            target,
                            !firstTurn,
                            memorized
                        )
                    }

                count = Pair(count.first + firstCount, count.second + secondCount)
            }
        }
    }

    memorized[State(firstPos, secondPos, firstPoints, secondPoints, firstTurn)] = count
    return count
}
