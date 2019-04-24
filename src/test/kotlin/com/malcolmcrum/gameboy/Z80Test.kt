package com.malcolmcrum.gameboy

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.BeforeEach

@ExperimentalUnsignedTypes
internal abstract class Z80Test {

    val z80 = Z80()

    @BeforeEach
    fun reset() {
        z80.reset()
    }

    fun assert(
            a: Int? = null,
            b: Int? = null,
            e: Int? = null,
            f: Int? = null,
            m: Int? = null,
            t: Int? = null
    ) {
        a?.let { assertThat(z80.registers.a).isEqualTo(it.toUByte()) }
        b?.let { assertThat(z80.registers.b).isEqualTo(it.toUByte()) }
        e?.let { assertThat(z80.registers.e).isEqualTo(it.toUByte()) }
        f?.let { assertThat(z80.registers.f).isEqualTo(it.toUByte()) }
        m?.let { assertThat(z80.registers.m).isEqualTo(it.toUByte()) }
        t?.let { assertThat(z80.registers.t).isEqualTo(it.toUByte()) }
    }
}