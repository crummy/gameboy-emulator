package com.malcolmcrum.gameboy

import assertk.assertThat
import com.malcolmcrum.gameboy.emulator.Joypad
import com.malcolmcrum.gameboy.emulator.MMU
import com.malcolmcrum.gameboy.utils.isEqualToByte
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class MMUTest {
    private val mmu = MMU(joypad = Joypad())

    @Test
    fun `working RAM shadow read`() {
        mmu[0xC000u] = 0x01u

        val workingCopy = mmu[0xE000u]

        assertThat(workingCopy).isEqualToByte(0x01u)
    }

    @Test
    fun `working RAM shadow write`() {
        mmu[0xE000u] = 0x01u

        val copy = mmu[0xC000u]

        assertThat(copy).isEqualToByte(0x01u)
    }

    @Test
    fun `working RAM read and write`() {
        mmu[0xC000u] = 0x01u

        val read = mmu[0xC000u]

        assertThat(read).isEqualToByte(0x01u)
    }

    @Test
    fun `read and write every byte`() {
        mmu.inBios = false // first, unload bios
        for (i in (0u..0xFFFFu)) {
            if (i in (0xFE00u..0xFF80u)) continue // can't write to these IO addresses
            mmu[i] = 0xAAu
            val read = mmu[i]
            assertThat(read).isEqualToByte(0xAAu)
        }
    }
}