package com.malcolmcrum.gameboy

import mu.KotlinLogging

@ExperimentalUnsignedTypes
class GBZ80 {
    private val log = KotlinLogging.logger {}

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()
    val interruptsEnabled = false
    val operations = OperationBuilder(registers, mmu) { interruptsEnabled }.operations

    fun execute() {
        val opCode = mmu[registers.pc].toInt()
        val operation = operations[opCode]
        log.debug { "${registers.pc.hex()}: ${opCode.toUByte().hex()} ${operation.name}" }
        operation.operation.invoke()

        registers.pc = (registers.pc + operation.instructionBytes.toUInt()).toUShort()
        clock.add(registers.m, registers.t)
    }

}

