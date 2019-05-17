package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.emulator.Tile.Companion.BYTES_PER_PIXEL
import com.malcolmcrum.gameboy.emulator.Tile.Companion.TILE_BYTES
import com.malcolmcrum.gameboy.util.getBit

// TODO: Does some of this belong outside the emulator package?
@ExperimentalUnsignedTypes
class GPU(val ram: UByteArray, oam: UByteArray) {
    // TODO better names
    val lcdEnabled = oam[LCDC].getBit(7)
    val windowUpperTileMap = oam[LCDC].getBit(6)
    val windowEnabled = oam[LCDC].getBit(5)
    val bgAndWindowTileDataSelect = oam[LCDC].getBit(4)
    val bgUpperTileMap = oam[LCDC].getBit(3)
    val spriteSize = oam[LCDC].getBit(2)
    val spritesEnabled = oam[LCDC].getBit(1)
    val bgEnabled = oam[LCDC].getBit(0)

    val scrollY = oam[SCX]
    val scrollX = oam[SCY]

    val bgPalette: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(oam[BGP].getBit(7), oam[BGP].getBit(6)),
            2 to Colour.fromBytes(oam[BGP].getBit(5), oam[BGP].getBit(4)),
            1 to Colour.fromBytes(oam[BGP].getBit(3), oam[BGP].getBit(2)),
            0 to Colour.fromBytes(oam[BGP].getBit(1), oam[BGP].getBit(0))
    )
    val objectPalette0: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(oam[OBP0].getBit(7), oam[OBP0].getBit(6)),
            2 to Colour.fromBytes(oam[OBP0].getBit(5), oam[OBP0].getBit(4)),
            1 to Colour.fromBytes(oam[OBP0].getBit(3), oam[OBP0].getBit(2)),
            0 to Colour.fromBytes(oam[OBP0].getBit(1), oam[OBP0].getBit(0))
    )
    val objectPalette1: Map<Int, Colour> = mapOf(
            3 to Colour.fromBytes(oam[OBP1].getBit(7), oam[OBP1].getBit(6)),
            2 to Colour.fromBytes(oam[OBP1].getBit(5), oam[OBP1].getBit(4)),
            1 to Colour.fromBytes(oam[OBP1].getBit(3), oam[OBP1].getBit(2)),
            0 to Colour.fromBytes(oam[OBP1].getBit(1), oam[OBP1].getBit(0))
    )

    fun getTile(set: Int, tile: Int): Tile {
        // We're in GPU ram, so offsets already start at 0x8000
        return when (set) {
            1 -> when (tile) {
                in (0..127) -> getTile(0x0000 + tile)
                in (128..255) -> getTile(0x0800 + tile)
                else -> throw IndexOutOfBoundsException(tile)
            }
            0 -> when (tile) {
                in (-128..-1) -> getTile(0x0800 + tile)
                in (0..127) -> getTile(0x1000 + tile)
                else -> throw IndexOutOfBoundsException(tile)
            }
            else -> throw IndexOutOfBoundsException(set)
        }
    }

    private fun getTile(tileAddress: Int): Tile {
        val pixels = UByteArray(TILE_BYTES)
        for ((index, address) in ((tileAddress until tileAddress + TILE_BYTES) step BYTES_PER_PIXEL).withIndex()) {
            val byte1 = ram[address]
            val byte2 = ram[address + 1]

            for (bit in (0..7)) {
                val lsb = if (byte1.getBit(bit)) 1 else 0
                val msb = if (byte2.getBit(bit)) 1 else 0
                val colour = (msb shl 1) + lsb
                pixels[index] = colour.toUByte()
            }
        }
        return Tile(pixels)
    }

    companion object {
        private const val LCDC = 0x40
        private const val STAT = 0x41
        private const val SCY = 0x42
        private const val SCX = 0x43
        private const val LY = 0x44
        private const val LX = 0x45
        private const val LYC = 0x46
        private const val BGP = 0x47
        private const val OBP0 = 0x48
        private const val OBP1 = 0x49
        private const val WY = 0x4a
        private const val WX = 0x4b

        const val MAX_TILES = 384
    }

}

@ExperimentalUnsignedTypes
class Tile(private val pixels: UByteArray) {

    fun getPixels(): Map<Pair<Int, Int>, UByte> {
        return pixels.mapIndexed { i, byte -> Pair(i / WIDTH, i % HEIGHT) to byte }.toMap()
    }

    operator fun get(x: Int, y: Int): UByte {
        return pixels[y * HEIGHT + x]
    }

    companion object {
        const val WIDTH = 8
        const val HEIGHT = 8
        const val BYTES_PER_PIXEL = 2
        const val TILE_BYTES = WIDTH * HEIGHT * BYTES_PER_PIXEL
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