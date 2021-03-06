package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class ANDTest {
    @Nested
    @DisplayName("AND n8")
    inner class AND_XX {
        val opcode = 0xe6

        @Test
        fun `0 and 0 is 0`() {
            test(opcode) {
                initial = State(a = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG or HALF_CARRY_FLAG)
            }
        }

        @Test
        fun `0xFF and 0 is 0`() {
            test(opcode) {
                initial = State(a = 0xFFu, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG or HALF_CARRY_FLAG)
            }
        }

        @Test
        fun `0xF0 and 0x1F is 0x10`() {
            test(opcode) {
                initial = State(a = 0xF0u, args = listOf(0x1Fu))
                expected = State(a = 0x10u, f = HALF_CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("AND (HL)")
    inner class AND_HL {
        val opcode = 0xa6

        @Test
        fun `0xFF and 0x01 is 1`() {
            test(opcode) {
                initial = State(a = 0xFFu, hl = 0x1234u, ram = mapOf(0x1234u to 0x01u))
                expected = State(a = 0x01u, f = HALF_CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("AND D")
    inner class AND_D {
        val opcode = 0xa2

        @Test
        fun `0x10 and 0x01 is 0`() {
            test(opcode) {
                initial = State(a = 0x10u, d = 0x01u)
                expected = State(a = 0x0u, f = ZERO_FLAG or HALF_CARRY_FLAG)
            }
        }
    }
}