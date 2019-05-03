package com.malcolmcrum.gameboy

import assertk.assertThat
import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo
import org.junit.jupiter.api.BeforeEach
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
    val operations = OperationBuilder(registers, mmu, { _-> null }).operations

    @BeforeEach
    fun `reset registers`() {
        registers.reset()
    }

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

