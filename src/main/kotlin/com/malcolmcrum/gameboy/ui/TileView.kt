package com.malcolmcrum.gameboy.ui

import com.malcolmcrum.gameboy.emulator.Colour.*
import com.malcolmcrum.gameboy.emulator.GPU
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.Region
import javafx.scene.paint.Color

@ExperimentalUnsignedTypes
class TileView(val gpu: GPU) : Region() {
    val imageView = ImageView()

    init {
        styleClass.add("tiles")
        imageView.isPreserveRatio = true
        imageView.fitHeight = 144 * 2.0
        render()
    }

    fun render() {
        val image = WritableImage(160, 144)
        val pixels = image.pixelWriter
        for (i in (-128..127)) {
            val tile = gpu.getTile(0, i)
            tile.getPixels().forEach {
                val (x, y) = it.key
                val colour = lookupColour(it.value)
                pixels.setColor(x, y, colour)
            }
        }
        imageView.image = image
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