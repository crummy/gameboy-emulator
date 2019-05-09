package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class ORTest {
    @Test
    fun `OR $xx`() {
        test(0xf6) {
            initial = State(a = 0b0101u, args = listOf(0b0011u))
            expected = State(a = 0b0111u)
        }
    }

    @Test
    fun `OR L`() {
        test(0xb5) {
            initial = State(a = 0b1110u, l = 0b0001u)
            expected = State(a = 0b1111u)
        }
    }
}