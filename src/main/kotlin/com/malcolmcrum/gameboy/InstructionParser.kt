package com.malcolmcrum.gameboy

import com.malcolmcrum.gameboy.emulator.MMU
import com.malcolmcrum.gameboy.emulator.Operations
import com.malcolmcrum.gameboy.util.createUShort
import com.malcolmcrum.gameboy.util.hex

@ExperimentalUnsignedTypes
fun parseInstructions(mmu: MMU, operations: Operations): Map<UShort, Instruction> {
    val instructions: MutableMap<Int, Instruction> = HashMap()
    var address = 0
    while (address < mmu.rom.size - 2) {
        val opcode = mmu[address.toUInt()]
        val operation = operations[address.toUShort()]
        val name = parseInstruction(operation.mnemonic, mmu, address)
        instructions[address] = Instruction(opcode, operation.mnemonic, name)

        address += operation.instructionBytes
    }
    return instructions.map { Pair(it.key.toUShort(), it.value) }.toMap()
}

@ExperimentalUnsignedTypes
fun parseInstruction(operation: String, mmu: MMU, address: Int): String {
    val rom = mmu.rom
    return operation
            .replace("n16", createUShort(rom[address + 2], rom[address + 1]).hex())
            .replace("n8", rom[address + 1].hex())
            .replace("e8", toSigned(rom[address + 1].toInt()))
}

@ExperimentalUnsignedTypes
fun toSigned(int: Int): String {
    return "${int.toUByte().hex()} (${int.toByte()})"
}

@ExperimentalUnsignedTypes
data class Instruction(val opCode: UByte, val name: String, val expandedName: String)