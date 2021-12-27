package src

sealed class Value {
    data class Literal(val value: Long): Value()
    data class Variable(val name: String): Value()
}

sealed class Instructions {
    data class ReadInput(val variable: Value): Instructions()
    data class Add(val leftOperand: Value, val rightOperand: Value): Instructions()
    data class Mul(val leftOperand: Value, val rightOperand: Value): Instructions()
    data class Div(val leftOperand: Value, val rightOperand: Value): Instructions()
    data class Mod(val leftOperand: Value, val rightOperand: Value): Instructions()
    data class Eql(val leftOperand: Value, val rightOperand: Value): Instructions()
}

data class Alu(
    val memory: MutableMap<String, Long> = mutableMapOf("x" to 0L, "y" to 0L, "w" to 0L, "z" to 0L),
    var inputIdx: Int = 0,
    var input: Int = 0,
    val instructions: List<Instructions> = listOf(),
    var invalid: Boolean = false
) {
    // HAMMER TIME. These values could have been deduced from the instruction list :). May change this if I feel like
    val xValues = listOf(11, 12, 10, -8, 15, 15, -11, 10, -3, 15, -3, -1, -10, -16)

    fun run() {
        for (instruction in instructions) {
            when(instruction) {
                is Instructions.Add -> process(instruction)
                is Instructions.Div -> process(instruction)
                is Instructions.Eql -> process(instruction)
                is Instructions.Mod -> process(instruction)
                is Instructions.Mul -> process(instruction)
                is Instructions.ReadInput -> process(instruction)
            }
        }
    }

    fun isSuccess() = memory["z"]!! == 0L

    fun isInvalid() = invalid

    private fun process(op: Instructions.Add) {
        val leftOperand = readOperand(op.leftOperand)
        var rightOperand = readOperand(op.rightOperand)
        val storeVar = (op.leftOperand as Value.Variable).name

        // More Hammer Time

        memory[storeVar] = leftOperand + rightOperand
    }

    private fun process(op: Instructions.Div) {
        val leftOperand = readOperand(op.leftOperand)
        val rightOperand = readOperand(op.rightOperand)
        val storeVar = (op.leftOperand as Value.Variable).name

        if (rightOperand == 0L) return
        memory[storeVar] = leftOperand / rightOperand
    }

    private fun process(op: Instructions.Eql) {
        val leftOperand = readOperand(op.leftOperand)
        val rightOperand = readOperand(op.rightOperand)
        val storeVar = (op.leftOperand as Value.Variable).name

        memory[storeVar] = if (leftOperand == rightOperand) 1 else 0
    }

    private fun process(op: Instructions.Mod) {
        val leftOperand = readOperand(op.leftOperand)
        val rightOperand = readOperand(op.rightOperand)
        val storeVar = (op.leftOperand as Value.Variable).name

        if (rightOperand <= 0L) return

        // HAMMER TIME
        // We must always divide by 26 the same number of times we multiply so we ensure
        // we get a 0 z at the end.
        // That can only happen when x values are negative because it's the only time we can have a
        // one digit x (which is the same len as input). Furthermore, input and x + <value> need to be the same
        // That is the condition necessary for the division by 26 to happen according to the input
        // Or better... for z to not get multiplied by 26 again after having been divided
        if (xValues[inputIdx] < 0) {
            val hackMod = (memory["x"]!! % 26) + xValues[inputIdx]
            invalid = (hackMod < 10 && (input.toLong() != hackMod)) || hackMod >= 10
        }

        memory[storeVar] = leftOperand % rightOperand
    }

    private fun process(op: Instructions.Mul) {
        val leftOperand = readOperand(op.leftOperand)
        val rightOperand = readOperand(op.rightOperand)
        val storeVar = (op.leftOperand as Value.Variable).name

        memory[storeVar] = leftOperand * rightOperand
    }

    private fun process(op: Instructions.ReadInput) {
        val storeVar = (op.variable as Value.Variable).name

        memory[storeVar] = input.toLong()
    }

    private fun readOperand(vle: Value) = when(vle) {
        is Value.Literal -> vle.value
        is Value.Variable -> memory[vle.name]!!
    }
}
fun main() {
    val input = generateSequence(::readLine).toList()
    val instructions = readInstructions(input)

    println("p1 - ${solveP1(instructions, Alu(), 0)}")
    println("p2 - ${solveP2(instructions, Alu(), 0)}")

}

fun solveP1(instructions: List<Instructions>, prev: Alu, depth: Int): String? {
    if (depth == 14) {
        println(prev.memory)
        return ""
    }

    val offset = depth * 18
    for (input in 9 downTo 1) {
        val current = prev.copy(
            memory = prev.memory.toMutableMap(),
            instructions = instructions.slice(offset until (offset + 18) ),
            input = input,
            inputIdx = depth,
            invalid = false
        )
        current.run()
        if (!current.isInvalid()) {
            val next = solveP1(instructions, current, depth + 1)
            if (next != null) { // Reached in the future to a valid solution
                return input.toString() + next
            }
        }
    }

    return null
}

fun solveP2(instructions: List<Instructions>, prev: Alu, depth: Int): String? {
    if (depth == 14) {
        println(prev.memory)
        return ""
    }

    val offset = depth * 18
    for (input in 1 .. 9) {
        val current = prev.copy(
            memory = prev.memory.toMutableMap(),
            instructions = instructions.slice(offset until (offset + 18) ),
            input = input,
            inputIdx = depth,
            invalid = false
        )
        current.run()
        if (!current.isInvalid()) {
            val next = solveP2(instructions, current, depth + 1)
            if (next != null) { // Reached in the future to a valid solution
                return input.toString() + next
            }
        }
    }

    return null
}

fun readInstructions(input: List<String>) = input.map { instruction ->
    val instrOp = instruction.split(" ")
    if (instrOp.size == 2) {
        // This is an input operation
        Instructions.ReadInput(Value.Variable(instrOp[1]))
    } else {
        val leftOperand = readLeftOperand(instrOp)
        val rightOperand = readRightOperand(instrOp)

        when (instrOp[0]) {
            "mul" -> Instructions.Mul(leftOperand, rightOperand)
            "add" -> Instructions.Add(leftOperand, rightOperand)
            "div" -> Instructions.Div(leftOperand, rightOperand)
            "mod" -> Instructions.Mod(leftOperand, rightOperand)
            "eql" -> Instructions.Eql(leftOperand, rightOperand)
            else -> TODO("Impossibruuh!!!")
        }
    }
}

fun readRightOperand(instrOp: List<String>) =
    if (instrOp[2].toIntOrNull() == null)
        Value.Variable(instrOp[2])
    else
        Value.Literal(instrOp[2].toLong())

fun readLeftOperand(instrOp: List<String>) =
    if (instrOp[1].toIntOrNull() == null)
        Value.Variable(instrOp[1])
    else
        Value.Literal(instrOp[1].toLong())
