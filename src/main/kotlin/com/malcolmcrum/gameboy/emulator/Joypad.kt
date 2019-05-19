package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.getBit
import com.malcolmcrum.gameboy.util.withBit
import java.util.*

@ExperimentalUnsignedTypes
class Joypad {
    val state: EnumMap<Button, Boolean> = EnumMap(Button::class.java)

    var flags: UByte = 0u
        set(value) {
            field = 0xffu
            Button.values().forEach { button ->
                if (!value.getBit(button.selectBit)) field = field.withBit(button.returnBit, 0)
            }
        }

    fun pressButton(button: Button) {
        state[button] = true
    }

    fun releaseButton(button: Button) {
        state[button] = false
    }

    enum class Button(val selectBit: Int, val returnBit: Int) {
        RIGHT(4, 0),
        LEFT(4, 1),
        UP(4, 2),
        DOWN(4, 3),
        A(5, 0),
        B(5, 1),
        SELECT(5, 2),
        START(5, 3)
    }
}