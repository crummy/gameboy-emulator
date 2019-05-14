package com.malcolmcrum.gameboy

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.getGameScene
import com.almasb.fxgl.dsl.getGameState
import com.almasb.fxgl.dsl.getInput
import com.almasb.fxgl.input.UserAction
import javafx.scene.input.KeyCode
import javafx.scene.text.Text

@ExperimentalUnsignedTypes
class App : GameApplication() {
    var z80 = GBZ80()

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
        val registers = Text()
        registers.translateX = 50.0
        registers.translateY = 50.0
        registers.textProperty().bind(getGameState().stringProperty(REGISTERS))

        getGameScene().addUINode(registers)
    }

    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars[REGISTERS] = z80.registers.toString()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java, args)
        }

        const val REGISTERS = "REGISTERS"
    }

}

class NextStep(val z80: GBZ80) : UserAction("step") {
    override fun onActionEnd() {
        z80.execute()
        getGameState().setValue(App.REGISTERS, z80.registers.toString())
    }
}
