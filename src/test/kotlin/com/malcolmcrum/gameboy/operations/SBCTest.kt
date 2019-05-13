package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class SBCTest {
    @Nested
    @DisplayName("SBC A,\$xx")
    inner class SBC_A_XX {
        val opcode = 0xde

        @Test
        fun `0-0-0=0`() {
            test(opcode) {
                initial = State(a = 0u, f = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun `8-4-1=3`() {
            test(opcode) {
                initial = State(a = 0x8u, f = CARRY_FLAG, args = listOf(0x04u))
                expected = State(a = 3u, f = SUBTRACT_FLAG)
            }
        }

        @Test
        fun `8-4-0=4`() {
            test(opcode) {
                initial = State(a = 0x8u, f = 0u, args = listOf(0x04u))
                expected = State(a = 4u, f = SUBTRACT_FLAG)
            }
        }

        @Test
        fun underflow() {
            test(opcode) {
                initial = State(a = 0x00u, f = 0u, args = listOf(0x01u))
                expected = State(a = 0xffu, f = SUBTRACT_FLAG or CARRY_FLAG)
            }
        }
    }

    @Test
    fun `SBC A,(HL)`() {
        test(0x9e) {
            initial = State(a = 0x0fu, hl = 0x1002u, f = CARRY_FLAG, ram = mapOf(0x1002u to 0x08u))
            expected = State(a = 0x06u, f = SUBTRACT_FLAG)
        }
    }

    @Test
    fun `SBC A,D`() {
        test(0x9a) {
            initial = State(a = 0x0fu, f = CARRY_FLAG, d = 0x08u)
            expected = State(a = 0x06u, f = SUBTRACT_FLAG)
        }
    }

}