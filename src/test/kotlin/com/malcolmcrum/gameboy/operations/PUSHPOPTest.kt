package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class PUSHPOPTest {
    @Test
    fun `PUSH HL then POP BC`() {
        test(0xe5, 0xc1) {
            initial = State(sp = 0x4442u, hl = 0x1234u)
            expected = State(sp = 0x4442u, bc = 0x1234u)
        }
    }
}