fun main(args: Array<String>) {
    val input = generateSequence(::readLine).toList()
    println(input)
    val wayMap = TobogganMap(input)

    val p1 = solve(wayMap, 3, 1)
    println("Found $p1 trees")

    val p2First = solve(wayMap, 1, 1)
    val p2Third = solve(wayMap, 5, 1)
    val p2Fourth = solve(wayMap, 7, 1)
    val p2Fifth = solve(wayMap, 1, 2)
    println("Multiplying Found trees {$p2First, $p1, $p2Third, $p2Fourth, $p2Fifth} " +
            "= ${p1 * p2First * p2Third * p2Fourth * p2Fifth}")
}

fun solve(wayMap: TobogganMap, rightStep: Int, downStep: Int): Long {
    var line = downStep
    var row = rightStep
    var treeCount = 0L;
    while (line < wayMap.getLinesCount()) {
        if (wayMap.isTree(line, row)) {
            treeCount++
        }

        line += downStep
        row += rightStep
    }

    return treeCount
}