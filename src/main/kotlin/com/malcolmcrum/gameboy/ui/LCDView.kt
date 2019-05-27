package com.malcolmcrum.gameboy.ui

import com.malcolmcrum.gameboy.emulator.Colour
import com.malcolmcrum.gameboy.emulator.LCD
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import javafx.scene.paint.Color

@ExperimentalUnsignedTypes
class LCDView(val lcd: LCD) : Region() {
    val canvas = Canvas(LCD.WIDTH.toDouble(), LCD.HEIGHT.toDouble())

    init {
        this.children.add(canvas)

        for (x in 0 until LCD.WIDTH) {
            for (y in 0 until LCD.HEIGHT) {
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, Color.GREEN)
            }
        }
    }

    fun render() {
        for (x in 0 until LCD.WIDTH) {
            for (y in 0 until LCD.HEIGHT) {
                val colour = lcd.pixels[y][x]
                val color = when(colour) {
                    Colour.WHITE -> Color.WHITE
                    Colour.LIGHT_GRAY -> Color.LIGHTGRAY
                    Colour.DARK_GRAY -> Color.DARKGRAY
                    Colour.BLACK -> Color.BLACK
                }
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, color)
            }
        }
    }
}
