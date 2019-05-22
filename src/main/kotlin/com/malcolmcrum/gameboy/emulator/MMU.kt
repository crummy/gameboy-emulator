package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.hex
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class MMU(val interrupts: Interrupts = Interrupts(),
          val joypad: Joypad = Joypad(),
          val lcd: LCD = LCD(),
          val gpu: GPU = GPU(),
          val timer: Timer = Timer(interrupts),
          val div: DIV = DIV(),
          val serial: Serial = Serial(),
          val sound: Sound = Sound()
) {
    private val log = KotlinLogging.logger {}

    var inBios = true // bios is unmapped soon after boot

    val rom = UByteArray(0x8000)
    val workingRam = UByteArray(0x4000) // working ram
    val externalRam = UByteArray(0x2000) // external ram
    val zram = UByteArray(0x80) // zero page ram - high speed
    val oam = UByteArray(0xA0) // object attribute memory

    fun load(vararg game: UByte) {
        game.copyInto(rom)
    }

    operator fun get(addr: UInt): UByte {
        assert(addr <= 0xFFFFu)
        return get(addr.toUShort())
    }

    operator fun get(address: UShort, readOnly: Boolean = true): UByte {
        if (address == 0x0100u.toUShort() && !readOnly) inBios = false
        val value = when(address) {
            in (0x0000u until 0x0100u) -> if (inBios) BIOS[address] else rom[address]
            in (0x0000u until 0x1000u) -> rom[address]
            in (0x1000u until 0x8000u) -> rom[address]
            in (0x8000u until 0xA000u) -> gpu[address]
            in (0xA000u until 0xC000u) -> externalRam[address and 0x1FFFu]
            in (0xC000u until 0xE000u) -> workingRam[address and 0x1FFFu]
            in (0xE000u until 0xFE00u) -> workingRam[address and 0x1FFFu] // working ram shadow
            in (0xFE00u until 0xFEA0u) -> oam[address and 0xFFu]
            in (0xFEA0u until 0xFF00u) -> 0u
            0xFF00u.toUShort() -> joypad.flags
            in (0xFF01u..0xFF02u) -> serial[address]
            0xFF04u.toUShort() -> div.value
            in (0xFF05u..0xFF07u) -> timer[address]
            0xFF0Fu.toUShort() -> interrupts[address]
            in (0xFF10u..0xFF26u) -> sound[address]
            in (0xFF40u..0xFF4Bu) -> lcd[address and 0xFFu]
            in (0xFF80u until 0xFFFFu) -> zram[address and 0x7Fu]
            0xFFFFu.toUShort() -> interrupts[address]
            else -> throw IllegalAccessException(address.toString())
        }
        log.trace { "mmu[${address.hex()}] == ${value.hex()}"}
        return value
    }

    operator fun set(address: UInt, value: UByte) {
        assert(address <= 0xFFFFu)
        set(address.toUShort(), value)
    }

    operator fun set(address: UShort, value: UByte) {
        log.trace { " ${value.hex()} -> mmu[${address.hex()}]"}
        when(address) {
            in (0x0000u..0x1000u) -> {
                if (inBios) throw IllegalAccessException("Cannot write to BIOS")
                else rom[address] = value
            }
            in (0x1000u until 0x8000u) -> rom[address] = value
            in (0x8000u until 0xA000u) -> gpu[address] = value
            in (0xA000u until 0xC000u) -> externalRam[address and 0x1FFFu] = value
            in (0xC000u until 0xE000u) -> workingRam[address and 0x1FFFu] = value
            in (0xE000u until 0xFE00u) -> workingRam[address and 0x1FFFu] = value
            in (0xFE00u until 0xFEA0u) -> oam[address and 0xFFu] = value
            in (0xFEA0u until 0xFF00u) -> throw IllegalAccessException(address.hex())
            0xFF00u.toUShort() -> joypad.flags = value
            in (0xFF01u..0xFF02u) -> serial[address] = value
            0xFF04u.toUShort() -> div.value = value
            in (0xFF05u..0xFF07u) -> timer[address] = value
            0xFF0Fu.toUShort() -> interrupts[address] = value
            in (0xFF10u..0xFF26u) -> sound[address] = value
            in (0xff40u..0xff4bu) -> lcd[address and 0xffu] = value
            in (0xFF80u until 0xFFFFu) -> zram[address and 0x7Fu] = value
            0xFFFFu.toUShort() -> interrupts[address] = value
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