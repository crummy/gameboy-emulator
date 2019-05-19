package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.getBit
import com.malcolmcrum.gameboy.util.hex

@ExperimentalUnsignedTypes
class LCD {
    var LCDC: UByte = 0x40u
    var STAT: UByte = 0x41u
    var SCY: UByte = 0x42u
    var SCX: UByte = 0x43u
    var LY: UByte = 0x44u
    var LYC: UByte = 0x45u
    var DMA: UByte = 0x46u // TODO: on write, initiate DMA transfer
    var BGP: UByte = 0x47u
    var OBP0: UByte = 0x48u
    var OBP1: UByte = 0x49u
    var WY: UByte = 0x4au
    var WX: UByte = 0x4bu

    // TODO better names
    val lcdEnabled = LCDC.getBit(7)
    val windowUpperTileMap = LCDC.getBit(6)
    val windowEnabled = LCDC.getBit(5)
    val bgAndWindowTileDataSelect = LCDC.getBit(4)
    val bgUpperTileMap = LCDC.getBit(3)
    val spriteSize = LCDC.getBit(2)
    val spritesEnabled = LCDC.getBit(1)
    val bgEnabled = LCDC.getBit(0)

    val scrollY = SCX
    val scrollX = SCY

    val bgPalette: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(BGP.getBit(7), BGP.getBit(6)),
            2 to Colour.fromBytes(BGP.getBit(5), BGP.getBit(4)),
            1 to Colour.fromBytes(BGP.getBit(3), BGP.getBit(2)),
            0 to Colour.fromBytes(BGP.getBit(1), BGP.getBit(0))
    )
    val objectPalette0: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(OBP0.getBit(7), OBP0.getBit(6)),
            2 to Colour.fromBytes(OBP0.getBit(5), OBP0.getBit(4)),
            1 to Colour.fromBytes(OBP0.getBit(3), OBP0.getBit(2)),
            0 to Colour.fromBytes(OBP0.getBit(1), OBP0.getBit(0))
    )
    val objectPalette1: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(OBP1.getBit(7), OBP1.getBit(6)),
            2 to Colour.fromBytes(OBP1.getBit(5), OBP1.getBit(4)),
            1 to Colour.fromBytes(OBP1.getBit(3), OBP1.getBit(2)),
            0 to Colour.fromBytes(OBP1.getBit(1), OBP1.getBit(0))
    )

    operator fun get(address: UShort): UByte {
        return when (address.toUInt()) {
            0x40u -> LCDC
            0x41u -> STAT
            0x42u -> SCY
            0x43u -> SCX
            0x44u -> LY
            0x45u -> LYC
            0x46u -> DMA
            0x47u -> BGP
            0x48u -> OBP0
            0x49u -> OBP1
            0x4au -> WY
            0x4bu -> WX
            else -> throw IllegalAccessException(address.hex())
        }
    }

    operator fun set(address: UShort, value: UByte) {
        when (address.toUInt()) {
            0x40u -> LCDC = value
            0x41u -> STAT = value
            0x42u -> SCY = value
            0x43u -> SCX = value
            0x44u -> throw IllegalAccessException("LY is not writeable")
            0x45u -> LYC = value
            0x46u -> DMA = value
            0x47u -> BGP = value
            0x48u -> OBP0 = value
            0x49u -> OBP1 = value
            0x4au -> WY = value
            0x4bu -> WX = value
            else -> throw IllegalAccessException(address.hex())
        }
    }
}

enum class Colour(val value: Int) {
    WHITE(0), // or transparent for sprites
    LIGHT_GRAY(1),
    DARK_GRAY(2),
    BLACK(3);

    companion object {
        fun fromBytes(upperBit: Boolean, lowerBit: Boolean): Colour {
            val lsb = if (lowerBit) 1 else 0
            val msb = if (upperBit) 1 else 0
            val value = (msb shl 1) + lsb
            return values().find { it.value == value }!!
        }
    }
}