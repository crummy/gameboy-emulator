package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.parseInstruction
import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging
import kotlin.concurrent.thread


@ExperimentalUnsignedTypes
class GBZ80 {
    private val log = KotlinLogging.logger {}

    var isPaused = true
    val clock = Clock()
    val registers = Registers()
    val interrupts = Interrupts()
    val joypad = Joypad()
    val lcd = LCD()
    val gpu = GPU()
    val timer = Timer(interrupts)
    val div = DIV()
    val mmu = MMU(interrupts, joypad, lcd, gpu, timer, div)
    val operations = Operations(registers, mmu)

    fun execute() = thread(start = true) {
        while (!isPaused) {
            step()
        }
    }

    fun step() {
        val (opCode, operation) = operations[registers.pc, false]
        log.debug { "${registers.pc.hex()}: ${opCode.hex()} ${parseInstruction(operation.mnemonic, mmu, registers.pc)} - $registers" }
        registers.pc = operation.invoke(registers.pc)

        clock.add(registers.m, registers.t)
        timer.tick()
        div.tick()

        val interruptAddress = interrupts.getInterruptAddress()
        if (interruptAddress != null) {
            registers.pc = interruptAddress
        }
    }

}