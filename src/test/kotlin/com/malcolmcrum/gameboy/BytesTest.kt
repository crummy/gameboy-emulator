package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.malcolmcrum.gameboy.util.*
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

            val ff = 0xffu.toUByte()
            assertThat(ff.getBit(7), "0xff bit 7").isTrue()
            assertThat(ff.getBit(6), "0xff bit 6").isTrue()
            assertThat(ff.getBit(5), "0xff bit 5").isTrue()
            assertThat(ff.getBit(4), "0xff bit 4").isTrue()
            assertThat(ff.getBit(3), "0xff bit 3").isTrue()
            assertThat(ff.getBit(2), "0xff bit 2").isTrue()
            assertThat(ff.getBit(1), "0xff bit 1").isTrue()
            assertThat(ff.getBit(0), "0xff bit 0").isTrue()
        }
    }

    @Test
    fun `set bits`() {
        val one = 0u.toUByte().withBit(0, 1)
        assertThat(one).isEqualToByte(0x01u)

        val two = 0u.toUByte().withBit(1, 1)
        assertThat(two).isEqualToByte(0x02u)

        val zero = 0xff.toUByte().withBit(7, 0)
                .withBit(6, 0)
                .withBit(5, 0)
                .withBit(4, 0)
                .withBit(3, 0)
                .withBit(2, 0)
                .withBit(1, 0)
                .withBit(0, 0)
        assertThat(zero).isEqualToByte(0u)
    }
}