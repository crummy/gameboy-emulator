package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExperimentalUnsignedTypes
internal class DAATest {
    val opcode = 0x27

    @ParameterizedTest
    @CsvSource(
            "0, 0x00, false, false",
            "10, 0x10, false, false",
            "15, 0x15, false, false",
            "16, 0x16, true, false",
            "50, 0x50, true, false",
            "99, 0x99, true, false"
    )
    fun `sample values`(hex: Int, dec: Int, halfCarry: Boolean, carry: Boolean) {
        var f: UByte = 0u
        if (carry) f = f or CARRY_FLAG
        if (halfCarry) f = f or HALF_CARRY_FLAG
        test(opcode) {
            initial = State(a = hex.toUByte(), f = f)
            expected = State(a = dec.toUByte())
        }
    }

    @Test
    fun `0x0A to 10`() {
        test(opcode) {
            initial = State(a = 0x0Au)
            expected = State(a = 0x10u)
        }
    }

    @Test
    fun `0x0F to 15`() {
        test(opcode) {
            initial = State(a = 0x0Fu)
            expected = State(a = 0x15u)
        }
    }

    @Test
    fun `0x1F to 31`() {
        test(opcode) {
            initial = State(a = 0x1Fu, f = HALF_CARRY_FLAG)
            expected = State(a = 0x31u)
        }
    }

    @Test
    fun `0x10 to 16`() {
        test(opcode) {
            initial = State(a = 0x10u, f = HALF_CARRY_FLAG)
            expected = State(a = 0x16u)
        }
    }

    @Test
    fun `0x0B to 11`() {
        test(opcode) {
            initial = State(a = 0x0Bu, f = HALF_CARRY_FLAG)
            expected = State(a = 0x11u)
        }
    }
}