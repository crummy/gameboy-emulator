package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class CCFTest {
    val opcode = 0x3f

    @Test
    fun `from set`() {
        test(opcode) {
            initial = State(f = CARRY_FLAG)
            expected = State(f = 0u)
        }
    }

    @Test
    fun `from unset`() {
        test(opcode) {
            initial = State(f = 0u)
            expected = State(f = CARRY_FLAG)
        }
    }
}