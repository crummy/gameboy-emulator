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
        log.debug { "${registers.pc.hex()}: ${opCode.toUByte().hex()} ${detailedOpcode(operation.name)}" }
        registers.pc = operation.invoke(registers.pc)

        clock.add(registers.m, registers.t)
    }

    fun detailedOpcode(operation: String): String {
        return operation.replace("\$aabb", createUShort(mmu[registers.pc + 2u], mmu[registers.pc + 1u]).hex())
                .replace("\$xx", mmu[registers.pc + 1u].hex())
    }

}

