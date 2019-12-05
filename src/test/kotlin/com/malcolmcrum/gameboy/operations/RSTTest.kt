package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class RSTTest {

    @Test
    fun `RST $00`() {
        test(0xc7) {
            initial = State(sp = 0x1000u, pc = 0x2002u)
            expected = State(ram = mapOf(0x0ffeu to 0x02u, 0x0fffu to 0x20u), pc = 0x0000u, sp = 0x0ffeu)
        }
    }

    @Test
    fun `RST $08`() {
        test(0xcf) {
            initial = State(sp = 0x1000u, pc = 0x2002u)
            expected = State(ram = mapOf(0x0ffeu to 0x02u, 0x0fffu to 0x20u), pc = 0x0008u, sp = 0x0ffeu)
        }
    }

    @Test
    fun `RST $38`() {
        test(0xff) {
            initial = State(sp = 0x1000u, pc = 0x2002u)
            expected = State(ram = mapOf(0x0ffeu to 0x02u, 0x0fffu to 0x20u), pc = 0x0038u, sp = 0x0ffeu)
        }
    }

}