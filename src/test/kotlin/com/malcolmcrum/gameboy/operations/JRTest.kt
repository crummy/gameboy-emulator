package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class JRTest {
    @Test
    fun `JR $xx`() {
        test(0x18) {
            initial = State(pc = 0x01u, args = listOf(0x22u))
            expected = State(pc = 0x23u)
        }
    }

    @Test
    fun `JR C,$xx, carry set`() {
        test(0x38) {
            initial = State(pc = 0x01u, args = listOf(0x22u), f = CARRY_FLAG)
            expected = State(pc = 0x23u)
        }
    }

    @Test
    fun `JR Z,$xx, carry not set`() {
        test(0x38) {
            initial = State(pc = 0x01u, args = listOf(0x22u), f = 0u)
            expected = State(pc = 0x01u)
        }
    }
}