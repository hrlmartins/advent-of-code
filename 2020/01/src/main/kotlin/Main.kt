fun main(args: Array<String>) {
    val inputList = generateSequence(::readLine)
    val numberList = inputList.map { it.toInt() }.toList()

    //val sumElementsList = findSummingPairFor(2020, numberList)
    val sumElementsList = findSummingTripletFor(2020, numberList)

    val result = sumElementsList.fold(1) { acc, elem -> acc * elem }

    println("The product of $sumElementsList = $result")
}

fun findSummingPairFor(expectedResult: Int, data: List<Int>): List<Int> {
    val seenValues = mutableSetOf<Int>()

    for (num in data) {
        val summingValue = expectedResult - num;
        if (seenValues.contains(summingValue)) {
            // We have found the pair!
            return listOf(num, summingValue)
        }

        seenValues.add(num)
    }

    return listOf()
}

fun findSummingTripletFor(expectedResult: Int, data: List<Int>): List<Int> {
    for (i in 1 until data.size) {
        for (j in i + 1 until data.size) {
            for (k in j + 1 until data.size) {
                if (data[i] + data[j] + data[k] == expectedResult) {
                    return listOf(data[i], data[j], data[k])
                }
            }
        }
    }

    return listOf()
}