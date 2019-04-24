package com.malcolmcrum.gameboy

class App {
    fun getGreeting(): String {
        return "Hello world."
    }

    companion object {
        @JvmStatic
        fun main() {
            println(App().getGreeting())

        }
    }
}