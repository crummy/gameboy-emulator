package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.emulator.Tile.Companion.TILE_BYTES
import com.malcolmcrum.gameboy.util.get
import com.malcolmcrum.gameboy.util.getBit
import com.malcolmcrum.gameboy.util.hex

// TODO: Does some of this belong outside the emulator package?
@ExperimentalUnsignedTypes
class GPU {
    private val tileRam = UByteArray(0x1800)
    private val mapRam = UByteArray(0x0800)
    val tiles = Array(MAX_TILES) { Tile(UByteArray(TILE_BYTES)) }

    fun getTile(upperTileMap: Boolean, tile: Int): Tile {
        // TODO: fix offsets
        return when (upperTileMap) {
            false -> tiles[tile]
            true -> tiles[256 + tile]
        }
    }


    operator fun get(address: UShort): UByte {
        return when(address.toInt() and 0x1FFF) {
            in (0 until 0x1800) -> tileRam[address]
            in (0x1800 until 0x2000) -> mapRam[address - 0x1800u]
            else -> throw IllegalAccessException(address.hex())
        }
    }

    operator fun set(absoluteAddress: UShort, value: UByte) {
        val address = absoluteAddress and 0x1FFFu
        val tileMapData = address >= 0x1800u
        if (tileMapData) {
            mapRam[address.toInt() - 0x1800] = value
        } else {
            tileRam[address.toInt()] = value

            val normalizedAddress = address and 0x1FFEu
            val byte1 = tileRam[normalizedAddress]
            val byte2 = tileRam[normalizedAddress + 1u]

            val tileIndex = address / 16u
            val rowIndex = (address % 16u) / 2u

            for (pixelIndex in (0..7)) {
                val lsb = if (byte1.getBit(pixelIndex)) 1u else 0u
                val msb = if (byte2.getBit(pixelIndex)) 1u else 0u
                val colour = ((msb shl 1) or lsb).toUByte()
                tiles[tileIndex.toInt()].pixels[rowIndex.toInt() * Tile.WIDTH + pixelIndex] = colour
            }
        }
    }

    companion object {
        const val MAX_TILES = 384
    }
}

@ExperimentalUnsignedTypes
class Tile(val pixels: UByteArray) {

    fun getPixels(): Map<Pair<Int, Int>, UByte> {
        return pixels.mapIndexed { i, byte -> Pair(i % WIDTH, i / HEIGHT) to byte }.toMap()
    }

    operator fun get(x: Int, y: Int): UByte {
        return pixels[y * WIDTH + x]
    }

    companion object {
        const val WIDTH = 8
        const val HEIGHT = 8
        const val BYTES_PER_PIXEL = 2
        const val TILE_BYTES = WIDTH * HEIGHT * BYTES_PER_PIXEL
    }
}