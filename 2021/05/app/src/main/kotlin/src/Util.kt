package src

import kotlin.math.max
import kotlin.math.min

fun segmentsIntersect(firstSegment: Segment, secondSegment: Segment): Boolean {
    val d1 = calcRelativeDirection(secondSegment.pointA, secondSegment.pointB, firstSegment.pointA)
    val d2= calcRelativeDirection(secondSegment.pointA, secondSegment.pointB, firstSegment.pointB)
    val d3 = calcRelativeDirection(firstSegment.pointA, firstSegment.pointB, secondSegment.pointA)
    val d4= calcRelativeDirection(firstSegment.pointA, firstSegment.pointB, secondSegment.pointB)

    return if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
        && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
        true
    } else if (d1 == 0 && onSegment(secondSegment.pointA, secondSegment.pointB, firstSegment.pointA)) {
        true
    } else if (d2 == 0 && onSegment(secondSegment.pointA, secondSegment.pointB, firstSegment.pointB)) {
        true
    } else if (d3 == 0 && onSegment(firstSegment.pointA, firstSegment.pointB, secondSegment.pointA)) {
        true
    } else d4 == 0 && onSegment(firstSegment.pointA, firstSegment.pointB, secondSegment.pointB)
}

fun calcRelativeDirection(pointI: Point, pointJ: Point, pointK: Point): Int {
    val xDiffKtoIPoint = pointK.x - pointI.x
    val xDiffJtoIPoint = pointJ.x - pointI.x
    val yDiffKtoIPoint = pointK.y - pointI.y
    val yDiffJtoIPoint = pointJ.y - pointI.y

    return (xDiffKtoIPoint * yDiffJtoIPoint) - (xDiffJtoIPoint *  yDiffKtoIPoint)
}

// PointK rests on top of some point of segment PointI and PointJ
fun onSegment(pointI: Point, pointJ: Point, pointK: Point): Boolean {
    val minX = min(pointI.x, pointJ.x)
    val minY = min(pointI.y, pointJ.y)
    val maxX = max(pointI.x, pointJ.x)
    val maxY = max(pointI.y, pointJ.y)

    return (pointK.x in minX..maxX) && (pointK.y in minY..maxY)
}
