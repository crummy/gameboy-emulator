package com.malcolmcrum.gameboy.operations

import assertk.assertThat
import com.malcolmcrum.gameboy.MMU
import com.malcolmcrum.gameboy.OperationBuilder
import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.hex
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class OperationTest(vararg var opcode: UByte, var initial: State = State(), var expected: State = State()) {
    private val log = KotlinLogging.logger {}

    val registers = Registers()
    val mmu = MMU().apply { inBios = false }
    val operations = OperationBuilder(registers, mmu, { null }).operations

    fun execute() {
        log.debug { "Initial: $initial" }
        givenRegisters(initial)
        givenROM(initial.pc ?: 0u, opcode.toList().plus(initial.args))
        givenRAM(initial.ram)

        executeInstruction()

        assertThat(registers, operationDescription(opcode[0])).isEqualTo(expected)
        assertThat(mmu, operationDescription(opcode[0])).isEqualTo(expected.ram)
    }

    private fun operationDescription(opcode: UByte) = "${opcode.hex()}: ${operations[opcode.toInt()]}"

    private fun executeInstruction() {
        for (op in opcode) {
            operations[op.toInt()].operation.invoke()
        }
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

@ExperimentalUnsignedTypes
fun test(vararg instructions: Int, block: OperationTest.() -> Unit) {
    val instructionBytes = instructions.map { it.toUByte() }.toUByteArray()
    OperationTest(*instructionBytes).apply(block).execute()
}