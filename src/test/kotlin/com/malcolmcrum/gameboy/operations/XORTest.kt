package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class XORTest {
    @Test
    fun `XOR (HL)`() {
        test(0xae) {
            initial = State(a = 0b1100u, hl = 0x1002u, ram = mapOf(0x1002u to 0b0011u))
            expected = State(a = 0b1111u, f = 0u)
        }
    }

    @Test
    fun `XOR B`() {
        test(0xa8) {
            initial = State(a = 0b1100u, b = 0b1100u)
            expected = State(a = 0u, f = ZERO_FLAG)
        }
    }
}