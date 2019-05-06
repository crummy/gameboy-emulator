package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class Z80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()
    val interruptsEnabled = false
    val operations = OperationBuilder(registers, mmu) { interruptsEnabled }.operations

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


    fun execute() {
        val opCode = mmu[registers.pc].toInt()

        val operation = operations[opCode]
        operation.operation.invoke()
    }

}

