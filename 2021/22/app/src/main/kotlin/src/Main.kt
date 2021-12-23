package src

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Operation(val turnOn: Boolean, val xRange: Range, val yRange: Range, val zRange: Range)
data class Range(val first: Int, val last: Int)
data class Cuboid(val x: Range, val y: Range, val z: Range) {
    fun intersects(other: Cuboid): Boolean {
        return x.last >= other.x.first && x.first <= other.x.last &&
                y.last >= other.y.first && y.first <= other.y.last &&
                z.last >= other.z.first && z.first <= other.z.last
    }

    fun countCubes() =
        (abs(x.last.toLong() - x.first.toLong()) + 1L) *
                (abs(y.last.toLong() - y.first.toLong()) + 1L) *
                (abs(z.last.toLong() - z.first.toLong()) + 1L)

    fun intersect(other: Cuboid) =
        Cuboid(
            Range(max(x.first, other.x.first), min(x.last, other.x.last)),
            Range(max(y.first, other.y.first), min(y.last, other.y.last)),
            Range(max(z.first, other.z.first), min(z.last, other.z.last)),
        )
}

fun main() {
    val input = generateSequence(::readLine).toList()
    val pattern = """(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()
    val operations = readInput(input, pattern)

    solve(operations.filter {
        abs(it.xRange.first) <= 50
                && abs(it.xRange.last) <= 50
                && abs(it.yRange.first) <= 50
                && abs(it.yRange.last) <= 50
                && abs(it.zRange.first) <= 50
                && abs(it.zRange.last) <= 50
    })
    solve(operations)
}

fun solve(operations: List<Operation>) {
    var cuboids = mutableSetOf<Cuboid>()
    operations.forEach { op ->
        // When it is on process
        // Meaning if it doesn intersect existing ones just add to the set
        // If it intersects remove the original one that intersects and than add back the new parts
        //      - The intersection part
        //      - The part of the original cuboids without the intersection.
        // A cuboid may generate more than one cuboid when removing the intersection part of itself
        // When turning lights off on intersections get the original ON cube parts without the intersection
        // And remove the original one. Itś basically the turn on lights operation but without adding the intersection
        // and the part of the cuboid with the off operation
        if (op.turnOn) {
            cuboids = turnOn(op, cuboids)
        } else {
            cuboids = turnOff(op, cuboids)
        }
    }

    println(cuboids.sumOf { it.countCubes() })

}

fun turnOn(op: Operation, cuboids: MutableSet<Cuboid>): MutableSet<Cuboid> {
    val newCuboids = mutableSetOf<Cuboid>()
    val opCuboid = Cuboid(op.xRange, op.yRange, op.zRange)

    for (cuboid in cuboids) {
        if (cuboid.intersects(opCuboid)) {
            // Get the part of the existing cuboids without the intersection
            newCuboids.addAll(splitCuboid(cuboid, cuboid.intersect(opCuboid)))
        } else {
            newCuboids.add(cuboid)
        }
    }

    newCuboids.add(opCuboid)

    return newCuboids
}

fun turnOff(op: Operation, cuboids: MutableSet<Cuboid>): MutableSet<Cuboid> {
    val newCuboids = mutableSetOf<Cuboid>()
    val opCuboid = Cuboid(op.xRange, op.yRange, op.zRange)

    for (cuboid in cuboids) {
        if (cuboid.intersects(opCuboid)) {
            // Get the part of the existing cuboids without the intersection
            newCuboids.addAll(splitCuboid(cuboid, cuboid.intersect(opCuboid)))
        } else {
            newCuboids.add(cuboid)
        }
    }

    return newCuboids
}

fun splitCuboid(cuboid: Cuboid, intersection: Cuboid): Set<Cuboid> {
    val splitCuboids = mutableSetOf<Cuboid>()

    if (cuboid.y.last > intersection.y.last) {
        splitCuboids.add(Cuboid(cuboid.x, Range(intersection.y.last + 1, cuboid.y.last), cuboid.z))
    }

    if (cuboid.y.first < intersection.y.first) {
        splitCuboids.add(Cuboid(cuboid.x, Range(cuboid.y.first, intersection.y.first - 1), cuboid.z))
    }

    if (cuboid.x.first < intersection.x.first) {
        splitCuboids.add(Cuboid(Range(cuboid.x.first, intersection.x.first -1), intersection.y, cuboid.z))
    }

    if (cuboid.x.last > intersection.x.last) {
        splitCuboids.add(Cuboid(Range(intersection.x.last + 1, cuboid.x.last), intersection.y, cuboid.z))
    }

    if (cuboid.z.first < intersection.z.first) {
        splitCuboids.add(Cuboid(intersection.x, intersection.y, Range(cuboid.z.first, intersection.z.first - 1)))
    }

    if (cuboid.z.last > intersection.z.last) {
        splitCuboids.add(Cuboid(intersection.x, intersection.y, Range(intersection.z.last + 1, cuboid.z.last)))
    }

    return splitCuboids
}

private fun readInput(
    input: List<String>,
    pattern: Regex
) = input.map { line ->
    val (operation,
        xMin,
        xMax,
        yMin,
        yMax,
        zMin,
        zMax) = pattern.matchEntire(line)!!.destructured

    Operation(
        operation == "on",
        Range(xMin.toInt(), xMax.toInt()),
        Range(yMin.toInt(), yMax.toInt()),
        Range(zMin.toInt(), zMax.toInt())
    )
}
