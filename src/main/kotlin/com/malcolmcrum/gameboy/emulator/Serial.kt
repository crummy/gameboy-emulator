package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.hex

@ExperimentalUnsignedTypes
class Serial {
    var SB: UByte = 0u
    var SC: UByte = 0u

    operator fun get(address: UShort): UByte {
        return when(address.toUInt()) {
            0xFF01u -> SB
            0xFF02u -> SC
            else -> throw IllegalAccessException(address.hex())
        }
    }

    operator fun set(address: UShort, value: UByte) {
        when(address.toUInt()) {
            0xFF01u -> SB = value
            0xFF02u -> SC = value
            else -> throw IllegalAccessException(address.hex())
        }
    }
}