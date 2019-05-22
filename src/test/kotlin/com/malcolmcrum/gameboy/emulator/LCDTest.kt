package com.malcolmcrum.gameboy.emulator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.malcolmcrum.gameboy.utils.isEqualToByte
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
internal class LCDTest {
    val gpu = GPU()
    val interrupts = Interrupts()
    lateinit var lcd: LCD

    @BeforeEach
    fun `set up LCD`() {
        lcd = LCD(gpu, interrupts)
    }

    @Test
    fun `mode transitions`() {
        assertThat(lcd.LY).isEqualToByte(0u)
        assertThat(lcd.mode).isEqualTo(Mode.OAM_READ)
        repeat(80) { lcd.tick() }
        assertThat(lcd.LY).isEqualToByte(0u)
        assertThat(lcd.mode).isEqualTo(Mode.VRAM_READ)
        repeat(172) { lcd.tick() }
        assertThat(lcd.mode).isEqualTo(Mode.HBLANK)
        repeat(204) { lcd.tick() }
        assertThat(lcd.mode).isEqualTo(Mode.OAM_READ)
        assertThat(lcd.LY).isEqualToByte(1u)
    }
}