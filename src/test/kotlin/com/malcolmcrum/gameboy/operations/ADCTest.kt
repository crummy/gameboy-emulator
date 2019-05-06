package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class ADCTest {

    @Nested
    @DisplayName("ADC \$xx")
    inner class ADCXX {
        val opcode = 0xce

        @Test
        fun `0+0+0=0`() {
            test(opcode) {
                initial = State(a = 0u, b = 0u, f = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG)
            }
        }

        @Test
        fun `0+1+0=1`() {
            test(opcode) {
                initial = State(a = 0u, args = listOf(0x01u))
                expected = State(a = 1u, f = 0u)
            }
        }

        @Test
        fun `1+1+1=3`() {
            test(opcode) {
                initial = State(a = 1u, f = CARRY_FLAG, args = listOf(0x01u))
                expected = State(a = 3u, f = 0u)
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(a = 0u, f = CARRY_FLAG, args = listOf(0xffu))
                expected = State(a = 0u, f = CARRY_FLAG or ZERO_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("ADC A,(HL)")
    inner class ADC_HL {
        val opcode = 0x8e

        @Test
        fun `0+0+0=0`() {
            test(opcode) {
                initial = State(a = 0u, hl = 0x20u, ram = mapOf(0x20u to 0x00u))
                expected = State(a = 0u, f = ZERO_FLAG)
            }
        }

        @Test
        fun `2+1+3=6`() {
            test(opcode) {
                initial = State(a = 2u, f = CARRY_FLAG, hl = 0x20u, ram = mapOf(0x20u to 0x3u))
                expected = State(a = 3u, f = 0u)
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(a = 1u, f = CARRY_FLAG, hl = 0x1000u, ram = mapOf(0x1000u to 0xFFu))
                expected = State(a = 0u, f = CARRY_FLAG or ZERO_FLAG)
            }
        }
    }

    @Nested
    @DisplayName("ADC A,B")
    inner class ADC_AB {
        val opcode = 0x88

        @Test
        fun `0+0+0=0`() {
            test(opcode) {
                initial = State(a = 0u, b = 0u, f = 0u)
                expected = State(a = 0u, b = 0u, f = ZERO_FLAG)
            }
        }

        @Test
        fun `1+3+0=4`() {
            test(opcode) {
                initial = State(a = 1u, b = 3u, f = 0u)
                expected = State(a = 4u)
            }
        }

        @Test
        fun `1+0+1=2`() {
            test(opcode) {
                initial = State(a = 1u, f = CARRY_FLAG)
                expected = State(a = 2u)
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(a = 0xFFu, b = 1u, f = CARRY_FLAG)
                expected = State(a = 0x1u, b = 1u, f = CARRY_FLAG)
            }
        }
    }
}

