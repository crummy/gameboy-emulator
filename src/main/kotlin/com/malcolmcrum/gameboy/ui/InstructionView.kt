package com.malcolmcrum.gameboy.ui

import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.dsl.getGameState
import com.malcolmcrum.gameboy.App
import com.malcolmcrum.gameboy.Instruction
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import org.slf4j.LoggerFactory

@ExperimentalUnsignedTypes
class InstructionView(instructions: Map<UShort, Instruction>) : ScrollPane(), PropertyChangeListener<UShort> {
    val contents = VBox()
    val instructions: Map<UShort, Text> = instructions.map { it.key to toText(it.key, it.value) }.toMap()

    init {
        this.contents.styleClass.add("instructions")
        this.stylesheets.add("ui/css/styles.css")
        contents.children.addAll(this.instructions.values)
        this.content = contents

        getGameState().addListener(App.INSTRUCTION, this)
    }

    private fun toText(address: UShort, i: Instruction): Text {
        val text = Text()
        val secondOp = i.secondOpCode?.hex() ?: ""
        text.text = "${address.hex()} $secondOp: ${i.expandedName}"
        return text
    }

    override fun onChange(prev: UShort, now: UShort) {
        instructions[prev]?.styleClass?.remove("highlighted")
        instructions[now]?.let {
            it.styleClass.add("highlighted")
            this.vvalue = it.layoutY / contents.height
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(InstructionView::class.java)
    }
}