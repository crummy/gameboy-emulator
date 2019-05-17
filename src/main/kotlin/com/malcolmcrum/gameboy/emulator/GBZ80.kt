package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.parseInstruction
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
        val operation = operations[registers.pc]
        log.debug { "${registers.pc.hex()}: ${opCode.toUByte().hex()} ${parseInstruction(operation.mnemonic, mmu, registers.pc.toInt())}" }
        registers.pc = operation.invoke(registers.pc)

        clock.add(registers.m, registers.t)
    }

}