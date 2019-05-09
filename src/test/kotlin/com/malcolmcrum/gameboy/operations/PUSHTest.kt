package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class PUSHTest {

    @Test
    fun `PUSH AF`() {
        test(0xf5) {
            initial = State(sp = 0x4442u, a = 0x12u, f = 0xFFu)
            expected = State(sp = 0x4440u, ram = mapOf(0x4441u to 0x12u, 0x4440u to 0xF0u))
        }
    }

    @Test
    fun `PUSH HL`() {
        test(0xe5) {
            initial = State(sp = 0x4442u, h = 0x12u, l = 0xFFu)
            expected = State(sp = 0x4440u, ram = mapOf(0x4441u to 0x12u, 0x4440u to 0xFFu))
        }
    }
}