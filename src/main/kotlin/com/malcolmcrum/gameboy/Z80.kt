package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class Z80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()

    // A = A + E
    fun ADDr_e() {
        with(registers) {
            val result = a + e
            f = 0u
            if (result > 255u) carry = true
            if (result == 0u) zero = true
            a = result.toUByte()
            tick()
        }
    }

    // Compare A and B, set flags
    fun CPr_b() {
        with(registers) {
            val result = a - b
            subtract = true
            if (b > a) carry = true // underflow, really
            if (result == 0u) zero = true
            tick()
        }
    }

    // Push B and C onto stack
    fun PUSHBC() {
        with(registers) {
            sp--
            mmu[sp] = b
            sp--
            mmu[sp] = c
            tick(3)
        }
    }

    // Pop H and L off stack
    fun POPHL() {
        with(registers) {
            l = mmu[sp]
            sp++
            h = mmu[sp]
            tick(3)
        }
    }

    // Read a byte into A
    fun LDAmm() {
        with(registers) {
            val address = mmu.getWord(pc)
            pc = (pc + 2u).toUShort()
            a = mmu[address]
            tick(4)
        }
    }

    fun NOP() {
        registers.tick()
    }

    fun STOP() {
        TODO()
    }

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
        val operation = mmu.getWord(registers.pc)

        when (operation.upperByte.toUInt()) {
            0x00u -> NOP()
            0x10u -> STOP()
            0xceu -> TODO("adcA(\$xx)")
            0x8eu -> adcA(mmu[registers.hl])
            0x8fu -> adcA(registers.a)
            0x88u -> adcA(registers.b)
            0x89u -> adcA(registers.c)
            0x8au -> adcA(registers.d)
            0x8bu -> adcA(registers.e)
            0x8cu -> adcA(registers.h)
            0x8du -> adcA(registers.l)
            0xc6u -> TODO("addA(\$xx)")
            0x86u -> addA(mmu[registers.hl])
            0x87u -> addA(registers.a)
            0x80u -> addA(registers.b)
            0x81u -> addA(registers.c)
            0x82u -> addA(registers.d)
            0x83u -> addA(registers.e)
            0x84u -> addA(registers.h)
            0x85u -> addA(registers.l)
            0x09u -> addHL(registers.bc)
            0x19u -> addHL(registers.de)
            0x29u -> addHL(registers.hl)
            0x39u -> addHL(registers.sp)
            0xe8u -> TODO()
            0xe6u -> TODO()
            0xa6u -> andA(mmu[registers.hl])
            0xa7u -> andA(registers.a)
            0xa0u -> andA(registers.b)
            0xa1u -> andA(registers.c)
            0xa2u -> andA(registers.d)
            0xa3u -> andA(registers.e)
            0xa4u -> andA(registers.h)
            0xa5u -> andA(registers.l)
            0xcbu -> TODO()
            0xcdu -> TODO()
            0xdcu -> TODO()
            0xd4u -> TODO()
            0xc4u -> TODO()
            0xccu -> TODO()
            0x3fu -> ccf()
            0xfeu -> TODO()
            0xbeu -> cp(mmu[registers.hl])
            0xbfu -> cp(registers.a)
            0xb8u -> cp(registers.b)
            0xb9u -> cp(registers.c)
            0xbau -> cp(registers.d)
            0xbbu -> cp(registers.e)
            0xbcu -> cp(registers.h)
            0xbdu -> cp(registers.l)
            0x2fu -> cpl()
            0x27u -> daa()
            0x35u -> mmu[registers.hl] = dec(mmu[registers.hl])
            0x3du -> registers.a = dec(registers.a)
            0x05u -> registers.b = dec(registers.b)
            0x0bu -> registers.bc = dec(registers.bc)
            0x0du -> registers.c = dec(registers.c)
            0x15u -> registers.d = dec(registers.d)
            0x1bu -> registers.de = dec(registers.de)
            0x25u -> registers.h = dec(registers.h)
            0x2bu -> registers.hl = dec(registers.hl)
            0x2du -> registers.l = dec(registers.l)
            0x3bu -> registers.sp = dec(registers.sp)
            0xf3u -> di()
            0xfbu -> ei()
            0x76u -> halt()
            0x34u -> mmu[registers.hl] = inc(mmu[registers.hl])
            0x3cu -> registers.a = inc(registers.a)
            0x04u -> registers.b = inc(registers.b)
            0x03u -> registers.bc = inc(registers.bc)
            0x0cu -> registers.c = inc(registers.c)
            0x14u -> registers.d = inc(registers.d)
            0x13u -> registers.de = inc(registers.de)
            0x1cu -> registers.e = inc(registers.e)
            0x24u -> registers.h = inc(registers.h)
            0x23u -> registers.hl = inc(registers.hl)
            0x2cu -> registers.l = inc(registers.l)
            0x33u -> registers.sp = inc(registers.sp)
            0xc3u -> jp()
            0xe9u -> jp(registers.hl)
            0xdau -> if (registers.carry) jp()
            0xd2u -> if (!registers.carry) jp()
            0xc2u -> if (!registers.zero) jp()
            0xcau -> if (registers.zero) jp()
            0x18u -> jr(operation.lowerByte)
            0x38u -> if (registers.carry) jr(operation.lowerByte)
            0x30u -> if (!registers.carry) jr(operation.lowerByte)
            0x20u -> if (!registers.zero) jr(operation.lowerByte)
            0x28u -> if (registers.zero) jr(operation.lowerByte)
            0xeau -> load(storeInMemory(readWordFromMemory(registers.pc)), readFromRegister(registers.a))
            0x08u -> load(storeWordInMemory(registers.sp), readWordFromMemory(registers.pc))
            0xe0u -> load(storeInMemory(readFromMemory(registers.sp).invoke()), readFromRegister(registers.a))
            0x02u -> load(storeInMemory(registers.bc), readFromRegister(registers.a))
            0xe2u -> load(storeInMemory(registers.c), readFromRegister(registers.a))
            0x12u -> load(storeInMemory(registers.de), readFromRegister(registers.a))
            0x36u -> load(storeInMemory(registers.hl), readFromMemory(registers.pc))

            else -> TODO()
        }
    }

    private fun readFromRegister(value: UByte): () -> UByte = { value }

    private fun readFromMemory(address: UShort): () -> UByte = {
        registers.tick()
        mmu[address]
    }

    private fun readWordFromMemory(absolute: UShort): () -> UShort = {
        val upperByte = mmu[absolute + 1u]
        registers.tick()
        val lowerByte = mmu[absolute]
        registers.tick()
        createUShort(upperByte, lowerByte)
    }

    private fun storeInMemory(address: UShort) = { value: UByte ->
        mmu[address] = value
        registers.tick()
    }

    private fun storeInMemory(indirectAddress: () -> UShort): (UByte) -> Unit {
        return storeInMemory(indirectAddress.invoke())
    }

    private fun storeInMemory(absolute: UByte) = { value: UByte ->
        mmu[0xFF00u + absolute] = value
        registers.tick()
    }

    private fun storeWordInMemory(word: UShort): (UShort) -> Unit = { value ->
        mmu[word] = value.lowerByte
        registers.tick()
        mmu[word + 1u] = value.upperByte
        registers.tick()
    }

    @JvmName("load16")
    private fun load(save: (UShort) -> Unit, load: () -> UShort) {
        val short = load.invoke()
        save.invoke(short)
    }

    private fun load(save: (UByte) -> Unit, load: () -> UByte) {
        val byte = load.invoke()
        save.invoke(byte)
    }

    private fun jr(offset: UByte) {
        registers.pc = (registers.pc + offset).toUShort()
    }

    private fun jp(short: UShort) {
        registers.pc = short
    }


    private fun jp() {
        with(registers) {
            val lowerByte = mmu[pc]
            val upperByte = mmu[pc + 1u]
            pc = createUShort(upperByte, lowerByte)
        }

    }

    private fun inc(byte: UByte): UByte {
        with(registers) {
            val result = byte + 1u
            f = 0u
            if (result == 0u) zero = true
            if (result > 0xFFu) carry = true
            return result.toUByte()
        }
    }

    private fun inc(short: UShort): UShort {
        with(registers) {
            val result = short + 1u
            f = 0u
            if (result == 0u) zero = true
            if (result > 0xFFu) carry = true
            return result.toUShort()
        }
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

    private fun dec(byte: UByte): UByte {
        with(registers) {
            val result = byte - 1u
            f = 0u
            if (result == 0u) zero = true
            if (result < 0xFFu) carry = true
            return result.toUByte()
        }
    }

    private fun dec(short: UShort): UShort {
        with(registers) {
            val result = short - 1u
            if (result == 0u) zero = false
            if (result < 0xFFFFu) carry = true
            return result.toUShort()
        }
    }

    // Converts A into packed BCD (e.g. 0x0B -> 0x1 in upper nibble and 0x2 in lower nibble)
    private fun daa() {
        with(registers) {
            val lowerNibble = (a.toInt() % 10).toUByte()
            val upperNibble = (a.toUInt() shr 4).toUByte()
            a = createUByte(upperNibble, lowerNibble)
        }
    }

    // Flip bits in A
    private fun cpl() {
        with(registers) {
            a = a xor 0xffu
            f = 0u
            if (a == 0u.toUByte()) zero = true
        }
    }

    // Fake subtraction - doesn't store result, but does set flags
    private fun cp(byte: UByte) {
        with(registers) {
            val result = a - byte
            f = 0u
            if (result > 255u) carry = true
            if (result == 0u) zero = true
            tick()
        }
    }

    // Flip carry flag
    private fun ccf() {
        registers.carry = !registers.carry
    }

    private fun andA(byte: UByte) {
        with(registers) {
            val a = a and byte
            if (a == 0u.toUByte()) zero = true
            tick()
        }
    }

    private fun adcA(byte: UByte) {
        with(registers) {
            val result = a + byte + if (registers.carry) 1u else 0u
            f = 0u
            if (result > 255u) carry = true
            if (result == 0u) zero = true
            a = result.toUByte()
            tick()
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

