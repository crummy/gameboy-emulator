package com.malcolmcrum.gameboy

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.getGameScene
import com.almasb.fxgl.dsl.getGameState
import com.almasb.fxgl.dsl.getInput
import com.almasb.fxgl.input.UserAction
import com.malcolmcrum.gameboy.emulator.GBZ80
import com.malcolmcrum.gameboy.ui.InstructionView
import com.malcolmcrum.gameboy.ui.RegisterView
import com.malcolmcrum.gameboy.ui.TileView
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import java.io.File

@ExperimentalUnsignedTypes
class App : GameApplication() {
    private lateinit var tileView: TileView
    var z80 = GBZ80()

    init {
        val rom = File("src/main/resources/tetris.gb")
        val gameData = rom.readBytes().asUByteArray()
        z80.mmu.load(*gameData)
        z80.registers.pc = 0x100u
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
        registerView.translateX = 600.0
        borderPane.bottom = registerView

        val instructionView = InstructionView(parseInstructions(z80.mmu, z80.operations))
        borderPane.left = instructionView

        tileView = TileView(z80.gpu)
        borderPane.center = tileView

        getGameScene().addUINodes(borderPane)
    }
    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars[REGISTERS] = z80.registers
        vars[INSTRUCTION] = z80.registers.pc
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
class NextStep(val z80: GBZ80) : UserAction("step") {
    override fun onActionEnd() {
        z80.execute()
        getGameState().setValue(App.REGISTERS, z80.registers.copy().apply { f = z80.registers.f })
        getGameState().setValue(App.INSTRUCTION, z80.registers.pc)
    }
}
