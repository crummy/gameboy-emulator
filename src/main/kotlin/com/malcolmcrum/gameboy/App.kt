package com.malcolmcrum.gameboy

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings

class App : GameApplication() {
    override fun initSettings(settings: GameSettings) {
        settings.width = 1024
        settings.height = 600
        settings.title = "Gameboy"
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            GameApplication.launch(App::class.java, args)
        }
    }

}
