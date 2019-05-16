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
    val pc = Text()
    val sp = Text()
    val flags = Text()

    init {
        this.stylesheets.add("ui/css/styles.css")
        this.styleClass.add("monospaced")
        children.addAll(a, b, c, d, e, h, l, f, pc, sp, flags)
        getGameState().addListener(App.REGISTERS, this)
    }

    override fun onChange(prev: Registers, now: Registers) {
        println(now)
        a.text = "A: " + now.a.hex()
        if (prev.a != now.a) a.styleClass.add("highlighted") else a.styleClass.remove("highlighted")
        b.text = "B: " + now.b.hex()
        if (prev.b != now.b) b.styleClass.add("highlighted") else b.styleClass.remove("highlighted")
        c.text = "C: " + now.c.hex()
        if (prev.c != now.c) c.styleClass.add("highlighted") else c.styleClass.remove("highlighted")
        d.text = "D: " + now.d.hex()
        if (prev.d != now.d) d.styleClass.add("highlighted") else d.styleClass.remove("highlighted")
        e.text = "E: " + now.e.hex()
        if (prev.e != now.e) e.styleClass.add("highlighted") else e.styleClass.remove("highlighted")
        h.text = "H: " + now.h.hex()
        if (prev.h != now.h) h.styleClass.add("highlighted") else h.styleClass.remove("highlighted")
        l.text = "L: " + now.l.hex()
        if (prev.l != now.l) l.styleClass.add("highlighted") else l.styleClass.remove("highlighted")
        f.text = "F: " + now.f.hex()
        if (prev.f != now.f) f.styleClass.add("highlighted") else f.styleClass.remove("highlighted")
        pc.text = "PC: " + now.pc.hex()
        if (prev.pc != now.pc) f.styleClass.add("highlighted") else pc.styleClass.remove("highlighted")
        sp.text = "SP: " + now.sp.hex()
        if (prev.sp != now.sp) sp.styleClass.add("highlighted") else sp.styleClass.remove("highlighted")
        flags.text = toFlags(now)
    }

    private fun toFlags(now: Registers): String {
        var s = ""
        if (now.zero) s += "Z"
        if (now.carry) s += "C"
        if (now.halfCarry) s += "H"
        if (now.subtract) s += "N"
        return s
    }
}