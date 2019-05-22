package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.emulator.Tile.Companion.BYTES_PER_PIXEL
import com.malcolmcrum.gameboy.emulator.Tile.Companion.TILE_BYTES
import com.malcolmcrum.gameboy.util.getBit

// TODO: Does some of this belong outside the emulator package?
@ExperimentalUnsignedTypes
class GPU : Ticks {
    val ram = UByteArray(0x2000)

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

    operator fun get(address: UShort): UByte {
        return ram[address.toInt() and 0x1FFF]
    }

    operator fun set(address: UShort, value: UByte) {
        ram[address.toInt() and 0x1FFF] = value
    }

    override fun tick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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