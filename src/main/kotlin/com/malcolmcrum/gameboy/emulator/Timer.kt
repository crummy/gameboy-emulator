package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.getBit
import com.malcolmcrum.gameboy.util.hex

@ExperimentalUnsignedTypes
class Timer(val interrupts: Interrupts) : Ticks {
    val timerEnabled: Boolean
        get() = TAC.getBit(2)
    val selectedClock: Int
        get() {
            return when ((TAC and 0b11u).toUInt()) {
                0u -> clock0
                1u -> clock1
                2u -> clock2
                3u -> clock3
                else -> throw RuntimeException("impossible!")
            }
        }
    var count = 0

    var TIMA: UByte = 0u // Incremented every `selectedClock` cycles
    var TMA: UByte = 0u // When TIMA overflows, the value in TMA is placed in it
    var TAC: UByte = 0u // Control registers

    override fun tick() {
        if (timerEnabled) {
            count++
            if (count > selectedClock) {
                count = 0
                TIMA++

                val overflow = TIMA == 0u.toUByte()
                if (overflow) {
                    TIMA = TMA
                    interrupts.setInterrupt(Interrupt.TIMER)
                }
            }
        }
    }

    operator fun get(address: UShort): UByte {
        return when(address.toUInt()) {
            0xFF05u -> TIMA
            0xFF06u -> TMA
            0xFF07u -> TAC
            else -> throw IllegalAccessException(address.hex())
        }
    }

    operator fun set(address: UShort, value: UByte) {
        return when(address.toUInt()) {
            0xFF05u -> TIMA = value
            0xFF06u -> TMA = value
            0xFF07u -> TAC = value
            else -> throw IllegalAccessException(address.hex())
        }
    }

    companion object {
        val clock0 = 1024 //   4096 Hz
        val clock1 = 16 // 262144 Hz
        val clock2 = 64 //  65536 Hz
        val clock3 = 256 //  16384 Hz
    }
}