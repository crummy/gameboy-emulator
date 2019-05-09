package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class GBZ80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()
    val interruptsEnabled = false
    val operations = OperationBuilder(registers, mmu) { interruptsEnabled }.operations

    fun execute(debug: Boolean = false): Int {
        val opCode = mmu[registers.pc].toInt()
        val operation = operations[opCode]
        if (debug) {
            println(operation.name)
        }
        operation.operation.invoke()
        return operation.instructionBytes
    }

}

