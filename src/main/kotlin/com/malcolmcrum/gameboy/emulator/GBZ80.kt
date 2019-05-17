package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.createUShort
import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class GBZ80 {
    private val log = KotlinLogging.logger {}

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()
    val gpu = GPU(mmu.videoRam, mmu.oam)
    val interruptsEnabled = false
    val operations = Operations(registers, mmu) { interruptsEnabled }

    fun execute() {
        val opCode = mmu[registers.pc].toInt()
        val operation = operations[opCode]
        log.debug { "${registers.pc.hex()}: ${opCode.toUByte().hex()} ${expandedMnemonic(operation.mnemonic)}" }
        registers.pc = operation.invoke(registers.pc)

        clock.add(registers.m, registers.t)
    }

    fun expandedMnemonic(operation: String): String {
        return operation.replace("\$aabb", createUShort(mmu[registers.pc + 2u], mmu[registers.pc + 1u]).hex())
                .replace("\$xx", mmu[registers.pc + 1u].hex())
    }

}