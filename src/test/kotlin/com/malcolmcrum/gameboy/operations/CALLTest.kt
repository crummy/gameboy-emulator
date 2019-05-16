package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class CALLTest {
    @Test
    fun `CALL $aabb`() {
        test(0xcd) {
            initial = State(sp = 8u, args = listOf(0x44u, 0x33u))
            expected = State(pc = 0x3344u, sp = 6u)
        }
    }

    @Test
    fun `CALL C,$aabb with flag`() {
        test(0xdc) {
            initial = State(sp = 8u, f = CARRY_FLAG, args = listOf(0x44u, 0x33u))
            expected = State(pc = 0x3344u, sp = 6u)
        }
    }

    @Test
    fun `CALL C,$aabb without flag`() {
        test(0xdc) {
            initial = State(sp = 8u, f = 0u, args = listOf(0x44u, 0x33u))
            expected = State(pc = 0u, sp = 8u)
        }
    }

}