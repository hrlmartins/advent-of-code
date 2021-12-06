package src

fun main() {
    val input = generateSequence(::readLine).toList()
    val allFish = input.flatMap { it.split(",") }.map { LanternFish(it.toInt()) }

    val trackFish = mutableMapOf<Int, Long>()
    (0..8).forEach { trackFish[it] = 0 }
    allFish.forEach { trackFish[it.internalClock] = trackFish[it.internalClock]!! + 1 }
    println("Number of fish P1: ${solve(trackFish, 0, 80)}")

    (0..8).forEach { trackFish[it] = 0 }
    allFish.forEach { trackFish[it.internalClock] = trackFish[it.internalClock]!! + 1 }
    println("Number of fish P2: ${solve(trackFish, 0, 256)}")

}

fun solve(trackFish: MutableMap<Int, Long>, currentSimDay: Int, totalSimDays: Int): Long {
    if (currentSimDay == totalSimDays) {
        return trackFish.values.sum()
    }

    // let's save the fishes with 1 day first so we can update the 0 counter later
    val currentDay1Fishes = trackFish[1]!!
    // 1 and 7 will inherit the previous fishes with higher days left (we are passing a day so who had 7 days now has 6 for instance)
    (1..7).forEach {
        trackFish[it] = trackFish[it + 1]!!
    }

    if (trackFish[0]!! > 0) {
        // if we have fish with 0 days this will mean we will have a N amount of new fishes with 8 days.
        // Addittionally it will also reset all the 0 fishes into 6 days. So we add to the existing 6
        trackFish[8] = trackFish[0]!!
        trackFish[6] = trackFish[6]!! + trackFish[0]!!
    }

    //let's pass the day for the new fishies... which will be the same number as the current fishes with 0 days :D
    trackFish[8] = trackFish[0]!!
    //Now we can set the 0 day fishes
    trackFish[0] = currentDay1Fishes

    return solve(trackFish, currentSimDay + 1, totalSimDays)
}

data class LanternFish(var internalClock: Int = 8) // not even necessary but yeh :D

/**********************************************************************************************************
 * This was the solution I did for a fast p1.
 *
 * Obviously per the problem statement it would not fit in memory :D
 * (I did provide "infinite ram" to JVM just for science sake. You can guess what happened :D)
 *
 *********************************************************************************************************/
//fun solveP1(fish: List<LanternFish>, currentSimDay: Int, totalSimDays: Int): List<LanternFish> {
//    if (currentSimDay == totalSimDays) return fish
//
//    return solveP1(fish.flatMap { processFish(it) }, currentSimDay + 1, totalSimDays)
//}
//
//fun processFish(fish: LanternFish): List<LanternFish> {
//    if (fish.internalClock == 0) {
//        fish.internalClock = 6
//        return listOf(fish, LanternFish())
//    } else {
//        fish.internalClock--
//    }
//
//    return listOf(fish)
//}
