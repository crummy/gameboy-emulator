package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class LDTest {

    @Test
    fun `LD ($aabb),A`() {
        test(0xea) {
            initial = State(a = 0x01u, args = listOf(0x12u, 0x34u))
            expected = State(ram = mapOf(0x3412u to 0x01u))
        }
    }

    @Test
    fun `LD ($aabb),SP`() {
        test(0x08) {
            initial = State(sp = 0xFEEDu, args = listOf(0x12u, 0x34u))
            expected = State(ram = mapOf(0x3412u to 0xEDu, 0x3413u to 0xFEu))
        }
    }

    @Test
    fun `LD ($xx),A`() {
        test(0xe0) {
            initial = State(a = 0x69u, args = listOf(0x32u))
            expected = State(ram = mapOf(0x0032u to 0x69u))
        }
    }

    @Test
    fun `LD (BC),A`() {
        test(0x02) {
            initial = State(a = 0x34u, bc = 0x442Au)
            expected = State(ram = mapOf(0x442Au to 0x34u))
        }
    }

    @Test
    fun `LD (C),A`() {
        test(0xe2) {
            initial = State(a = 0x34u, c = 0xFAu)
            expected = State(ram = mapOf(0xFFFAu to 0x34u))
        }
    }


}