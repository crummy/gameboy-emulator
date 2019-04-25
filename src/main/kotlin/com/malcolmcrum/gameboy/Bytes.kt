package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
fun UShort.upperByte(): UByte {
    return (this shr 8u).toUByte()
}

@ExperimentalUnsignedTypes
fun UShort.lowerByte(): UByte {
    return this.toUByte()
}

@ExperimentalUnsignedTypes
infix fun UShort.shr(b: UShort) = (toInt() ushr b.toInt()).toUShort()