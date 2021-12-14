package src

data class InsertionRule(val input: String, val produces: String)

fun main() {
    val input = generateSequence(::readLine).toList()
    val template = input.first()
    val insertionRules = processRules(input)

    solve(template, 10, insertionRules)
    solve(template, 40, insertionRules)
}

fun solve(template: String, steps: Int, insertionRules: List<InsertionRule>) {
    val inputToRule =
        insertionRules.fold(mutableMapOf<String, InsertionRule>()) { acc, rule ->
            acc[rule.input] = rule
            acc
        }

    var pairMap = mutableMapOf<String, Long>()
    val countChars = mutableMapOf<Char, Long>()
    initPairMap(template, pairMap, countChars)

    for (step in 0 until steps) {
        val tmpMap = pairMap.toMutableMap()
        for ((pair, value) in pairMap) {
            if (value <= 0) continue

            val output = inputToRule[pair]!!
            if (!countChars.contains(output.produces.first())) {
                countChars[output.produces.first()] = 0
            }

            // We will add a new char in the middle of the pair. If there are 'value' pairs
            // it means we will a 'value' of the new chars in addition to the number it already exists
            countChars[output.produces.first()] = countChars[output.produces.first()]!! + value

            // Forms 2 new pairs
            val firstPair = "" + pair[0] + output.produces
            val secondPair = "" + output.produces + pair[1]

            if (!tmpMap.contains(firstPair)) {
                tmpMap[firstPair] = 0
            }
            if (!tmpMap.contains(secondPair)) {
                tmpMap[secondPair] = 0
            }

            // Forms new pairs in the same number the original pair exists
            tmpMap[firstPair] = tmpMap[firstPair]!! + value
            tmpMap[secondPair] = tmpMap[secondPair]!! + value

            // we will always possibly loose the initial pairs since we put a letter in the middle
            // doesn't mean that the new conjuctions above don't reform the pairs again :)
            tmpMap[pair] = tmpMap[pair]!! - value
        }

        pairMap = tmpMap
    }

    println("the diff most common and lest common is ${countChars.maxOf { it.value } - countChars.minOf { it.value }}")
}

private fun initPairMap(
    template: String,
    pairMap: MutableMap<String, Long>,
    countChars: MutableMap<Char, Long>
) {
    template.withIndex().forEach { (idx, c) ->
        if (idx + 1 < template.length) {
            val pair = "" + c + template[idx + 1]
            if (!pairMap.contains(pair)) {
                pairMap[pair] = 0
            }

            pairMap[pair] = pairMap[pair]!! + 1
        }

        if (!countChars.contains(c)) {
            countChars[c] = 0
        }

        countChars[c] = countChars[c]!! + 1
    }
}

fun processRules(
    input: List<String>
): List<InsertionRule> {
    val pattern = """(\w+) -> (\w+)""".toRegex()
    return input.slice(2 until input.size).map {
        val (ruleInput, ruleOutput) = pattern.matchEntire(it)!!.destructured
        InsertionRule(ruleInput, ruleOutput)
    }
}
