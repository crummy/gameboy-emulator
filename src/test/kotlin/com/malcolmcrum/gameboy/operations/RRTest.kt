package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class RRTest {
    @Nested
    @DisplayName("RRA")
    inner class RRA {
        val opcode = 0x1f

        @Test
        fun `0xff`() {
            test(opcode) {
                initial = State(0b11111111u, f = 0u)
                expected = State(0b01111111u, f = Registers.CARRY_FLAG)
            }
        }

        @Test
        fun `0x00, carry enabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = Registers.CARRY_FLAG)
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
                initial = State(0b00000001u, f = 0u)
                expected = State(0b00000000u, f = Registers.CARRY_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("RRCA")
    inner class RRCA {
        val opcode = 0x07

        @Test
        fun `0x0f`() {
            test(opcode) {
                initial = State(0b11111111u, f = 0u)
                expected = State(0b11111111u, f = Registers.CARRY_FLAG)
            }
        }

        @Test
        fun `0x00, carry enabled`() {
            test(opcode) {
                initial = State(0b00000000u, f = Registers.CARRY_FLAG)
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
                expected = State(0b00000001u, f = Registers.CARRY_FLAG)
            }
        }
    }
}