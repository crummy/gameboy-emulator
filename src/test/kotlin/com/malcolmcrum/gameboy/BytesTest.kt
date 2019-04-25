package com.malcolmcrum.gameboy

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
}