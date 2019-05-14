package com.malcolmcrum.gameboy.emulator

@ExperimentalUnsignedTypes
data class Clock(
        var m: UShort = 0u,
        var t: UShort = 0u
) {
    fun add(m: UByte, t: UByte) {
        this.m = (this.m + m).toUShort()
        this.t = (this.t + t).toUShort()
    }
}