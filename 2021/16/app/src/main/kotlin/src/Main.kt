package src

import java.lang.RuntimeException

enum class OperatorType(val value: Int) {
    SUM(0),
    PRODUCT(1),
    MINIMUM(2),
    MAXIMUM(3),
    GREATER_THAN(5),
    LESS_THAN(6),
    EQUAL_TO(7)
}

fun main() {
    val input = generateSequence(::readLine).toList()
    val binaryCodedString = convertToBinary(input)

    val (_, packet) = parsePacket(binaryCodedString)

    val versionVisitor = VersionNumberVisitor()
    packet.accept(versionVisitor).also { println("P1 - Sum of version is $it") }

    val expressionVisitor = ExpressionVisitor()
    packet.accept(expressionVisitor).also { println("P2 - Result is $it") }
}

sealed class Packet {
    fun accept(visitor: Visitor) = visitor.visit(this)

    class LiteralPacket(val version: Long, val value: Long) : Packet()
    class OperatorPacket(val version: Long, val operatorType: OperatorType, val packets: List<Packet>) : Packet()
}

interface Visitor {
    fun visit(packet: Packet): Long
}

class VersionNumberVisitor : Visitor {
    override fun visit(packet: Packet): Long {
        return when (packet) {
            is Packet.LiteralPacket -> packet.version
            is Packet.OperatorPacket -> packet.version + packet.packets.sumOf { it.accept(this) }
        }
    }
}

class ExpressionVisitor : Visitor {
    override fun visit(packet: Packet): Long {
        return when (packet) {
            is Packet.LiteralPacket -> visitSpecific(packet)
            is Packet.OperatorPacket -> visitSpecific(packet)
        }
    }

    private fun visitSpecific(packet: Packet.LiteralPacket): Long = packet.value

    private fun visitSpecific(packet: Packet.OperatorPacket): Long {
        return when (packet.operatorType) {
            OperatorType.SUM -> packet.packets.sumOf { it.accept(this) }
            OperatorType.PRODUCT -> {
                packet.packets.fold(1) { acc, subPackets -> acc * subPackets.accept(this) }
            }
            OperatorType.MINIMUM -> packet.packets.minOf { it.accept(this) }
            OperatorType.MAXIMUM -> packet.packets.maxOf { it.accept(this) }
            OperatorType.GREATER_THAN -> {
                if (packet.packets[0].accept(this) > packet.packets[1].accept(this)) 1 else 0
            }
            OperatorType.LESS_THAN -> {
                if (packet.packets[0].accept(this) < packet.packets[1].accept(this)) 1 else 0
            }
            OperatorType.EQUAL_TO -> {
                if (packet.packets[0].accept(this) == packet.packets[1].accept(this)) 1 else 0
            }
        }
    }

}

fun parsePacket(binaryCodedString: String): Pair<String, Packet> {
    val pkgVersion = binaryCodedString.slice(0 until 3).toLong(2)
    val pkgTypeId = binaryCodedString.slice(3 until 6).toInt(2)

    val remainingCode = binaryCodedString.slice(6 until binaryCodedString.length)
    return when (pkgTypeId) {
        4 -> parseLiteralPacket(pkgVersion, remainingCode)
        else -> parseOperatorPacket(pkgVersion, pkgTypeId, remainingCode)
    }
}

private fun parseOperatorPacket(
    pkgVersion: Long,
    pkgTypeId: Int,
    binaryCodedString: String
): Pair<String, Packet.OperatorPacket> {
    val operatorLenType = binaryCodedString.first().digitToInt()
    val packets = mutableListOf<Packet>()
    return when (operatorLenType) {
        0 -> {
            val bitsOfSubPackets = binaryCodedString.slice(1 until 16).toLong(2)
            var subpacketsCode = binaryCodedString.slice(16 until binaryCodedString.length)
            while (subpacketsCode.length > (subpacketsCode.length - bitsOfSubPackets)) {
                val (remaining, packet) = parsePacket(subpacketsCode)
                packets.add(packet)
                subpacketsCode = remaining
            }

            Pair(
                subpacketsCode,
                Packet.OperatorPacket(pkgVersion, OperatorType.values().first { it.value == pkgTypeId }, packets)
            )
        }
        1 -> {
            val numberSubPackets = binaryCodedString.slice(1 until 12).toLong(2)
            var subpacketsCode = binaryCodedString.slice(12 until binaryCodedString.length)
            (0 until numberSubPackets).forEach { _ ->
                val (remaining, packet) = parsePacket(subpacketsCode)
                packets.add(packet)
                subpacketsCode = remaining
            }

            Pair(
                subpacketsCode,
                Packet.OperatorPacket(pkgVersion, OperatorType.values().first { it.value == pkgTypeId }, packets)
            )
        }
        else -> throw RuntimeException("Impossubro!!!")
    }
}

fun parseLiteralPacket(pkgVersion: Long, literalCode: String): Pair<String, Packet.LiteralPacket> {
    var endNumberFound = false
    var count = 0
    val value = literalCode.chunked(5).fold(mutableListOf<String>()) { acc, it ->
        val controlBit = it.first().digitToInt()
        if (!endNumberFound) {
            acc.add(it.slice(1 until it.length))
            count++
        }
        if (controlBit == 0) endNumberFound = true

        acc
    }.joinToString("").toLong(2)

    return Pair(literalCode.removeRange(0 until (5 * count)), Packet.LiteralPacket(pkgVersion, value))
}


fun convertToBinary(input: List<String>): String =
    input.first().fold("") { acc, hex ->
        var binValue = hex.digitToInt(16).toString(2)
        // Hammer for the padding to always have 4 bits :D
        binValue = if (binValue.length < 4) {
            (1..(4 - binValue.length)).fold(binValue) { bin, i -> "0$bin" }
        } else {
            binValue
        }
        acc + binValue
    }
