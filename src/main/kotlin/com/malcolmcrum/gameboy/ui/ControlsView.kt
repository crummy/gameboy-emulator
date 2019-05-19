package com.malcolmcrum.gameboy.ui

import com.malcolmcrum.gameboy.emulator.GBZ80
import javafx.scene.control.Button
import javafx.scene.layout.HBox

@ExperimentalUnsignedTypes
class ControlsView(z80: GBZ80) : HBox() {
    val playPause = Button("Play")
    val step = Button("Step")

    init {
        children.addAll(playPause, step)

        playPause.setOnAction {
            z80.isPaused = !z80.isPaused
            if (!z80.isPaused) {
                z80.execute()
            }
        }
        step.setOnAction {
            if (z80.isPaused) z80.step()
        }
    }
}