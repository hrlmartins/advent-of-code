// each list pos is a line of the maze
// Size of the list is the maze lines
// the size of a list position is the width of the maze
class TobogganMap(val mapping: List<String>) {

    fun isTree(line: Int, row: Int): Boolean = mapping[line % getLinesCount()][row % getRowsCount()] == '#'

    fun getLinesCount() = mapping.size

    fun getRowsCount() = mapping[0].length
}