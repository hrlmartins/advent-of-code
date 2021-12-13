package src

sealed class Fold(val value: Int) {
    data class Horizontal(val v: Int): Fold(v)
    data class Vertical(val v: Int): Fold(v)
}

data class Point(val x: Int, val y: Int)

fun main() {
    val input = generateSequence(::readLine).toList()
    val separator = input.indexOf("")
    val rawPoints = input.slice(0 until separator)
    val rawInstructions = input.slice((separator + 1) until input.size)
    val points = processPoints(rawPoints)
    val foldInstructions = processDirections(rawInstructions)

    solveP1(points, foldInstructions[0])
    println()
    solveP2(points, foldInstructions)

}

fun solveP1(points: Set<Point>, fold: Fold) {
    val foldResult = foldPaper(fold, points)
    foldResult.toSet().also { println("P1 - ${it.size}") }
}

fun solveP2(points: Set<Point>, folds: List<Fold>) {
    val allFolds = folds.fold(points) { acc, fold -> foldPaper(fold, acc).toSet() }
    printCode(allFolds)
}

private fun printCode(paper: Set<Point>) {
    val maxX = paper.maxOf { it.x }
    val maxY = paper.maxOf { it.y }
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            if (paper.contains(Point(x, y))) {
                print("\u001b[1;35m#\u001B[0m")
            } else {
                print(".")
            }
        }
        println()
    }
}

private fun foldPaper(fold: Fold, points: Set<Point>): List<Point> {
    return when (fold) {
        is Fold.Horizontal ->
            // it's an y fold
            points.map { p ->
                if (p.y > fold.value) {
                    val diff = p.y - fold.value
                    return@map Point(p.x, fold.value - diff)
                }
                Point(p.x, p.y)
            }

        is Fold.Vertical ->
            points.map { p ->
                if (p.x > fold.value) {
                    val diff = p.x - fold.value
                    return@map Point(fold.value - diff, p.y)
                }
                Point(p.x, p.y)
            }
    }
}

private fun processDirections(rawInstructions: List<String>): List<Fold> {
    val pattern = """(\w)=(\d+)""".toRegex()
    return rawInstructions.map {
        val (direction, value) = pattern.find(it)!!.destructured
        if (direction == "y") {
            Fold.Horizontal(value.toInt())
        } else {
            Fold.Vertical(value.toInt())
        }
    }
}

private fun processPoints(points: List<String>) =
    points.map {
        val coordinates = it.split(",")
        Point(coordinates[0].toInt(), coordinates[1].toInt())
    }.toSet()
