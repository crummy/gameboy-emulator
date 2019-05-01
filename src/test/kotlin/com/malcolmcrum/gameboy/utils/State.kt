package com.malcolmcrum.gameboy.utils

import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.hex

@ExperimentalUnsignedTypes
data class State(
    var a: UByte? = null,
    var b: UByte? = null,
    var c: UByte? = null,
    var d: UByte? = null,
    var e: UByte? = null,
    var h: UByte? = null,
    var l: UByte? = null,
    var f: UByte? = null,
    var pc: UShort? = null,
    var sp: UShort? = null,
    var m: UByte? = null,
    var t: UByte? = null
) {
    override fun toString(): String {
        var flags = ""
        f?.let {
            flags = if (it and Registers.ZERO_FLAG != 0u.toUByte()) "Z" else ""
            flags += if (it and Registers.SUBTRACT_FLAG != 0u.toUByte()) "N" else ""
            flags += if (it and Registers.HALF_CARRY_FLAG != 0u.toUByte()) "H" else ""
            flags += if (it and Registers.CARRY_FLAG != 0u.toUByte()) "C" else ""
        }
        var state = ""
        a?.let { state += "a=${it.hex}" }
        b?.let { state += "b=${it.hex}" }
        c?.let { state += "c=${it.hex}" }
        d?.let { state += "d=${it.hex}" }
        e?.let { state += "e=${it.hex}" }
        h?.let { state += "h=${it.hex}" }
        l?.let { state += "l=${it.hex}" }
        pc?.let { state += "pc=${it.hex}" }
        sp?.let { state += "sp=${it.hex}" }
        m?.let { state += "m=${it.hex}" }
        t?.let { state += "t=${it.hex}" }
        return "State($state $flags)"
    }
}