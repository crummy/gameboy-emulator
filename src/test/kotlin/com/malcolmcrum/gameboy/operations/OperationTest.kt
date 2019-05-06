package com.malcolmcrum.gameboy.operations

import assertk.assertThat
import com.malcolmcrum.gameboy.MMU
import com.malcolmcrum.gameboy.OperationBuilder
import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.hex
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo

@ExperimentalUnsignedTypes
class OperationTest(val opcode: UByte, val description: String?, var initial: State = State(), var expected: State = State()) {
    val registers = Registers()
    val mmu = MMU().apply { inBios = false }
    val operations = OperationBuilder(registers, mmu, { null }).operations

    fun execute() {
        println("Initial: $initial")
        givenRegisters(initial)
        givenROM(listOf(opcode).plus(initial.args))
        givenRAM(initial.ram)

        executeInstruction()

        assertThat(registers, "${opcode.hex}: $description").isEqualTo(expected)
    }

    private fun executeInstruction() {
        val opCode = mmu[registers.pc].toInt()
        operations[opCode].operation.invoke()
    }

    private fun givenROM(instructions: List<UByte>) {
        instructions.forEachIndexed { index, instruction -> mmu[index.toUInt()] = instruction }
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
fun test(instruction: Int, name: String? = null, block: OperationTest.() -> Unit) {
    OperationTest(instruction.toUByte(), name).apply(block).execute()
}