package com.malcolmcrum.gameboy.utils

import com.malcolmcrum.gameboy.emulator.Registers
import com.malcolmcrum.gameboy.util.hex
import com.malcolmcrum.gameboy.util.lowerByte
import com.malcolmcrum.gameboy.util.upperByte

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
        var bc: UShort? = null,
        var af: UShort? = null,
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
            h = it.upperByte
            l = it.lowerByte
        }
        bc?.let {
            assert(b == null && c == null) { "Set BC xor (B or C), but not both." }
            b = it.upperByte
            c = it.lowerByte
        }
        af?.let {
            assert(a == null && f == null) { "Set AF xor (A or F), but not both." }
            a = it.upperByte
            f = it.lowerByte
        }
        ram.forEach { (address, byte) ->
            assert(address <= 0xFFFFu) { "RAM address ${address.hex()} too big" } // TODO: choose lower min, to fit actual ram limits
            assert(byte <= 0xFFu) { "ram contains bytes; ${byte.hex()} is too big" }
        }
    }

    override fun toString(): String {
        val components = mutableListOf<String>()
        a?.let { components.add("a=${it.hex()}") }
        b?.let { components.add("b=${it.hex()}") }
        c?.let { components.add("c=${it.hex()}") }
        d?.let { components.add("d=${it.hex()}") }
        e?.let { components.add("e=${it.hex()}") }
        h?.let { components.add("h=${it.hex()}") }
        l?.let { components.add("l=${it.hex()}") }
        pc?.let { components.add("pc=${it.hex()}") }
        sp?.let { components.add("sp=${it.hex()}") }
        m?.let { components.add("m=${it.hex()}") }
        t?.let { components.add("t=${it.hex()}") }
        if (ram.isNotEmpty()) components.add("ram=${ram.hex()}")
        if (args.isNotEmpty()) components.add("args=${args.hex()}")
        f?.let {
            var flags = if (it and Registers.ZERO_FLAG != 0u.toUByte()) "Z" else ""
            flags += if (it and Registers.SUBTRACT_FLAG != 0u.toUByte()) "N" else ""
            flags += if (it and Registers.HALF_CARRY_FLAG != 0u.toUByte()) "H" else ""
            flags += if (it and Registers.CARRY_FLAG != 0u.toUByte()) "C" else ""
            if (flags.isNotEmpty()) components.add(flags)
        }
        return "State(${components.joinToString()})"
    }
}