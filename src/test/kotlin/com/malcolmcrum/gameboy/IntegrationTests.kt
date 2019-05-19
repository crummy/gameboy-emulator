package com.malcolmcrum.gameboy

import assertk.assertThat
import com.malcolmcrum.gameboy.emulator.GBZ80
import com.malcolmcrum.gameboy.utils.State
import com.malcolmcrum.gameboy.utils.isEqualTo
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class IntegrationTests {
    val z80 = GBZ80()

    @Test
    fun `LD B,A then LD C,B`() {
        z80.mmu.load(0x47u, 0x48u)
        z80.registers.a = 0x45u

        z80.execute()
        z80.execute()

        assertThat(z80.registers).isEqualTo(State(a = 0x45u, b = 0x45u, c = 0x45u))
    }

    @Test
    fun `PUSH HL then POP BC`() {
        z80.mmu.load(0xe5u, 0xc1u)
        z80.registers.hl = 0x1234u
        z80.registers.sp = 0x4442u

        z80.execute()
        z80.execute()

        assertThat(z80.registers).isEqualTo(State(sp = 0x4442u, bc = 0x1234u))
    }

    @Test
    fun `DEC until zero`() {
        z80.mmu.load(0x32u, 0x05u, 0x20u, 0xfcu) // LD (HL-),A, DEC B, JR NZ -4
        z80.registers.b = 2u

        z80.execute()
        z80.execute()
        z80.execute()
        z80.execute()
        z80.execute()
        z80.execute()
        z80.execute() // should not have jumped

        assertThat(z80.registers).isEqualTo(State(b = 0u, pc = 0x0005u))
    }
}