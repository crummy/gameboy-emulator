package com.malcolmcrum.gameboy

import assertk.Assert
import assertk.assertThat
import assertk.assertions.support.expected
import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OperationTest {
    val registers = Registers()
    val mmu = MMU().apply { inBios = false }
    val operations = OperationBuilder(registers, mmu).operations

    @ParameterizedTest
    @MethodSource
    fun `ADD A,$xx`(state: State, xx: Int, expected: State) {
        givenRegisters(state)
        givenRAM(listOf(0xc6, xx))

        executeInstruction()

        assertThat(registers).isEqualTo(expected)
    }

    fun `ADD A,$xx`(): Stream<Arguments> = Stream.of(
            arguments(State(a = 0u), 0x00, State(a = 0u)),
            arguments(State(a = 0u), 0x01, State(a = 1u)),
            arguments(State(a = 1u), 0x00, State(a = 1u)),
            arguments(State(a = 0xFFu), 0x01, State(a = 0u, f = CARRY_FLAG or ZERO_FLAG))
    )

    private fun executeInstruction() {
        val opCode = mmu[registers.pc].toInt()
        operations[opCode].operation.invoke()
    }

    private fun givenRAM(instructions: List<Int>) {
        instructions.forEachIndexed { index, instruction -> mmu[index.toUInt()] = instruction.toUByte() }
    }

    private fun givenRegisters(before: State) {
        registers.a = before.a ?: 0u
        registers.b = before.b ?: 0u
        registers.c = before.c ?: 0u
        registers.d = before.d ?: 0u
        registers.e = before.e ?: 0u
        registers.h = before.h ?: 0u
        registers.l = before.l ?: 0u
        registers.f = before.f ?: 0u
        registers.sp = before.sp ?: 0u
        registers.pc = before.pc ?: 0u
        registers.m = before.m ?: 0u
        registers.t = before.t ?: 0u
    }
}

@ExperimentalUnsignedTypes
data class State(
    var a: UByte? = null,
    var b: UByte? = null,
    var c: UByte? = null,
    var d: UByte? = null,
    var e: UByte? = null,
    var h: UByte? = null,
    var l: UByte? = null,
    var f: UByte? = null,
    var pc: UShort? = null,
    var sp: UShort? = null,
    var m: UByte? = null,
    var t: UByte? = null
) {
    override fun toString(): String {
        var flags = ""
        f?.let {
            flags = if (it and ZERO_FLAG != 0u.toUByte()) "Z" else ""
            flags += if (it and SUBTRACT_FLAG != 0u.toUByte()) "N" else ""
            flags += if (it and HALF_CARRY_FLAG != 0u.toUByte()) "H" else ""
            flags += if (it and CARRY_FLAG != 0u.toUByte()) "C" else ""
        }
        var state = ""
        a?.let { state += "a=${it.hex}" }
        b?.let { state += "b=${it.hex}" }
        c?.let { state += "c=${it.hex}" }
        d?.let { state += "d=${it.hex}" }
        e?.let { state += "e=${it.hex}" }
        h?.let { state += "h=${it.hex}" }
        l?.let { state += "l=${it.hex}" }
        pc?.let { state += "pc=${it.hex}" }
        sp?.let { state += "sp=${it.hex}" }
        m?.let { state += "m=${it.hex}" }
        t?.let { state += "t=${it.hex}" }
        return "State($state $flags)"
    }
}

@ExperimentalUnsignedTypes
fun Assert<Registers>.isEqualTo(expected: State) = given { actual ->
    val match =  expected.a?.equals(actual.a) ?: true &&
            expected.b?.equals(actual.b) ?: true &&
            expected.c?.equals(actual.c) ?: true &&
            expected.d?.equals(actual.d) ?: true &&
            expected.e?.equals(actual.e) ?: true &&
            expected.h?.equals(actual.h) ?: true &&
            expected.l?.equals(actual.l) ?: true &&
            expected.f?.equals(actual.f) ?: true &&
            expected.pc?.equals(actual.pc) ?: true &&
            expected.sp?.equals(actual.sp) ?: true &&
            expected.m?.equals(actual.m) ?: true &&
            expected.t?.equals(actual.t) ?: true
    if (match) return
    expected("registers do not match.", expected, actual)
}