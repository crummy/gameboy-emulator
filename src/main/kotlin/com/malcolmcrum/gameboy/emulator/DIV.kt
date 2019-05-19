package com.malcolmcrum.gameboy.emulator

@ExperimentalUnsignedTypes
class DIV : Ticks {
    private var cycles: UByte = 0u

    var value: UByte = 0u
        set(_) {
            field = 0u
        }

    override fun tick() {
        cycles++
        if (cycles == 0u.toUByte()) { // cycles per tick is 256, conveniently
            value++
        }
    }
}
