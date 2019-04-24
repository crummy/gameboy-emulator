package com.malcolmcrum.gameboy

import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class ComparisonTest : Z80Test() {
    @Test
    fun `test values left unchanged`() {
        with(z80.registers) {
            a = 1u
            b = 2u
            z80.CPr_b()

            assert(a = 1, b = 2)
        }
    }

    @Test
    fun `test equal comparison`() {
        with(z80.registers) {
            a = 1u
            b = 1u
            z80.CPr_b()

            assert(f = 0x80 or 0x40)
        }
    }
}