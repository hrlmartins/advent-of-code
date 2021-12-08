package src

fun main() {
    val input = generateSequence(::readLine).toList()
    val digitalSignals = input.map { DigitalSignal(it.split("|")[0].trim(), it.split("|")[1].trim()) }

    /******************************************************************************
     * p1
     *****************************************************************************/

    val frequencyMap = setOf(2, 3, 4, 7)
    digitalSignals.sumOf { digitalSignal ->
        digitalSignal.output.split(" ").count { frequencyMap.contains(it.length) }
    }.also { println("P1 - number of identifiable numbers is $it") }


    /****************************************************************************
     * p2 - WOHOOOOOOOO EMBRACE THE SPAGUETTI :D
     ***************************************************************************/

    digitalSignals.sumOf { signal ->
        val map = determineMapping(signal.input)
        signal.output.split(" ").fold("") { acc, elem ->
            acc + map[elem.toSortedSet().joinToString("")]
        }.toLong()
    }.also { println("The sum of all output number is: $it") }
}

fun determineMapping(input: String): Map<String, String> {
    val mapCodeToInt = mutableMapOf<String, String>()
    val mapIntToCode = mutableMapOf<String, String>()
    val splitInput = input.split(" ").map { it.toSortedSet().joinToString("") }

    splitInput.forEach { code ->
        when (code.length) {
            2 -> {
                mapCodeToInt[code] = "1"
                mapIntToCode["1"] = code
            }
            3 -> {
                mapCodeToInt[code] = "7"
                mapIntToCode["7"] = code
            }
            4 -> {
                mapCodeToInt[code] = "4"
                mapIntToCode["4"] = code
            }
            7 -> {
                mapCodeToInt[code] = "8"
                mapIntToCode["8"] = code
            }
        }
    }

    extractFiveSegmentDigits(splitInput, mapIntToCode, mapCodeToInt)
    extractSixSegmentDigits(splitInput, mapIntToCode, mapCodeToInt)

    return mapCodeToInt
}

private fun extractSixSegmentDigits(
    splitInput: List<String>,
    mapIntToCode: MutableMap<String, String>,
    mapCodeToInt: MutableMap<String, String>
) {
    // Now onwards to the digits with 6 segments
    val allSixSegmentDigits = splitInput.filter { it.length == 6 }

    // so digit 6 is the only one who will not have all the digit 1 segments
    val sixCode =
        allSixSegmentDigits
            .first { it.toSortedSet().intersect(mapIntToCode["1"]!!.toSortedSet()).size < 2 }
    mapCodeToInt[sixCode] = "6"

    // digit 9 compared to 0 is the only one to have four segments in common with 4
    val nineCode =
        allSixSegmentDigits
            .filter { it != sixCode }
            .first { mapIntToCode["4"]!!.toSortedSet().intersect(it.toSortedSet()).size == 4 }
    mapCodeToInt[nineCode] = "9"

    // oh well it's the reamining right? :D
    val zeroCode = allSixSegmentDigits.first { it != sixCode && it != nineCode }
    mapCodeToInt[zeroCode] = "0"
}

fun extractFiveSegmentDigits(
    splitInput: List<String>,
    mapIntToCode: MutableMap<String, String>,
    mapCodeToInt: MutableMap<String, String>
) {
    // The base is set let's start deducing the other ones. So all numbers with 5 segments first 2, 3 and 5
    val allFiveSegmentDigits = splitInput.filter { it.length == 5 }
    // The number 3 is the only one that contains both number 1 segments
    val threeCode =
        allFiveSegmentDigits
            .first { it.toSortedSet().intersect(mapIntToCode["1"]!!.toSortedSet()).size == 2 }
    mapCodeToInt[threeCode] = "3"

    // well.. now that we have 3... 5 is the only number here (comparing with 2) that has three segments in common with 4 :D
    val fiveCode =
        allFiveSegmentDigits
            .filter { it != threeCode }
            .first { it.toSortedSet().intersect(mapIntToCode["4"]!!.toSortedSet()).size == 3 }
    mapCodeToInt[fiveCode] = "5"

    // well now only 2 is left right? :P
    val twoCode = allFiveSegmentDigits.first { it != threeCode && it != fiveCode }
    mapCodeToInt[twoCode] = "2"
}

data class DigitalSignal(val input: String, val output: String)