package com.malcolmcrum.gameboy

import com.malcolmcrum.gameboy.emulator.MMU
import com.malcolmcrum.gameboy.emulator.Operations
import com.malcolmcrum.gameboy.ui.InstructionView
import com.malcolmcrum.gameboy.util.createUShort
import com.malcolmcrum.gameboy.util.hex

@ExperimentalUnsignedTypes
fun parseInstructions(mmu: MMU, operations: Operations): Map<UShort, Instruction> {
    val instructions: MutableMap<Int, Instruction> = HashMap()
    val rom = mmu.rom
    var address = 0
    while (address < rom.size - 2) {
        InstructionView.log.info("Loading address $address from rom of size ${rom.size}")
        val opcode = rom[address]
        val operation = operations[opcode.toInt()]
        val name = operation.mnemonic.replace("\$aabb", createUShort(rom[address + 2], rom[address + 1]).hex())
                .replace("\$xx", rom[address + 1].hex())
        instructions[address] = Instruction(opcode, operation.mnemonic, name)

        address += operation.instructionBytes
    }
    return instructions.map { Pair(it.key.toUShort(), it.value) }.toMap()
}

@ExperimentalUnsignedTypes
data class Instruction(val opCode: UByte, val name: String, val expandedName: String)