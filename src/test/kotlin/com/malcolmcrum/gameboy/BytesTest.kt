package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
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

    @Test
    fun `select bits`() {
        assertAll {
            assertThat(0xF0u.toUByte().getBit(7)).isTrue()
            assertThat(0xF0u.toUByte().getBit(0)).isFalse()

            val byte = 0b01000000.toUByte()
            assertThat(byte.getBit(7), "7").isFalse()
            assertThat(byte.getBit(6), "6").isTrue()
            assertThat(byte.getBit(5), "5").isFalse()
            assertThat(byte.getBit(4), "4").isFalse()
            assertThat(byte.getBit(3), "3").isFalse()
            assertThat(byte.getBit(2), "2").isFalse()
            assertThat(byte.getBit(1), "1").isFalse()
            assertThat(byte.getBit(0), "0").isFalse()

            assertThat(1u.toUByte().getBit(0)).isTrue()
            assertThat(0u.toUByte().getBit(0)).isFalse()
        }


    }
}