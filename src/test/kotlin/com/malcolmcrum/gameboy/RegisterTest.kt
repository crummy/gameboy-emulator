package com.malcolmcrum.gameboy

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.isEqualToByte
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
                assertThat(b).isEqualToByte(0xDEu)
                assertThat(c).isEqualToByte(0xAFu)
            }
        }
    }

    @Test
    fun `test zero flag`() {
        registers.zero = true
        assertThat(registers.f).isEqualToByte(ZERO_FLAG)
        registers.zero = false
        assertThat(registers.f).isEqualToByte(0u)
    }

    @Test
    fun `test subtract flag`() {
        registers.subtract = true
        assertThat(registers.f).isEqualToByte(SUBTRACT_FLAG)
        registers.subtract = false
        assertThat(registers.f).isEqualToByte(0u)
    }

    @Test
    fun `test carry flag`() {
        registers.carry = true
        assertThat(registers.f).isEqualToByte(CARRY_FLAG)
        registers.carry = false
        assertThat(registers.f).isEqualToByte(0u)
    }

    @Test
    fun `test half-carry flag`() {
        registers.halfCarry = true
        assertThat(registers.f).isEqualToByte(HALF_CARRY_FLAG)
        registers.halfCarry = false
        assertThat(registers.f).isEqualToByte(0u)
    }

    @Test
    fun `test all flags`() {
        registers.zero = true
        registers.subtract = true
        registers.carry = true
        registers.halfCarry = true
        assertThat(registers.f).isEqualToByte(ZERO_FLAG or SUBTRACT_FLAG or CARRY_FLAG or HALF_CARRY_FLAG)
    }

}

