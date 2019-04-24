package com.malcolmcrum.gameboy

@kotlin.ExperimentalUnsignedTypes
class Z80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()

    // A = A + E
    fun ADDr_e() {
        with (registers) {
            val result = a + e
            f = 0u
            if (result > 255u) setOverflowFlag()
            if (result == 0u) setZeroFlag()
            a = result.toUByte()
        }
        clock.tick()
    }

    // Compare A and B, set flags
    fun CPr_b() {
        with (registers) {
            val result = a - b
            setSubtractionFlag()
            if (b > a) setOverflowFlag() // underflow, really
            if (result == 0u) setZeroFlag()
        }
        clock.tick()
    }

    // Push B and C onto stack
    fun PUSHBC() {
        with (registers) {
            sp--
            mmu.wb(sp, b)
            sp--
            mmu.wb(sp, c)
        }
        clock.tick(3)
    }

    // Pop H and L off stack
    fun POPHL() {
        with (registers) {
            l = mmu.rb(sp)
            sp++
            h = mmu.rb(sp)
        }
        clock.tick(3)
    }

    // Read a byte into A
    fun LDAmm() {
        with (registers) {
            val address = mmu.rw(pc)
            pc = (pc + 2u).toUShort()
            a = mmu.rb(address)
        }
        clock.tick(4)
    }

    fun NOP() {
        clock.tick()
    }

    fun reset() {
        with (registers) {
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
        with (clock) {
            t = 0u
            m = 0u
        }
    }

}

@kotlin.ExperimentalUnsignedTypes
data class Clock(
        var m: UByte = 0u,
        var t: UByte = 0u
) {
    fun tick(times: Int = 1) {
        repeat(times) {
            m = 1u
            t = 4u
        }
    }
}

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
        var sp: UShort = 0u
) {
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
}