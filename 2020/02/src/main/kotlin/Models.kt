interface PasswordPolicy {
    fun isPasswordValid(password: String): Boolean
}

class CountPasswordPolicy(val letter: Char, val lowerLimit: Int, val upperLimit: Int): PasswordPolicy {
    override fun isPasswordValid(password: String): Boolean =
        password.count { c: Char -> c == letter } in lowerLimit..upperLimit
}

class PositionPassowrdPolicy(val letter: Char, val lowerPos: Int, val upperPos: Int): PasswordPolicy {
    override fun isPasswordValid(password: String): Boolean {
        val positions = listOf(lowerPos, upperPos)
        return positions.count { pos -> password[pos - 1] ==  letter} == 1
    }

}

class Password(val password: String, val policy: PasswordPolicy) {
    fun isValid(): Boolean = policy.isPasswordValid(password)
}