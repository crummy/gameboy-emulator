package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class RETTest {
    @Test
    fun RET() {
        test(0xc9) {
            initial = State(sp = 0x1234u, ram = mapOf(0x1234u to 0xAAu, 0x1235u to 0xBBu))
            expected = State(sp = 0x1236u, pc = 0xBBAAu)
        }
    }

    @Test
    fun `RET C, carry enabled`() {
        test(0xd8) {
            initial = State(sp = 0x1234u, ram = mapOf(0x1234u to 0xAAu, 0x1235u to 0xBBu), f = CARRY_FLAG)
            expected = State(sp = 0x1236u, pc = 0xBBAAu)
        }
    }

    @Test
    fun `RET C, carry disabled`() {
        test(0xd8) {
            initial = State(sp = 0x1234u, ram = mapOf(0x1234u to 0xAAu, 0x1235u to 0xBBu), f = 0u)
            expected = State(sp = 0x1234u)
        }
    }
}