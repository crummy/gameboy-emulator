package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.malcolmcrum.gameboy.utils.isEqualToByte
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class BytesTest {
    @Test
    fun `select upper byte`() {
        val short = 0xFE01.toUShort()

        assertThat(short.upperByte).isEqualToByte(0xFEu)
    }

    @Test
    fun `select lower byte`() {
        val short = 0xFE01.toUShort()

        assertThat(short.lowerByte).isEqualToByte(0x01u)
    }

    @Test
    fun `read nibbles`() {
        val byte = 0xF9u.toUByte()

        assertAll {
            assertThat(byte.lowerNibble).isEqualToByte(0x9u)
            assertThat(byte.upperNibble).isEqualToByte(0xFu)
        }
    }

    @Test
    fun `write nibbles`() {
        val byte = createUByte(0xFu, 0x9u)

        assertAll {
            assertThat(byte.upperNibble).isEqualToByte(0xFu)
            assertThat(byte.lowerNibble).isEqualToByte(0x9u)
        }
    }

    @Test
    fun `create UByte`() {
        val byte = createUByte(0xFu, 0x9u)
        assertThat(byte).isEqualToByte(0xF9u)
    }

    @Test
    fun `create UShort`() {
        val short = createUShort(0xFFu, 0x99u)
        assertThat(short).isEqualTo(0xFF99u.toUShort())
    }
}