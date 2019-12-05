package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.getBit
import com.malcolmcrum.gameboy.util.hex
import com.malcolmcrum.gameboy.util.withBit
import mu.KotlinLogging

@ExperimentalUnsignedTypes
class Interrupts {
    private val log = KotlinLogging.logger {}

    var interruptsEnabled: Boolean = false
    var IE: UByte = 0u
    var IF: UByte = 0u

    val vblank: Boolean = IE.getBit(0)
    val lcdStat: Boolean = IE.getBit(1)
    val timer: Boolean = IE.getBit(2)
    val serial: Boolean = IE.getBit(3)
    val joypad: Boolean = IE.getBit(4)

    operator fun get(address: UShort): UByte {
        return when (address.toUInt()) {
            0xFF0Fu -> IF
            0xFFFFu -> IE
            else -> throw IllegalAccessException(address.hex())
        }
    }

    operator fun set(address: UShort, value: UByte) {
        when (address.toUInt()) {
            0xFF0Fu -> IF = value
            0xFFFFu -> IE = value
            else -> throw IllegalAccessException(address.hex())
        }
    }

    fun setInterrupt(interrupt: Interrupt) {
        log.debug { "Interrupt $interrupt set" }
        IE = IE.withBit(interrupt.bit, 1)
    }

    fun getInterruptAddress(): UShort? {
        return Interrupt.values().find { interrupt ->
            interruptsEnabled && IF.getBit(interrupt.bit) && IE.getBit(interrupt.bit)
        }?.address
    }

}

@ExperimentalUnsignedTypes
enum class Interrupt(val bit: Int, val address: UShort) {
    VBLANK(0, 0x0040u),
    LCD_STAT(1, 0x0048u),
    TIMER(2, 0x0050u),
    SERIAL(3, 0x0058u),
    JOYPAD(4, 0x0060u)
}
