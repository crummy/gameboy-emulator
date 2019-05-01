package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class RegisterTest {
    private val registers = Registers()

    @Test
    fun `test bc read`() {
        with (registers) {
            b = 0x12u
            c = 0x34u

            assertThat(bc).isEqualTo(0x3412u.toUShort())
        }
    }

    @Test
    fun `test bc write`() {
        with (registers) {
            bc = 0xDEAFu

            assertAll {
                assertThat(b).isEqualTo(0xDEu.toUByte())
                assertThat(c).isEqualTo(0xAFu.toUByte())
            }
        }
    }

}