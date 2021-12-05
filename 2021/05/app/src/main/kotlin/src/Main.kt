package src

fun main() {
    val input = generateSequence(::readLine).toList()
    solve(readSegments(input).filter { it.pointA.x == it.pointB.x || it.pointA.y == it.pointB.y })
    solve(readSegments(input))
}

fun solve(segments: List<Segment>) {
    segments.flatMap { segment ->
        if (segment.pointA.x == segment.pointB.x) {
            range(segment.pointA.y, segment.pointB.y).map { Point(segment.pointA.x, it) }
        } else if (segment.pointA.y == segment.pointB.y) {
            range(segment.pointA.x, segment.pointB.x).map { Point(it, segment.pointA.y) }
        } else {
            range(segment.pointA.x, segment.pointB.x)
                .zip(range(segment.pointA.y, segment.pointB.y))
                .map { Point(it.first, it.second) }
        }
    }.groupingBy { it }.eachCount().count { it.value >= 2 }.also { println("Intersect points: $it") }
}

fun range(start: Int, end: Int) = if (start > end) (start downTo end) else (start..end)

fun readSegments(rawSegments: List<String>): List<Segment> {
    val pattern = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()

    return rawSegments.map {
        val (x0, y0, x1, y1) = pattern.matchEntire(it)!!.destructured
        Segment(Point(x0.toInt(), y0.toInt()), Point(x1.toInt(), y1.toInt()))
    }.toList()
}

data class Point(val x: Int, val y: Int)
data class Segment(val pointA: Point, val pointB: Point)