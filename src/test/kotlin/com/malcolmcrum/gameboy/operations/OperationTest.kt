package com.malcolmcrum.gameboy.operations

import assertk.assertThat
import com.malcolmcrum.gameboy.MMU
import com.malcolmcrum.gameboy.OperationBuilder
import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class OperationTest(private val instruction: Int) {
    val registers = Registers()
    val mmu = MMU().apply { inBios = false }
    val operations = OperationBuilder(registers, mmu, { null }).operations

    @BeforeEach
    fun `reset registers`() {
        registers.reset()
    }

    abstract fun parameters(): Stream<Arguments>

    @ParameterizedTest
    @MethodSource("parameters")
    fun testOperation(state: State, expected: State, instructionArguments: List<Int>) {

        givenRegisters(state)
        givenRAM(listOf(instruction).plus(instructionArguments))

        executeInstruction()

        assertThat(registers).isEqualTo(expected)

    }

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

