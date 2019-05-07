package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class INCTest {

    @Nested
    @DisplayName("INC (HL)")
    inner class INC_HL {
        private val opcode = 0x34

        @Test
        fun `0xf1++ = 0xf2`() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0xF1u))
                expected = State(ram = mapOf(0x4433u to 0xF2u))
            }
        }

        @Test
        fun zero() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0x00u))
                expected = State(ram = mapOf(0x4433u to 0x01u))
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(hl = 0x4433u, ram = mapOf(0x4433u to 0xFFu))
                expected = State(ram = mapOf(0x4433u to 0x00u), f = CARRY_FLAG or ZERO_FLAG)
            }
        }
    }

    @Test
    fun `INC A`() {
        test(0x3c) {
            initial = State(a = 0x88u)
            expected = State(a = 0x89u)
        }
    }

    @Test
    fun `INC DE`() {
        test(0x03) {
            initial = State(bc = 0x1288u)
            expected = State(bc = 0x1289u)
        }
    }
}