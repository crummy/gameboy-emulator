package com.malcolmcrum.gameboy

@kotlin.ExperimentalUnsignedTypes
class Z80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()

    // A = A + E
    fun ADDr_e() {
        with(registers) {
            val result = a + e
            f = 0u
            if (result > 255u) setOverflowFlag()
            if (result == 0u) setZeroFlag()
            a = result.toUByte()
            tick()
        }
    }

    // Compare A and B, set flags
    fun CPr_b() {
        with(registers) {
            val result = a - b
            setSubtractionFlag()
            if (b > a) setOverflowFlag() // underflow, really
            if (result == 0u) setZeroFlag()
            tick()
        }
    }

    // Push B and C onto stack
    fun PUSHBC() {
        with(registers) {
            sp--
            mmu.wb(sp, b)
            sp--
            mmu.wb(sp, c)
            tick(3)
        }
    }

    // Pop H and L off stack
    fun POPHL() {
        with(registers) {
            l = mmu.rb(sp)
            sp++
            h = mmu.rb(sp)
            tick(3)
        }
    }

    // Read a byte into A
    fun LDAmm() {
        with(registers) {
            val address = mmu.rw(pc)
            pc = (pc + 2u).toUShort()
            a = mmu.rb(address)
            tick(4)
        }
    }

    fun NOP() {
        registers.tick()
    }

    fun STOP() {
        TODO()
    }

    fun reset() {
        with(registers) {
            a = 0u
            b = 0u
            c = 0u
            d = 0u
            e = 0u
            h = 0u
            l = 0u
            f = 0u
            pc = 0u
            sp = 0u
        }
        with(clock) {
            t = 0u
            m = 0u
        }
    }

    fun execute(operation: UByte) {
        when (operation.toUInt()) {
            0x00u -> NOP()
            0x10u -> STOP()
            0xceu -> TODO("adcA(\$xx)")
            0x8eu -> adcA(mmu.rb(registers.hl))
            0x8fu -> adcA(registers.a)
            0x88u -> adcA(registers.b)
            0x89u -> adcA(registers.c)
            0x8au -> adcA(registers.d)
            0x8bu -> adcA(registers.e)
            0x8cu -> adcA(registers.h)
            0x8du -> adcA(registers.l)
            0xc6u -> TODO("addA(\$xx)")
            0x86u -> addA(mmu.rb(registers.hl))
            0x87u -> addA(registers.a)
            0x80u -> addA(registers.b)
            0x81u -> addA(registers.c)
            0x82u -> addA(registers.d)
            0x83u -> addA(registers.e)
            0x84u -> addA(registers.h)
            0x85u -> addA(registers.l)
            0x09u -> addHL(registers.bc)
            0x19u -> addHL(registers.de)
            0x29u -> addHL(registers.hl)
            0x39u -> addHL(registers.sp)
            0xe8u -> TODO()
            0xe6u -> TODO()
            0xa6u -> andA(mmu.rb(registers.hl))
            0xa7u -> andA(registers.a)
            0xa0u -> andA(registers.b)
            0xa1u -> andA(registers.c)
            0xa2u -> andA(registers.d)
            0xa3u -> andA(registers.e)
            0xa4u -> andA(registers.h)
            0xa5u -> andA(registers.l)
            0xcbu -> TODO()
            0xcdu -> TODO()
            0xdcu -> TODO()
            0xd4u -> TODO()
            0xc4u -> TODO()
            0xccu -> TODO()
            0x3fu -> ccf()
            0xfeu -> TODO()
            0xbeu -> cp(mmu.rb(registers.hl))
            0xbfu -> cp(registers.a)
            0xb8u -> cp(registers.b)
            0xb9u -> cp(registers.c)
            0xbau -> cp(registers.d)
            0xbbu -> cp(registers.e)
            0xbcu -> cp(registers.h)
            0xbdu -> cp(registers.l)
            0x2fu -> cpl()
            0x27u -> daa()
//            0x2 -> LDBCmA()
//            0x3 -> INCBC()
//            0x4 -> INCr_b()
            else -> TODO()
        }
    }

    // Converts A into packed BCD (e.g. 0x0B -> 0x1 in upper nibble and 0x2 in lower nibble)
    private fun daa() {
        with(registers) {
            val lowerNibble = (a.toInt() % 10).toUByte()
            val upperNibble = (a.toUInt() shr 4).toUByte()
            a = createUByte(upperNibble, lowerNibble)
        }
    }

    // Flip bits in A
    private fun cpl() {
        with(registers) {
            a = a xor 0xffu
            f = 0u
            if (a == 0u.toUByte()) zero = 1u
        }
    }

    // Fake subtraction - doesn't store result, but does set flags
    private fun cp(byte: UByte) {
        with(registers) {
            val result = a - byte
            f = 0u
            if (result > 255u) carry = 1u
            if (result == 0u) zero = 1u
            tick()
        }
    }

    // Flip carry flag
    private fun ccf() {
        registers.carry = if (registers.carry == 0u.toUByte()) 1u else 0u
    }

    private fun andA(byte: UByte) {
        with(registers) {
            val a = a and byte
            if (a == 0u.toUByte()) zero = 1u
            tick()
        }
    }

    private fun adcA(byte: UByte) {
        with(registers) {
            val result = a + byte + registers.carry
            f = 0u
            if (result > 255u) carry = 1u
            if (result == 0u) zero = 1u
            a = result.toUByte()
            tick()
        }
    }

    private fun addHL(short: UShort) {
        with(registers) {
            val result = hl + short
            f = 0u
            if (result > 255u) setOverflowFlag()
            if (result == 0u) setZeroFlag()
            hl = result.toUShort()
            tick()
        }
    }

    private fun addA(byte: UByte) {
        with(registers) {
            val result = a + byte
            f = 0u
            if (result > 255u) setOverflowFlag()
            if (result == 0u) setZeroFlag()
            a = result.toUByte()
            tick()
        }
    }

}

@kotlin.ExperimentalUnsignedTypes
data class Clock(
        var m: UShort = 0u,
        var t: UShort = 0u
) {
    fun add(m: UByte, t: UByte) {
        this.m = (this.m + m).toUShort()
        this.t = (this.t + t).toUShort()
    }
}

// Remember: Z80 is small endian
@kotlin.ExperimentalUnsignedTypes
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

    var bc: UShort
        get() {
            return ((c.toUShort() * 256u) + b).toUShort()
        }
        set(new) {
            b = new.upperByte()
            c = new.lowerByte()
        }
    var de: UShort
        get() {
            return ((e.toUShort() * 256u) + d).toUShort()
        }
        set(new) {
            d = new.upperByte()
            e = new.lowerByte()
        }
    var hl: UShort
        get() {
            return ((l.toUShort() * 256u) + h).toUShort()
        }
        set(new) {
            h = new.upperByte()
            l = new.lowerByte()
        }
    var carry: UByte
        get() {
            return if (f or 0x10u == 0u.toUByte()) 0u else 1u
        }
        set(new) {
            when (new.toUInt()) {
                0u -> f = (f xor 0x10u)
                1u -> f = (f or 0x10u)
                else -> IllegalStateException()
            }
        }
    var zero: UByte
        get() {
            return if (f or 0x80u == 0x08u.toUByte()) 0u else 1u
        }
        set(new) {
            when (new.toUInt()) {
                0u -> f = (f xor 0x80u)
                1u -> f = (f or 0x80u)
                else -> IllegalStateException()
            }
        }

    // Set if last operation resulted in 0
    fun setZeroFlag() {
        f = (f or 0x80u).toUByte()
    }

    // Set if last operation overflowed past 255
    fun setOverflowFlag() {
        f = (f or 0x10u).toUByte()
    }

    // Set if last operation overflowed past 15
    fun setHalfOverflowFlag() {
        f = (f or 0x20u).toUByte()
    }

    // Set if last operation was a subtraction
    fun setSubtractionFlag() {
        f = (f or 0x40u).toUByte()
    }

    fun tick(times: Int = 1) {
        repeat(times) {
            m = 1u
            t = 4u
        }
    }
}
