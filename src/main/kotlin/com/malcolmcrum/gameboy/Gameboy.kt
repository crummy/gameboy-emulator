package com.malcolmcrum.gameboy

import java.io.File

@ExperimentalUnsignedTypes
class Gameboy {
    val z80 = Z80()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Gameboy().boot(File("src/main/resources/tetris.gb"))

        }
    }

    private fun boot(rom: File) {
        val gameData = rom.readBytes().asUByteArray()
        z80.mmu.load(gameData)
        describeGame()
        repeat(99) {
            z80.execute()
            z80.registers.pc = z80.registers.pc and 0xFFFFu
            z80.clock.add(z80.registers.m, z80.registers.t)
        }
    }

    private fun describeGame() {
        val title = read(0x0134u..0x142u)
        println(String(title))
        val colourGB = read(0x0143u) == 0x80u.toUByte()
        println("colour: $colourGB")
        val cartridge = read(0x0147u)
        println("cartridge type: $cartridge")
        val romSize = read(0x0148u)
        println("ROM size: $romSize")
        val ramSize = read(0x0149u)
        println("RAM size: $ramSize")
        val japanese = read(0x014Au)
        println("japanese: $japanese")
    }

    fun read(range: UIntRange): ByteArray {
        return range.map { z80.mmu.rb(it.toUShort()) }.toUByteArray().asByteArray()
    }

    fun read(addr: UShort): UByte {
        return z80.mmu.rb(addr)
    }
}