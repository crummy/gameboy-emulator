package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class CPTest {
    @Nested
    @DisplayName("CP \$xx")
    inner class CP_XX {
        val opcode = 0xfe

        @Test
        fun `0-0=0`() {
            test(opcode) {
                initial = State(a = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun `1-0xFF=0`() {
            test(opcode) {
                initial = State(a = 1u, args = listOf(0xFFu))
                expected = State(a = 1u, f = SUBTRACT_FLAG or CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("CP (HL)")
    inner class CP_HL {
        val opcode = 0xbe

        @Test
        fun `0-0=0`() {
            test(opcode) {
                initial = State(a = 0u, hl = 0x4432u, ram = mapOf(0x4432u to 0u))
                expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun `1-0xFF=0`() {
            test(opcode) {
                initial = State(a = 1u, hl = 0x4432u, ram = mapOf(0x4432u to 0xFFu))
                expected = State(a = 1u, f = SUBTRACT_FLAG or CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("CP L")
    inner class CP_L {
        val opcode = 0xbd

        @Test
        fun `0-0=0`() {
            test(opcode) {
                initial = State(a = 0u, l = 0u)
                expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun `1-0xFF=0`() {
            test(opcode) {
                initial = State(a = 1u, l = 0xFFu)
                expected = State(a = 1u, f = SUBTRACT_FLAG or CARRY_FLAG)
            }
        }
    }
}