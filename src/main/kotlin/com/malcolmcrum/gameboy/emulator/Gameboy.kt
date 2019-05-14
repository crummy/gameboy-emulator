package com.malcolmcrum.gameboy.emulator

import mu.KotlinLogging
import java.io.File

@ExperimentalUnsignedTypes
class Gameboy {
    private val log = KotlinLogging.logger {}

    val z80 = GBZ80()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Gameboy().boot(File("src/main/resources/tetris.gb"))

        }
    }

    private fun boot(rom: File) {
        val gameData = rom.readBytes().asUByteArray()
        z80.mmu.load(*gameData)
        z80.registers.pc = 0x100u
        describeGame()
        repeat(99) {
            z80.execute()
        }
    }

    private fun describeGame() {
        val title = read(0x0134u..0x142u)
        log.info { String(title) }
        val colourGB = read(0x0143u) == 0x80u.toUByte()
        log.info { "colour: $colourGB" }
        val cartridge = read(0x0147u)
        log.info { "cartridge type: $cartridge" }
        val romSize = read(0x0148u)
        log.info { "ROM size: $romSize" }
        val ramSize = read(0x0149u)
        log.info { "RAM size: $ramSize" }
        val japanese = read(0x014Au)
        log.info { "japanese: $japanese" }
    }

    fun read(range: UIntRange): ByteArray {
        return range.map { z80.mmu[it.toUShort()] }.toUByteArray().asByteArray()
    }

    fun read(addr: UShort): UByte {
        return z80.mmu[addr]
    }
}