package com.malcolmcrum.gameboy.ui

import com.malcolmcrum.gameboy.emulator.GPU
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import javafx.scene.paint.Color

class LCDView(gpu: GPU) : Region() {
    val canvas = Canvas(160.0, 144.0)

    init {
        this.children.add(canvas)

        for (x in 0..160) {
            for (y in 0..144) {
                canvas.graphicsContext2D.pixelWriter.setColor(x, y, Color.GREEN)
            }
        }

    }
}