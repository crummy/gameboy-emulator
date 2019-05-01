package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class Operation(
        val name: String,
        val instructionBytes: Int,
        val operation: () -> Unit
)