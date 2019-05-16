package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class SUBTest {
    @Nested
    @DisplayName("SUB \$xx")
    inner class SUB_XX {
        val opcode = 0xd6

        @Test
        fun `0-0=0`() {
            test(opcode) {
                initial = State(a = 0u, args = listOf(0x00u))
                expected = State(a = 0u, f = ZERO_FLAG or SUBTRACT_FLAG)
            }
        }

        @Test
        fun `3-2=1`() {
            test(opcode) {
                initial = State(a = 3u, args = listOf(0x02u))
                expected = State(a = 1u, f = SUBTRACT_FLAG)
            }
        }

        @Test
        fun overflow() {
            test(opcode) {
                initial = State(a = 2u, args = listOf(0x03u))
                expected = State(a = 0xFFu, f = SUBTRACT_FLAG or CARRY_FLAG)
            }
        }
    }

    @Test
    fun `SUB H`() {
        test(0x94) {
            initial = State(a = 3u, h = 0x02u)
            expected = State(a = 1u, f = SUBTRACT_FLAG)
        }
    }
}