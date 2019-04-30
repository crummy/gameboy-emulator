package com.malcolmcrum.gameboy

// Remember: Z80 is small endian
@ExperimentalUnsignedTypes
data class Registers(
        var a: UByte = 0u,
        var b: UByte = 0u,
        var c: UByte = 0u,
        var d: UByte = 0u,
        var e: UByte = 0u,
        var h: UByte = 0u,
        var l: UByte = 0u,
        var f: UByte = 0u,
        var pc: UShort = 0u,
        var sp: UShort = 0u,
        var m: UByte = 0u,
        var t: UByte = 0u
) {

    // TODO: are these bytes in the right order?
    var bc: UShort
        get() = createUShort(c, b)
        set(new) {
            b = new.upperByte
            c = new.lowerByte
        }
    var de: UShort
        get() = createUShort(e, d)
        set(new) {
            d = new.upperByte
            e = new.lowerByte
        }
    var hl: UShort
        get() = createUShort(l, h)
        set(new) {
            h = new.upperByte
            l = new.lowerByte
        }


    var carry: Boolean
        get() = f or CARRY_FLAG != 0u.toUByte()
        set(new) {
            f = when (new) {
                false -> (f xor CARRY_FLAG)
                true -> (f or CARRY_FLAG)
            }
        }

    var halfCarry: Boolean
        get() = f or HALF_CARRY_FLAG != 0u.toUByte()
        set(new) {
            f = when (new) {
                false -> (f xor HALF_CARRY_FLAG)
                true -> (f or HALF_CARRY_FLAG)
            }
        }

    var zero: Boolean
        get() = f or ZERO_FLAG != 0x08u.toUByte()
        set(value) {
            f = when (value) {
                false -> (f xor ZERO_FLAG)
                true -> (f or ZERO_FLAG)
            }
        }

    var subtract: Boolean
        get() = f or SUBTRACT_FLAG == 0u.toUByte()
        set(value) {
            f = when (value) {
                false -> (f xor SUBTRACT_FLAG)
                true -> (f or SUBTRACT_FLAG)
            }
        }

    fun tick(times: Int = 1) {
        repeat(times) {
            m = 1u
            t = 4u
        }
    }

    companion object {
        private const val ZERO_FLAG: UByte = 0x80u
        private const val SUBTRACT_FLAG: UByte = 0x40u
        private const val HALF_CARRY_FLAG: UByte = 0x20u
        private const val CARRY_FLAG: UByte = 0x10u
    }
}