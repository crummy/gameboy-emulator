package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
val UShort.upperByte
    get() = (this shr 8u).toUByte()

@ExperimentalUnsignedTypes
val UShort.lowerByte
    get() = this.toUByte()

@ExperimentalUnsignedTypes
fun createUShort(upperByte: UByte, lowerByte: UByte): UShort {
    return ((upperByte.toUShort() * 256u) + lowerByte).toUShort()
}

@ExperimentalUnsignedTypes
infix fun UShort.shr(b: UShort) = (toInt() ushr b.toInt()).toUShort()

// from https://discuss.kotlinlang.org/t/recover-most-and-least-significant-bits-in-a-byte/11250/5
@ExperimentalUnsignedTypes
val UByte.upperNibble
    get() = (this.toInt() shr 4 and 0xFF).toUByte()

@ExperimentalUnsignedTypes
val UByte.lowerNibble
    get() = (this.toInt() and 0xFF).toUByte()

@ExperimentalUnsignedTypes
fun createUByte(upperNibble: UByte, lowerNibble: UByte): UByte {
    assert(lowerNibble <= 0xFFu)
    assert(upperNibble <= 0xFFu)
    return ((upperNibble.toUInt() shl 4) + lowerNibble).toUByte()
}
