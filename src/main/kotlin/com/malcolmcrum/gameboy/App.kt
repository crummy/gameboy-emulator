package com.malcolmcrum.gameboy

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.getGameScene
import com.almasb.fxgl.dsl.getGameState
import com.almasb.fxgl.dsl.getInput
import com.almasb.fxgl.input.UserAction
import com.malcolmcrum.gameboy.emulator.GBZ80
import com.malcolmcrum.gameboy.emulator.Joypad
import com.malcolmcrum.gameboy.ui.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import java.io.File

@ExperimentalUnsignedTypes
class App : GameApplication() {
    private lateinit var lcdView: LCDView
    private lateinit var tileView: TileView
    var z80 = GBZ80()

    init {
        val rom = File("src/main/resources/tetris.gb")
        val gameData = rom.readBytes().asUByteArray()
        z80.mmu.load(*gameData)
    }

    override fun initSettings(settings: GameSettings) {
        settings.width = 1024
        settings.height = 600
        settings.title = "Gameboy"
        settings.css = "styles.css"
    }

    override fun initInput() {
        val input = getInput()

        input.addAction(NextStep(z80), KeyCode.SPACE)
        input.addAction(ButtonPress(z80.joypad, Joypad.Button.UP), KeyCode.UP)
        input.addAction(object: UserAction("tile") {
            override fun onActionEnd() {
                tileView.render()
            }
        }, KeyCode.T)
    }

    override fun initUI() {
        val borderPane = BorderPane()
        borderPane.id = "everything"
        borderPane.prefHeight = 600.0
        borderPane.prefWidth = 1024.0

        val registerView = RegisterView()
        borderPane.bottom = registerView

        val instructionView = InstructionView(parseInstructions(z80.mmu, z80.operations))
        borderPane.left = instructionView

        tileView = TileView(z80.lcd, z80.gpu)
        borderPane.right = tileView

        lcdView = LCDView(z80.gpu)
        borderPane.center = lcdView

        val controlsView = ControlsView(z80)
        borderPane.top = controlsView

        getGameScene().addUINodes(borderPane)
    }

    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars[REGISTERS] = z80.registers
        vars[INSTRUCTION] = z80.registers.pc
    }

    override fun onUpdate(tpf: Double) {
        getGameState().setValue(REGISTERS, z80.registers.copy().apply { f = z80.registers.f })
        getGameState().setValue(INSTRUCTION, z80.registers.pc)
        tileView.render()
        lcdView.render()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, args)
        }

        const val REGISTERS = "REGISTERS"
        const val INSTRUCTION = "INSTRUCTION"
    }
}

@ExperimentalUnsignedTypes
class ButtonPress(val joypad: Joypad, val button: Joypad.Button) : UserAction("joypad") {
    override fun onActionBegin() {
        joypad.pressButton(button)
    }

    override fun onActionEnd() {
        joypad.releaseButton(button)
    }
}

@ExperimentalUnsignedTypes
class NextStep(val z80: GBZ80) : UserAction("step") {
    override fun onAction() {
        z80.step()
        getGameState().setValue(App.REGISTERS, z80.registers.copy().apply { f = z80.registers.f })
        getGameState().setValue(App.INSTRUCTION, z80.registers.pc)
    }
}
