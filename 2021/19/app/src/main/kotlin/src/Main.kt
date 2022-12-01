package src

import java.util.*
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val input = generateSequence(::readLine).toList()

    val scanners = readScannerBeacons(input)

    solve(scanners)
}

fun solve(scanners: List<Scanner>) {
    // Find equivalence rotation to first scanner
    // And all beacons to set. Count size
    val baseScanner = scanners.first()

    val exploreQueue: Queue<Scanner> = ArrayDeque(scanners.slice(1 until scanners.size))
    val allBeacons = baseScanner.beacons.toMutableSet()
    val allBeaconPositions = mutableSetOf(Beacon(0, 0, 0))

    while (exploreQueue.isNotEmpty()) {
        val scanner = exploreQueue.poll()
        val matchingBeacons = findMatchingRotation(scanner, allBeacons)
        if (matchingBeacons.first.isNotEmpty()) {
            allBeacons.addAll(matchingBeacons.first.toSet())
            allBeaconPositions.add(matchingBeacons.second)
        } else {
            exploreQueue.add(scanner)
        }
    }

    println("p1 - ${allBeacons.size}")

    var maxDist = Int.MIN_VALUE
    for(firstDist in allBeaconPositions) {
        for (secondDist in allBeaconPositions) {
            maxDist = max(maxDist, distance(firstDist, secondDist))
        }
    }

    println("p2 - $maxDist")
}

fun distance(firstDist: Beacon, secondDist: Beacon) =
    abs(firstDist.x - secondDist.x) + abs(firstDist.y - secondDist.y) + abs(firstDist.z - secondDist.z)

fun findMatchingRotation(scanner: Scanner, knownBeacons: Set<Beacon>): Pair<List<Beacon>, Beacon> {
    return scanner.rotations().firstNotNullOfOrNull { rotatedPoints ->
        rotatedPoints.firstNotNullOfOrNull { rotBeacon ->
            knownBeacons
                .map { baseBeacon -> difference(baseBeacon, rotBeacon) }
                .firstNotNullOfOrNull { diff ->
                    val moved = rotatedPoints.map { rotB -> addition(rotB, diff) }
                    if (knownBeacons.intersect(moved).size >= 12) {
                        Pair(moved, diff)
                    } else {
                        null
                    }
                }
        }
    } ?: Pair(emptyList(), Beacon(0, 0, 0))
}

// Poll time: What do you think itś most readable. The one above or this part in comment? :P
//    for (rotation in scanner.rotations()) {
//        for (knownBeacon in knownBeacons) {
//            for (rotBeacon in rotation) {
//                val diff = difference(knownBeacon, rotBeacon)
//                val movedRotBeacons = rotation.map { b -> addition(b, diff) }
//                if (knownBeacons.intersect(movedRotBeacons.toSet()).size >= 12) {
//                    return movedRotBeacons
//                }
//            }
//        }
//    }
//
//    return listOf()

fun difference(b1: Beacon, b2: Beacon) = Beacon(b1.x - b2.x, b1.y - b2.y, b1.z - b2.z)
fun addition(b1: Beacon, b2: Beacon) = Beacon(b1.x + b2.x, b1.y + b2.y, b1.z + b2.z)

data class Beacon(val x: Int, val y: Int, val z: Int) {
    fun rotate(): List<Beacon> {
        val result = mutableListOf<Beacon>()
        var rotBeacon = this.copy()
        for (cycle in 1..2) {
            for (step in 1..3) {
                rotBeacon = rotBeacon.roll()
                result.add(rotBeacon)
                for (i in 1..3) {
                    rotBeacon = rotBeacon.turn()
                    result.add(rotBeacon)
                }
            }
            rotBeacon = rotBeacon.roll().turn().roll()
        }

        return result
    }

    private fun roll() = Beacon(this.x, this.z, -this.y)
    private fun turn() = Beacon(-this.y, this.x, this.z)

    override fun toString(): String {
        return "$x,$y,$z"
    }
}
data class Scanner(val id: Int, val beacons: List<Beacon>) {
    fun rotations(): List<List<Beacon>> {
        val beaconRotations = this.beacons.map { it.rotate() }
        // beacon rotations is a list that contains a list. Each list is all rotations of a point
        // to iterate in solve we need to transpose to have all matching rotation of all points
        // in the same list
        return transpose(beaconRotations)
    }

    private fun transpose(beaconRotations: List<List<Beacon>>): List<List<Beacon>> {
        val result = mutableListOf<List<Beacon>>()
        for (col in 0 until beaconRotations.first().size) {
            val line = mutableListOf<Beacon>()
            for (row in beaconRotations.indices) {
                line.add(beaconRotations[row][col])
            }
            result.add(line)
        }

        return result
    }
}

fun readScannerBeacons(input: List<String>): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    val beacons = mutableListOf<Beacon>()
    var scannerId = 0
    for (line in input) {
        if (line.matches("--- scanner \\d+ ---".toRegex())) {
            val (scannerNumber) = "--- scanner (\\d+) ---".toRegex().matchEntire(line)!!.destructured
            scannerId = scannerNumber.toInt()
        } else if (line.isBlank()) {
            // End of current scanner beacons
            scanners.add(Scanner(scannerId, beacons.toList()))
            beacons.clear()
        } else {
            //reading beacons
            val (x, y, z) = line.split(",")
            beacons.add(Beacon(x.toInt(), y.toInt(), z.toInt()))
        }
    }

    // last line does not have extra blank :D
    scanners.add(Scanner(scannerId, beacons.toList()))

    return scanners
}