package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class RLTest {
    @Nested
    @DisplayName("RLA")
    inner class RLA {
        val opcode = 0x17

        @Test
        fun `0xff`() {
            test(opcode) {
                initial = State(0b11111111u, f = 0u)
                expected = State(0b11111110u, f = CARRY_FLAG)
            }
        }

        @Test
        fun `0x00, carry enabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = CARRY_FLAG)
                expected = State(0b00000001u, f = 0u)
            }
        }

        @Test
        fun `0x00, carry disabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = 0u)
                expected = State(0b00000000u, f = 0u)
            }
        }

        @Test
        fun `carry single bit`() {
            test(opcode) {
                initial = State(0b10000000u, f = 0u)
                expected = State(0b00000000u, f = CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("RLCA")
    inner class RLCA {
        val opcode = 0x07

        @Test
        fun `0xff`() {
            test(opcode) {
                initial = State(0b11111111u, f = 0u)
                expected = State(0b11111111u, f = CARRY_FLAG)
            }
        }

        @Test
        fun `0x00, carry enabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = CARRY_FLAG)
                expected = State(0b00000000u, f = 0u)
            }
        }

        @Test
        fun `0x00, carry disabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = 0u)
                expected = State(0b00000000u, f = 0u)
            }
        }

        @Test
        fun `carry single bit`() {
            test(opcode) {
                initial = State(0b10000000u, f = 0u)
                expected = State(0b00000001u, f = CARRY_FLAG)
            }
        }
    }
}