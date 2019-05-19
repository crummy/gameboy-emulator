package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.parseInstruction
import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class GBZ80 {
    private val log = KotlinLogging.logger {}

    val clock = Clock()
    val registers = Registers()
    val joypad = Joypad()
    val gpu = GPU()
    val lcd = LCD()
    val mmu = MMU(joypad, gpu, lcd)
    val operations = Operations(registers, mmu)

    fun execute() {
        val opCode = mmu[registers.pc].toInt()
        val operation = operations[registers.pc]
        log.debug { "${registers.pc.hex()}: ${opCode.toUByte().hex()} ${parseInstruction(operation.mnemonic, mmu, registers.pc.toInt())}" }
        registers.pc = operation.invoke(registers.pc)

        clock.add(registers.m, registers.t)
    }

}