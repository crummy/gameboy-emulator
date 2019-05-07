package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class DECTest {

    @Nested
    @DisplayName("DEC (HL)")
    inner class DEC_HL {
        val opcode = 0x35

        @Test
        fun `0xf1-- = 0xf0`() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0xF1u))
                expected = State(ram = mapOf(0x4433u to 0xF0u))
            }
        }

        @Test
        fun zero() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0x01u))
                expected = State(ram = mapOf(0x4433u to 0x00u), f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun underflow() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0x00u))
                expected = State(ram = mapOf(0x4433u to 0xFFu), f = SUBTRACT_FLAG)
            }
        }
    }

    @Test
    fun `DEC A`() {
        test(0x3D) {
            initial = State(a = 0x88u)
            expected = State(a = 0x87u)
        }
    }

    @Test
    fun `DEC DE`() {
        test(0x0b) {
            initial = State(bc = 0x1288u)
            expected = State(bc = 0x1287u)
        }
    }
}