package com.malcolmcrum.gameboy.ui

import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.dsl.getGameState
import com.malcolmcrum.gameboy.App
import com.malcolmcrum.gameboy.emulator.Registers
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.layout.VBox
import javafx.scene.text.Text

@ExperimentalUnsignedTypes
class RegisterView : VBox(), PropertyChangeListener<Registers> {

    val a = Text()
    val b = Text()
    val c = Text()
    val d = Text()
    val e = Text()
    val h = Text()
    val l = Text()
    val f = Text()

    init {
        width = 100.0
        height = 100.0
        children.addAll(a, b, c, d, e, h, l, f)
        getGameState().addListener(App.REGISTERS, this)
    }

    override fun onChange(prev: Registers, now: Registers) {
        a.text = "A: " + now.a.hex()
        b.text = "B: " + now.b.hex()
        c.text = "C: " + now.c.hex()
        d.text = "D: " + now.d.hex()
        e.text = "E: " + now.e.hex()
        h.text = "H: " + now.h.hex()
        l.text = "L: " + now.l.hex()
        f.text = "F: " + now.f.hex()
    }
}