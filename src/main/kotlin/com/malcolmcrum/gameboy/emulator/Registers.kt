package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.createUShort
import com.malcolmcrum.gameboy.util.hex
import com.malcolmcrum.gameboy.util.lowerByte
import com.malcolmcrum.gameboy.util.upperByte

// Remember: GBZ80 is small endian
@ExperimentalUnsignedTypes
data class Registers(
        var a: UByte = 0u,
        var b: UByte = 0u,
        var c: UByte = 0u,
        var d: UByte = 0u,
        var e: UByte = 0u,
        var h: UByte = 0u,
        var l: UByte = 0u,
        var pc: UShort = 0u,
        var sp: UShort = 0u,
        var m: UByte = 0u,
        var t: UByte = 0u,
        var stopped: Boolean = false,
        var halted: Boolean = false
) {
    var f: UByte = 0u
        set(value) {
            field = value and 0xf0u // lower 4 bytes of f are always 0
        }

    var af: UShort
        get() = createUShort(a, f)
        set(value) {
            a = value.upperByte
            f = value.lowerByte
        }
    var bc: UShort
        get() = createUShort(b, c)
        set(value) {
            b = value.upperByte
            c = value.lowerByte
        }
    var de: UShort
        get() = createUShort(d, e)
        set(value) {
            d = value.upperByte
            e = value.lowerByte
        }
    var hl: UShort
        get() = createUShort(h, l)
        set(value) {
            h = value.upperByte
            l = value.lowerByte
        }

    var carry: Boolean
        get() = f and CARRY_FLAG != 0u.toUByte()
        set(new) {
            f = when (new) {
                false -> (f and CARRY_FLAG.inv())
                true -> (f or CARRY_FLAG)
            }
        }

    var halfCarry: Boolean
        get() = f and HALF_CARRY_FLAG != 0u.toUByte()
        set(new) {
            f = when (new) {
                false -> (f and HALF_CARRY_FLAG.inv())
                true -> (f or HALF_CARRY_FLAG)
            }
        }

    var zero: Boolean
        get() = f and ZERO_FLAG != 0u.toUByte()
        set(value) {
            f = when (value) {
                false -> (f and ZERO_FLAG.inv())
                true -> (f or ZERO_FLAG)
            }
        }

    var subtract: Boolean
        get() = f and SUBTRACT_FLAG != 0u.toUByte()
        set(value) {
            f = when (value) {
                false -> (f and SUBTRACT_FLAG.inv())
                true -> (f or SUBTRACT_FLAG)
            }
        }

    // TODO: should this be in Operations.kt?
    fun tick(times: Int = 1) {
        repeat(times) {
            m = (m + 1u).toUByte()
            t = (t + 4u).toUByte()
        }
    }

    override fun toString(): String {
        var flags = if (zero) "Z" else "-"
        flags += if (subtract) "N" else "-"
        flags += if (halfCarry) "H" else "-"
        flags += if (carry) "C" else "-"
        return "Registers(a=${a.hex()}, b=${b.hex()}, c=${c.hex()}, d=${d.hex()}, e=${e.hex()}, h=${h.hex()}, l=${l.hex()}, sp=${sp.hex()}, pc=${pc.hex()}, $flags)"
    }

    fun reset() {
        a = 0u; b = 0u; c = 0u; d = 0u; e = 0u; h = 0u; l = 0u; f = 0u; sp = 0u; pc = 0u; m = 0u; t = 0u
    }

    companion object {
        const val ZERO_FLAG: UByte = 0x80u
        const val SUBTRACT_FLAG: UByte = 0x40u
        const val HALF_CARRY_FLAG: UByte = 0x20u
        const val CARRY_FLAG: UByte = 0x10u
    }
}