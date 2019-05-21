package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class CPLTest {
    val opcode = 0x2f

    @Test
    fun `invert 0`() {
        test(opcode) {
            initial = State(a = 0u)
            expected = State(a = 0xFFu)
        }
    }

    @Test
    fun `invert ones and zeroes`() {
        test(opcode) {
            initial = State(a = 0b11011010u)
            expected = State(a = 0b00100101u)
        }
    }

    @Test
    fun `zero flag`() {
        test(opcode) {
            initial = State(a = 0xFFu)
            expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG or HALF_CARRY_FLAG)
        }
    }
}