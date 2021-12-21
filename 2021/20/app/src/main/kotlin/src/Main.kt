package src

fun main() {
    val input = generateSequence(::readLine).toList()

    val algorithm = listOf(input[0])
    val rawImage = input.slice(2 until input.size).map { it.trim() }

    solve(algorithm.first().trim(), Image(rawImage), 2)
    solve(algorithm.first().trim(), Image(rawImage), 50)
}

fun solve(algorithm: String, image: Image, passes: Int) {
    var tmpImg = image
    var infIdx = algorithm.indexOf(".")
    (1..passes).forEach {
        val minRow = tmpImg.imageMap.minOf { it.row }
        val minCol = tmpImg.imageMap.minOf { it.col }
        val maxRow = tmpImg.imageMap.maxOf { it.row }
        val maxCol = tmpImg.imageMap.maxOf { it.col }

        tmpImg = processImage(infIdx, tmpImg, algorithm, minRow, minCol, maxRow, maxCol)
        infIdx = if (algorithm[infIdx] == '#') 511 else 0
    }

    println("lit positions is equal to ${tmpImg.imageMap.size}")
}

fun processImage(
    infIdx: Int,
    originalImg: Image,
    algorithm: String,
    minRow: Int,
    minCol: Int,
    maxRow: Int,
    maxCol: Int
): Image {
    val imageResult = Image(listOf())
    for (row in minRow - 1..maxRow + 1) {
        for (col in minCol - 1..maxCol + 1) {
            val algorithmIdx =
                getAdjacentPositionsList(Position(row, col)).fold(StringBuilder()) { acc, pos ->
                    val inImage = (pos.row in minRow..maxRow) && (pos.col in minCol..maxCol)
                    if (inImage) {
                        if (originalImg.imageMap.contains(pos)) acc.append("1") else acc.append("0")
                    } else {
                        if (algorithm[infIdx] == '#') acc.append("1") else acc.append("0")
                    }
                }.toString().toInt(2)

            if (algorithm[algorithmIdx] == '#') {
                imageResult.imageMap.add(Position(row, col))
            }
        }
    }

    return imageResult
}

fun getAdjacentPositionsList(position: Position) =
    listOf(
        Position(position.row - 1, position.col - 1),
        Position(position.row - 1, position.col + 0),
        Position(position.row - 1, position.col + 1),
        Position(position.row + 0, position.col - 1),
        Position(position.row + 0, position.col + 0),
        Position(position.row + 0, position.col + 1),
        Position(position.row + 1, position.col - 1),
        Position(position.row + 1, position.col + 0),
        Position(position.row + 1, position.col + 1)
    )

data class Position(val row: Int, val col: Int)
class Image(rawImage: List<String>) {
    val imageMap: MutableSet<Position> = mutableSetOf()

    init {
        rawImage.forEachIndexed { row, columnString ->
            columnString.forEachIndexed { column, c ->
                if (c == '#') imageMap.add(Position(row, column))
            }
        }
    }

    override fun toString(): String {
        val result: StringBuilder = StringBuilder()
        val maxRow = this.imageMap.maxOf { it.row }
        val maxCol = this.imageMap.maxOf { it.col }

        for (row in -5..maxRow + 5) {
            for (col in -5..maxCol + 5) {
                if (imageMap.contains(Position(row, col))) {
                    result.append('#')
                } else {
                    result.append('.')
                }
            }
            result.append('\n')
        }
        return result.toString()
    }
}
