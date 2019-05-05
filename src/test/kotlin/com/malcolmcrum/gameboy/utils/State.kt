package com.malcolmcrum.gameboy.utils

import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.hex
import com.malcolmcrum.gameboy.lowerByte
import com.malcolmcrum.gameboy.upperByte

@ExperimentalUnsignedTypes
data class State(
        var a: UByte? = null,
        var b: UByte? = null,
        var c: UByte? = null,
        var d: UByte? = null,
        var e: UByte? = null,
        var h: UByte? = null,
        var l: UByte? = null,
        var hl: UShort? = null,
        var f: UByte? = null,
        var pc: UShort? = null,
        var sp: UShort? = null,
        var m: UByte? = null,
        var t: UByte? = null,
        var args: List<UByte> = listOf(),
        var ram: Map<UInt, UInt> = mapOf()
) {
    init {
        hl?.let {
            assert(h == null && l == null) { "Set HL xor (H or L), but not both." }
            h = it.lowerByte
            l = it.upperByte
        }
        ram.forEach { address, byte ->
            assert(address <= 0xFFFFu) { "RAM address too big" } // TODO: choose lower min, to fit actual ram limits
            assert(byte <= 0xFFu) { "ram contains bytes. data is too big" }
        }
    }

    override fun toString(): String {
        var flags = ""
        f?.let {
            flags = if (it and Registers.ZERO_FLAG != 0u.toUByte()) "Z" else ""
            flags += if (it and Registers.SUBTRACT_FLAG != 0u.toUByte()) "N" else ""
            flags += if (it and Registers.HALF_CARRY_FLAG != 0u.toUByte()) "H" else ""
            flags += if (it and Registers.CARRY_FLAG != 0u.toUByte()) "C" else ""
        }
        var state = ""
        a?.let { state += "a=${it.hex} " }
        b?.let { state += "b=${it.hex} " }
        c?.let { state += "c=${it.hex} " }
        d?.let { state += "d=${it.hex} " }
        e?.let { state += "e=${it.hex} " }
        h?.let { state += "h=${it.hex} " }
        l?.let { state += "l=${it.hex} " }
        pc?.let { state += "pc=${it.hex} " }
        sp?.let { state += "sp=${it.hex} " }
        m?.let { state += "m=${it.hex} " }
        t?.let { state += "t=${it.hex} " }
        if (ram.isNotEmpty()) state += "ram=$ram "
        return "State(${state.trim()}, $flags)"
    }
}