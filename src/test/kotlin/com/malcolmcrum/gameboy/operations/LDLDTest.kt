package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class LDLDTest {
    @Test
    fun `LD A,B then LD B,C`() {
        test(0x78, 0x47) {
            initial = State(a = 0x66u)
            expected = State(c = 0x66u)
        }
    }
}