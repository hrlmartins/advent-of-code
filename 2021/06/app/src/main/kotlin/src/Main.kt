package src

fun main() {
    val input = generateSequence(::readLine).toList()
    val allFish = input.flatMap { it.split(",") }.map { LanternFish(it.toInt()) }

    val trackFish = LongArray(9) { idx -> allFish.count { it.internalClock == idx }.toLong() }
    println("Number of fish P1: ${solve(trackFish, 0, 80)}")
    println("Number of fish P2: ${solve(trackFish, 0, 256)}")

}

fun solve(trackFish: LongArray, currentSimDay: Int, totalSimDays: Int): Long {
    if (currentSimDay == totalSimDays) {
        return trackFish.sum()
    }

    // Creating ne map to be easier to use old values and generate the new map with the new values without shenanigans
    val newTracker = LongArray(9) { 0 }

    // instead of the fish we just keep track of the number of fishes that have N days left to hatch a new fish
    // And update according to the rules:
    // We are processing a new day so every fish that had 5 days... now has 4 and so on and so forth
    // Just take into consideration the fishes that are zeroed that generate new fishes and reset themselves to 6
    for (day in 0..8) {
        if (day == 0) {
            newTracker[6] += trackFish[0]
            newTracker[8] += trackFish[0]
        } else {
            newTracker[day - 1] += trackFish[day]
        }
    }

    return solve(newTracker, currentSimDay + 1, totalSimDays)
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
