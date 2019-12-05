package com.malcolmcrum.gameboy.ui

import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.dsl.getGameState
import com.malcolmcrum.gameboy.App
import com.malcolmcrum.gameboy.Instruction
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text

@ExperimentalUnsignedTypes
class InstructionView(instructions: Map<UShort, Instruction>) : ScrollPane(), PropertyChangeListener<UShort> {
    val contents = VBox()
    val instructions: Map<UShort, Text> = instructions.map { it.key to toText(it.key, it.value) }.toMap()

    init {
        contents.styleClass.add("monospaced")
        styleClass.add("scrollpane")
        contents.children.addAll(this.instructions.values)
        content = contents

        getGameState().addListener(App.INSTRUCTION, this)
    }

    private fun toText(address: UShort, i: Instruction): Text {
        val text = Text()
        text.text = "${address.hex()}: ${i.opCode.hex()} ${i.expandedName}"
        return text
    }

    override fun onChange(prev: UShort, now: UShort) {
        instructions[prev]?.styleClass?.remove("highlighted")
        instructions[now]?.let {
            it.styleClass.add("highlighted")
            this.vvalue = it.layoutY / contents.height
        }
    }
}