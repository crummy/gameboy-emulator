package com.malcolmcrum.gameboy.util

@ExperimentalUnsignedTypes
val UShort.upperByte
    get() = (this shr 8u).toUByte()

@ExperimentalUnsignedTypes
val UShort.lowerByte
    get() = this.toUByte()

@ExperimentalUnsignedTypes
fun createUShort(upperByte: UByte, lowerByte: UByte): UShort {
    return ((upperByte.toUShort() shl 8u) + lowerByte).toUShort()
}

@ExperimentalUnsignedTypes
infix fun UShort.shr(b: UShort) = (toInt() ushr b.toInt()).toUShort()

@ExperimentalUnsignedTypes
infix fun UShort.shl(b: UShort) = (toInt() shl b.toInt()).toUShort()

// from https://discuss.kotlinlang.org/t/recover-most-and-least-significant-bits-in-a-byte/11250/5
@ExperimentalUnsignedTypes
val UByte.upperNibble
    get() = (this.toUInt() shr 4).toUByte()

@ExperimentalUnsignedTypes
val UByte.lowerNibble
    get() = (this.toUInt() and 0x0Fu).toUByte()

@ExperimentalUnsignedTypes
fun createUByte(upperNibble: UByte, lowerNibble: UByte): UByte {
    assert(lowerNibble <= 0xFu) { "${lowerNibble.hex()} is bigger than 0xF" }
    assert(upperNibble <= 0xFu) { "${upperNibble.hex()} is bigger than 0xF" }
    return ((upperNibble.toUInt() shl 4) + lowerNibble).toUByte()
}

@ExperimentalUnsignedTypes
fun UByte.hex(): String =  String.format("0x%02x", this.toInt())

@ExperimentalUnsignedTypes
fun UShort.hex(): String =  String.format("0x%04x", this.toInt())

@ExperimentalUnsignedTypes
fun UInt.hex(): String = String.format("0x%04x", this.toInt())

@ExperimentalUnsignedTypes
fun Map<UInt, UInt>.hex(): String = this.map { "${it.key.hex()}=${it.value.hex()}" }.toString()

@ExperimentalUnsignedTypes
@JvmName("mapIntByteHex")
fun Map<UInt, UByte>.hex(): String = this.map { "${it.key.hex()}=${it.value.hex()}" }.toString()

@ExperimentalUnsignedTypes
fun List<UByte>.hex(): String = this.map { "${it.hex()}}" }.toString()

@ExperimentalUnsignedTypes
fun UByte.getBit(position: Int): Boolean {
    assert(position <= 7)
    return (this.toUInt() shr position) and 0x01u == 1u
}
