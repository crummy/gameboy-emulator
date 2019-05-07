package com.malcolmcrum.gameboy.utils

import assertk.Assert
import assertk.assertions.support.expected
import com.malcolmcrum.gameboy.MMU
import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.hex

@ExperimentalUnsignedTypes
fun Assert<UByte>.isEqualToByte(expected: UByte) = given { actual ->
    val match = actual == expected.toUByte()
    if (match) return
    expected("byte does not match.", expected.hex(), actual.hex())
}

@ExperimentalUnsignedTypes
fun Assert<Registers>.isEqualTo(expected: State) = given { actual ->
    val match =  expected.a?.equals(actual.a) ?: true &&
            expected.b?.equals(actual.b) ?: true &&
            expected.c?.equals(actual.c) ?: true &&
            expected.d?.equals(actual.d) ?: true &&
            expected.e?.equals(actual.e) ?: true &&
            expected.h?.equals(actual.h) ?: true &&
            expected.l?.equals(actual.l) ?: true &&
            expected.f?.equals(actual.f) ?: true &&
            expected.pc?.equals(actual.pc) ?: true &&
            expected.sp?.equals(actual.sp) ?: true &&
            expected.m?.equals(actual.m) ?: true &&
            expected.t?.equals(actual.t) ?: true
    if (match) return
    expected("registers do not match.", expected, actual)
}

@ExperimentalUnsignedTypes
fun Assert<MMU>.isEqualTo(expected: Map<UInt, UInt>) = given { mmu ->
    val actual = expected.keys.map { Pair(it, mmu[it]) }.toMap()
    val match = expected.all { (address, value) -> actual[address] == value.toUByte() }
    if (match) return
    expected("memory does not match.", expected.hex(), actual.hex())
}