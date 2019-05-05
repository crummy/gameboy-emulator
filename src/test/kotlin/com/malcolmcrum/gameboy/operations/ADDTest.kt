package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class ADDTest {
    @Nested
    @DisplayName("ADD \$xx")
    inner class ADD_xx {
        val opcode = 0xc6

        @Test
        fun `0+0=0`() {
            test(opcode) {
                initial = State(a = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG)
            }
        }

        @Test
        fun `1+2=3`() {
            test(opcode) {
                initial = State(a = 1u, args = listOf(0x02u))
                expected = State(a = 3u, f = 0u)
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(a = 0xFFu, args = listOf(0x01u))
                expected = State(a = 0u, f = ZERO_FLAG or CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("ADD (HL)")
    inner class ADD_HL {
        val opcode = 0x86

        @Test
        fun `1+2=3`() {
            test(opcode) {
                initial = State(a = 0x01u, hl = 0x0034u, ram = mapOf(0x0034u to 0x02u))
                expected = State(a = 3u)
            }
        }
    }
}