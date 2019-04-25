package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class MMU {
    var inBios = true // bios is unmapped soon after boot

    val bios = UByteArray(0x00FF-0x0000)
    val rom = UByteArray(0x8000-0x0000)
    val wram = UByteArray(0x7FFF-0x4000) // working ram
    val eram = UByteArray(0xBFFF-0xA000) // external ram
    val zram = UByteArray(1)
    val vram = UByteArray(0x9FFF-0x8000) // video ram (belongs in GPU?)
    val oam = UByteArray(160) // object attribute memory

    fun load(game: UByteArray) {
        inBios = false
        game.copyInto(rom)
        rom.forEachIndexed { i, byte ->
            if (i % 40 == 39) println()
            print(String.format("%02x ", byte.toInt()))
        }
        println()
    }

    fun rb(addr: UShort): UByte {
        return when(addr) {
            in (0x0000u..0x1000u) -> {
                if (addr == 0x0100u.toUShort()) inBios = false
                if (inBios) bios[addr] else rom[addr]
            }
            in (0x1000u until 0x8000u) -> rom[addr]
            in (0x8000u until 0xA000u) -> vram[addr and 0x1FFFu]
            in (0xA000u until 0xC000u) -> eram[addr and 0x1FFFu]
            in (0xC000u until 0xE000u) -> wram[addr and 0x1FFFu]
            in (0xE000u until 0xFE00u) -> wram[addr and 0x1FFFu] // working ram shadow
            in (0xFE00u until 0xFEA0u) -> oam[addr and 0xFFu]
            in (0xFEA0u until 0xFF00u) -> 0u
            in (0xFF00u until 0xFF80u) -> TODO() // io control handling
            in (0xFF80u..0xFFFFu) -> zram[addr and 0x7Fu]
            else -> throw ArrayIndexOutOfBoundsException(addr.toString())
        }
    }

    fun rw(addr: UShort): UShort {
        val firstByte = rb(addr)
        val secondByte = rb((addr + 1u).toUShort())
        return (firstByte + secondByte shr 8).toUShort()
    }

    fun wb(addr: UShort, value: UByte) {
        when(addr) {
            in (0x0000u..0x1000u) -> {
                if (inBios) throw IllegalAccessException("Cannot write to BIOS")
                else rom[addr] = value
            }
            in (0x1000u until 0x8000u) -> rom[addr] = value
            in (0x8000u until 0xA000u) -> vram[addr and 0x1FFFu] = value
            in (0xA000u until 0xC000u) -> eram[addr and 0x1FFFu] = value
            in (0xC000u until 0xE000u) -> wram[addr and 0x1FFFu] = value
            in (0xE000u until 0xFE00u) -> wram[addr and 0x1FFFu] = value
            in (0xFE00u until 0xFEA0u) -> oam[addr and 0xFFu] = value
            in (0xFEA0u until 0xFF00u) -> throw IllegalAccessException()
            in (0xFF00u until 0xFF80u) -> throw IllegalAccessException()
            in (0xFF80u..0xFFFFu) -> zram[addr and 0x7Fu] = value
            else -> throw ArrayIndexOutOfBoundsException(addr.toString())
        }
    }

    fun ww(addr: UShort, value: UShort) {
        wb(addr, value.toUByte())
        wb((addr + 1u).toUShort(), (value.toInt() shl 8).toUByte())
    }
}

@ExperimentalUnsignedTypes
private operator fun UByteArray.set(addr: UShort, value: UByte) {
    set(addr.toInt(), value)
}

@ExperimentalUnsignedTypes
private operator fun UByteArray.get(address: UShort): UByte {
    return get(address.toInt())
}