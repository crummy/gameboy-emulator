package com.malcolmcrum.gameboy

import org.junit.jupiter.api.Test

@kotlin.ExperimentalUnsignedTypes
internal class AdditionTest : Z80Test() {

    @Test
    fun `basic addition`() {
        with(z80.registers) {
            a = 1u
            e = 2u
            z80.ADDr_e()

            assert(a = 3, e = 2, f = 0, m = 1, t = 4)
        }
    }

    @Test
    fun `addition overflow`() {
        with (z80.registers) {
            a = 255u
            e = 2u
            z80.ADDr_e()

            assert(a = 1, e = 2, f = 0x10)
        }
    }
}