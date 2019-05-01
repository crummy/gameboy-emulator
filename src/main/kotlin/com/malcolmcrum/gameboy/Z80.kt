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

    fun NOP() {
        registers.tick()
    }

    fun STOP() {
        TODO()
    }



    private fun halt() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Enable interrupts
    private fun ei() {
        TODO()
    }

    // Disable interrupts
    private fun di() {
        TODO()
    }

    private fun andA(source: () -> UByte) {
        return andA(source.invoke())
    }

    private fun andA(byte: UByte) {
        with(registers) {
            val a = a and byte
            if (a == 0u.toUByte()) zero = true
            tick()
        }
    }

    private fun adcA(source: () -> UByte) {
        return adcA(source.invoke())
    }

    private fun adcA(byte: UByte) {
        with(registers) {
            val result = a + byte + if (registers.carry) 1u else 0u
            setFlags(result)
            a = result.toUByte()
            tick()
        }
    }

    private fun add(destination: (UShort) -> Unit, left: UShort, right: UShort) {
        with(registers) {
            val result = left + right
            setFlags(result)
            destination.invoke(result.toUShort())
            tick(2)
        }
    }

    private fun addHL(short: UShort) {
        with(registers) {
            val result = hl + short
            f = 0u
            if (result > 255u) carry = true
            if (result == 0u) zero = true
            hl = result.toUShort()
            tick()
        }
    }



    private fun addA(source: () -> UByte) {
        return addA(source.invoke())
    }

    private fun addA(byte: UByte) {
        with(registers) {
            val result = a + byte
            f = 0u
            if (result > 255u) carry = true
            if (result == 0u) zero = true
            a = result.toUByte()
            tick()
        }
    }

}

