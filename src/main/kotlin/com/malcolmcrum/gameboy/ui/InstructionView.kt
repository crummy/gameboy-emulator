package com.malcolmcrum.gameboy.ui

import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.dsl.getGameState
import com.malcolmcrum.gameboy.App
import com.malcolmcrum.gameboy.emulator.Z80Operation
import com.malcolmcrum.gameboy.util.createUShort
import com.malcolmcrum.gameboy.util.hex
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import org.slf4j.LoggerFactory

@ExperimentalUnsignedTypes
class InstructionView(rom: UByteArray, operations: Array<Z80Operation>) : ScrollPane(), PropertyChangeListener<UShort> {
    val instructions: MutableMap<Int, Text> = HashMap()
    val contents = VBox()

    init {
        var address = 0
        while (address < rom.size - 2) {
            log.info("Loading address $address from rom of size ${rom.size}")
            val opcode = rom[address]
            val operation = operations[opcode.toInt()]
            val name = operation.mnemonic.replace("\$aabb", createUShort(rom[address + 2], rom[address + 1]).hex())
                    .replace("\$xx", rom[address + 1].hex())
            instructions[address] = Text(address.toUByte().hex() + ": " + name)
            address += operation.instructionBytes
        }
        contents.children.addAll(instructions.values)
        this.content = contents

        getGameState().addListener(App.INSTRUCTION, this)
    }

    override fun onChange(prev: UShort, now: UShort) {
        instructions[prev.toInt()]?.style = "-fx-text-fill: black;"
        instructions[now.toInt()]?.let {
            it.style = "-fx-text-fill: green;"
            this.vvalue = it.layoutY / contents.height
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(InstructionView::class.java)
    }
}