package com.malcolmcrum.gameboy.operations

import assertk.assertThat
import com.malcolmcrum.gameboy.emulator.*
import com.malcolmcrum.gameboy.util.hex
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class OperationTest(var opcode: UByte, var initial: State = State(), var expected: State = State()) {
    private val log = KotlinLogging.logger {}

    val registers = Registers()
    val mmu = MMU(joypad = Joypad()).apply { this[0x100u, false] }
    val operations = Operations(registers, mmu)

    fun execute() {
        log.debug { "Initial: $initial" }
        givenRegisters(initial)
        givenROM(initial.pc ?: 0u, listOf(opcode).plus(initial.args))
        givenRAM(initial.ram)

        val op = executeInstruction()

        assertThat(registers, "${opcode.hex()}: ${op.mnemonic}").isEqualTo(expected)
        assertThat(mmu, "${opcode.hex()}: ${op.mnemonic}").isEqualTo(expected.ram)
    }

    private fun executeInstruction(): Z80Operation {
        val (_, operation) = operations[registers.pc]
        registers.pc = operation.invoke(registers.pc)
        return operation
    }

    private fun givenROM(pc: UShort, instructions: List<UByte>) {
        instructions.forEachIndexed { index, instruction -> mmu[pc + index.toUInt()] = instruction }
    }

    private fun givenRAM(ram: Map<UInt, UInt>) {
        ram.forEach { (address, value) -> mmu[address] = value.toUByte() }
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
fun test(instruction: Int, block: OperationTest.() -> Unit) {
    OperationTest(instruction.toUByte()).apply(block).execute()
}