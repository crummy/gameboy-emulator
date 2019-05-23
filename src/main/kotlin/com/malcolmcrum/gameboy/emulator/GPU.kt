package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.emulator.Tile.Companion.TILE_BYTES
import com.malcolmcrum.gameboy.util.get
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
            // from https://github.com/stan-roelofs/Kotlin-Gameboy-Emulator/blame/master/src/main/kotlin/memory/IO/Lcd.kt#L238
            tileRam[address.toInt()] = value

            val normalizedAddress = (address and 0x1FFEu).toInt()

            val tileIndex = normalizedAddress shr 4 and 511
            val rowIndex = normalizedAddress shr 1 and 7

            for (pixelIndex in (0..7)) {
                val sx = (1 shl 7 - pixelIndex).toUByte()
                val colour = (if (tileRam[normalizedAddress] and sx != 0u.toUByte()) 1 else 0) or if (tileRam[normalizedAddress + 1] and sx != 0u.toUByte()) 2 else 0
                tiles[tileIndex].pixels[rowIndex * Tile.WIDTH + pixelIndex] = colour.toUByte()
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