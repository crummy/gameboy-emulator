package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class MMU {
    private val log = KotlinLogging.logger {}

    var inBios = true // bios is unmapped soon after boot

    val bios = UByteArray(0x00FF)
    val rom = UByteArray(0x8000)
    val workingRam = UByteArray(0x4000) // working ram
    val externalRam = UByteArray(0x2000) // external ram
    val zram = UByteArray(0x80) // high speed ram?
    val videoRam = UByteArray(0xA000-0x8000) // video ram (belongs in GPU?)
    val oam = UByteArray(0xA0) // object attribute memory

    fun load(vararg game: UByte) {
        inBios = false
        game.forEachIndexed { i, byte ->
            if (i % 40 == 39) println()
            print(String.format("%02x ", byte.toInt()))
        }
        game.copyInto(rom)
        println()
    }

    operator fun get(addr: UInt): UByte {
        assert(addr <= 0xFFFFu)
        return get(addr.toUShort())
    }

    operator fun get(addr: UShort): UByte {
        val value = when(addr) {
            in (0x0000u..0x1000u) -> {
                if (addr == 0x0100u.toUShort()) inBios = false
                if (inBios) bios[addr] else rom[addr]
            }
            in (0x1000u until 0x8000u) -> rom[addr]
            in (0x8000u until 0xA000u) -> videoRam[addr and 0x1FFFu]
            in (0xA000u until 0xC000u) -> externalRam[addr and 0x1FFFu]
            in (0xC000u until 0xE000u) -> workingRam[addr and 0x1FFFu]
            in (0xE000u until 0xFE00u) -> workingRam[addr and 0x1FFFu] // working ram shadow
            in (0xFE00u until 0xFEA0u) -> oam[addr and 0xFFu]
            in (0xFEA0u until 0xFF00u) -> 0u
            in (0xFF00u until 0xFF80u) -> TODO() // io control handling
            in (0xFF80u..0xFFFFu) -> zram[addr and 0x7Fu]
            else -> throw ArrayIndexOutOfBoundsException(addr.toString())
        }
        log.trace { "mmu[${addr.hex()}] contains ${value.hex()}"}
        return value
    }

    operator fun set(address: UInt, value: UByte) {
        assert(address <= 0xFFFFu)
        set(address.toUShort(), value)
    }

    operator fun set(address: UShort, value: UByte) {
        log.trace { "mmu[${address.hex()}] <= ${value.hex()}"}
        when(address) {
            in (0x0000u..0x1000u) -> {
                if (inBios) throw IllegalAccessException("Cannot write to BIOS")
                else rom[address] = value
            }
            in (0x1000u until 0x8000u) -> rom[address] = value
            in (0x8000u until 0xA000u) -> videoRam[address and 0x1FFFu] = value
            in (0xA000u until 0xC000u) -> externalRam[address and 0x1FFFu] = value
            in (0xC000u until 0xE000u) -> workingRam[address and 0x1FFFu] = value
            in (0xE000u until 0xFE00u) -> workingRam[address and 0x1FFFu] = value
            in (0xFE00u until 0xFEA0u) -> oam[address and 0xFFu] = value
            in (0xFEA0u until 0xFF00u) -> throw IllegalAccessException()
            in (0xFF00u until 0xFF80u) -> throw IllegalAccessException()
            in (0xFF80u..0xFFFFu) -> zram[address and 0x7Fu] = value
            else -> throw ArrayIndexOutOfBoundsException(address.toString())
        }
    }
}

@ExperimentalUnsignedTypes
private operator fun UByteArray.set(address: UShort, value: UByte) {
    set(address.toInt(), value)
}

@ExperimentalUnsignedTypes
private operator fun UByteArray.get(address: UShort): UByte {
    return get(address.toInt())
}