fun main(args: Array<String>) {
    val input = generateSequence(::readLine).toList()
    val passwordsP1 = input.map { parsePassword(it, true)}
    val passwordsP2 = input.map { parsePassword(it, false)}

    solve(passwordsP1)
    solve(passwordsP2)
}

fun solve(passwords: List<Password>) {
    println("Number of Valid Passwords: ${passwords.count { it.isValid() }}")
}

fun parsePassword(line: String, countPolicy: Boolean): Password {
    // Three parts: limits, letter, password
    val (limitsRaw, letterRaw, password)
            = line.split(" ")

    val (lowerLimit, upperLimit) = limitsRaw.split("-").map { it.toInt() }
    val letter = letterRaw.get(0)

    return Password(password,
        if (countPolicy) CountPasswordPolicy(letter, lowerLimit, upperLimit)
        else PositionPassowrdPolicy(letter, lowerLimit, upperLimit))
}