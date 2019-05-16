package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class SCFTest {
    @Test
    fun `set already set carry flag`() {
        test(0x37) {
            initial = State(f = CARRY_FLAG)
            expected = State(f = CARRY_FLAG)
        }
    }

    @Test
    fun `set carry flag`() {
        test(0x37) {
            initial = State(f = 0u)
            expected = State(f = CARRY_FLAG)
        }
    }
}