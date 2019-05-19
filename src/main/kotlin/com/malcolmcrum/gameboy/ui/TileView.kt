package com.malcolmcrum.gameboy.ui

import com.malcolmcrum.gameboy.emulator.Colour.*
import com.malcolmcrum.gameboy.emulator.GPU
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import javafx.scene.paint.Color

@ExperimentalUnsignedTypes
class TileView(val gpu: GPU) : Region() {
    val canvas = Canvas(160.0, 144.0)

    init {
        children.add(canvas)
        styleClass.add("tiles")
        render()
    }

    fun render() {
        val pixels = canvas.graphicsContext2D.pixelWriter
        for (i in (-128..127)) {
            val tile = gpu.getTile(0, i)
            tile.getPixels().forEach {
                val (x, y) = it.key
                val colour = lookupColour(it.value)
                pixels.setColor(x, y, colour)
            }
        }
    }

    private fun lookupColour(value: UByte): Color {
        val colour = gpu.bgPalette[value.toInt()] ?: throw IndexOutOfBoundsException(value.hex())
        return when (colour) {
            WHITE -> Color.WHITE
            LIGHT_GRAY -> Color.LIGHTGRAY
            DARK_GRAY -> Color.DARKGRAY
            BLACK -> Color.BLACK
        }
    }
}