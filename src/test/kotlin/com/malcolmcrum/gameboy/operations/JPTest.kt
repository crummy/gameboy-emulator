package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class JPTest {

    @Test
    fun `JP (HL)`() {
        test(0xe9) {
            initial = State(hl = 0x1234u)
            expected = State(pc = 0x1234u)
        }
    }

    @Nested
    @DisplayName("JP C,\$aabb")
    inner class JP_C_AABB {
        @Test
        fun `carry set`() {
            test(0xda) {
                initial = State(args = listOf(0x55u, 0x33u), f = CARRY_FLAG)
                expected = State(pc = 0x3355u)
            }
        }

        @Test
        fun `carry not set`() {
            test(0xda) {
                initial = State(pc = 0x2233u, args = listOf(0x55u, 0x33u), f = 0u)
                expected = State(pc = 0x2233u)
            }
        }
    }
}