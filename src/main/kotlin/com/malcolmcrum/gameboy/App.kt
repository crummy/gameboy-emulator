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
import javafx.scene.input.KeyCode
import java.io.File

@ExperimentalUnsignedTypes
class App : GameApplication() {
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
    }

    override fun initInput() {
        val input = getInput()

        input.addAction(NextStep(z80), KeyCode.SPACE)
    }

    override fun initUI() {
        val registerView = RegisterView()
        registerView.translateX = 600.0
        getGameScene().addUINode(registerView)

        val instructionView = InstructionView(parseInstructions(z80.mmu, z80.operations))
        instructionView.prefHeight = 600.0
        instructionView.prefWidth = 200.0
        instructionView.translateX = 0.0
        getGameScene().addUINode(instructionView)
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
        getGameState().setValue(App.REGISTERS, z80.registers.copy())
        getGameState().setValue(App.INSTRUCTION, z80.registers.pc)
    }
}
