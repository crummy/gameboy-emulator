package com.malcolmcrum.gameboy

import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class NopTest : Z80Test() {
    @Test
    fun `test clock increments`() {
        z80.NOP()

        assert(m = 1, t = 4)
    }
}