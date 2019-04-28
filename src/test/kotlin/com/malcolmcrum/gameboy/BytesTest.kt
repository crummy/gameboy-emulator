package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class BytesTest {
    @Test
    fun `select upper byte`() {
        val short = 0xFE01.toUShort()

        assertThat(short.upperByte()).isEqualTo(0xFE.toUByte())
    }

    @Test
    fun `select lower byte`() {
        val short = 0xFE01.toUShort()

        assertThat(short.lowerByte()).isEqualTo(0x01.toUByte())
    }

    @Test
    fun `read nibbles`() {
        val byte = 0xFEu.toUByte()

        assertAll {
            assertThat(byte.lowerNibble).isEqualTo(0xEu.toUByte())
            assertThat(byte.upperNibble).isEqualTo(0xFu.toUByte())
        }
    }

    @Test
    fun `write nibbles`() {
        val byte = createUByte(0xFu, 0xEu)

        assertAll {
            assertThat(byte.upperNibble).isEqualTo(0xFu.toUByte())
            assertThat(byte.lowerNibble).isEqualTo(0xEu.toUByte())
        }
    }
}