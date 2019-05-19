package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class MMU(val joypad: Joypad = Joypad(),
          val gpu: GPU = GPU(),
          val lcd: LCD = LCD(),
          val timer: Timer = Timer(),
          val div: DIV = DIV()
) {
    private val log = KotlinLogging.logger {}

    var inBios = true // bios is unmapped soon after boot

    val bios = UByteArray(0x00FF)
    val rom = UByteArray(0x8000)
    val workingRam = UByteArray(0x4000) // working ram
    val externalRam = UByteArray(0x2000) // external ram
    val zram = UByteArray(0x80) // high speed ram?
    val oam = UByteArray(0xA0) // object attribute memory

    fun load(vararg game: UByte) {
        inBios = false
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
            in (0x8000u until 0xA000u) -> gpu[addr and 0x1FFFu]
            in (0xA000u until 0xC000u) -> externalRam[addr and 0x1FFFu]
            in (0xC000u until 0xE000u) -> workingRam[addr and 0x1FFFu]
            in (0xE000u until 0xFE00u) -> workingRam[addr and 0x1FFFu] // working ram shadow
            in (0xFE00u until 0xFEA0u) -> oam[addr and 0xFFu]
            in (0xFEA0u until 0xFF00u) -> 0u
            0xFF00u.toUShort() -> joypad.flags
            0xFF04u.toUShort() -> div.value
            in (0xFF05u..0xFF07u) -> timer[addr]
            in (0xFF01u until 0xFF40u) -> TODO() // io control handling
            in (0xFF40u..0xFF4Bu) -> lcd[addr and 0xFFu]
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
            in (0x8000u until 0xA000u) -> gpu[address and 0x1FFFu] = value
            in (0xA000u until 0xC000u) -> externalRam[address and 0x1FFFu] = value
            in (0xC000u until 0xE000u) -> workingRam[address and 0x1FFFu] = value
            in (0xE000u until 0xFE00u) -> workingRam[address and 0x1FFFu] = value
            in (0xFE00u until 0xFEA0u) -> oam[address and 0xFFu] = value
            in (0xFEA0u until 0xFF00u) -> throw IllegalAccessException(address.hex())
            0xFF00u.toUShort() -> joypad.flags = value
            0xFF04u.toUShort() -> div.value = value
            in (0xFF05u..0xFF07u) -> timer[address] = value
            in (0xFF01u until 0xFF40u) -> throw IllegalAccessException(address.hex())
            in (0xff40u..0xff4bu) -> lcd[address and 0xffu] = value
            in (0xFF80u..0xFFFFu) -> zram[address and 0x7Fu] = value
            else -> throw ArrayIndexOutOfBoundsException(address.hex())
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