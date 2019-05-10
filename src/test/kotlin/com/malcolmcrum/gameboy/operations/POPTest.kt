package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class POPTest {
    @Test
    fun `POP AF`() {
        test(0xf1) {
            initial = State(sp = 0x2210u, ram = mapOf(0x2210u to 0xEEu, 0x2211u to 0xCCu))
            expected = State(sp = 0x2212u, af = 0xCCE0u)
        }
    }

    @Test
    fun `POP BC`() {
        test(0xc1) {
            initial = State(sp = 0x2210u, ram = mapOf(0x2210u to 0xEEu, 0x2211u to 0xCCu))
            expected = State(sp = 0x2212u, bc = 0xCCEEu)
        }
    }
}