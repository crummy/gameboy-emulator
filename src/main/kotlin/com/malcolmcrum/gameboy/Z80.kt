package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
class Z80 {

    val clock = Clock()
    val registers = Registers()
    val mmu = MMU()
    val operations: Array<Operation> = Array(0xFF) { TODO() }


    init {
        // instructionBytes is 1 (not 2) for all CB operations, because the first byte has already been read
        val cbOperations: Array<Operation> = Array(0xFF) { TODO() }
        createBitOperations(cbOperations, 0x46, 0)
        createBitOperations(cbOperations, 0x4e, 1)
        createBitOperations(cbOperations, 0x55, 2)
        createBitOperations(cbOperations, 0x5e, 3)
        createBitOperations(cbOperations, 0x66, 4)
        createBitOperations(cbOperations, 0x6e, 5)
        createBitOperations(cbOperations, 0x76, 6)
        createBitOperations(cbOperations, 0x7d, 7)
        createResOperations(cbOperations, 0x86, 0)
        createResOperations(cbOperations, 0x8e, 1)
        createResOperations(cbOperations, 0x96, 2)
        createResOperations(cbOperations, 0x9e, 3)
        createResOperations(cbOperations, 0xa6, 4)
        createResOperations(cbOperations, 0xae, 5)
        createResOperations(cbOperations, 0xb6, 6)
        createResOperations(cbOperations, 0xbe, 7)
        cbOperations[0x16] = Operation("RL (HL)", 1) { TODO() }
        cbOperations[0x17] = Operation("RL A", 1) { TODO() }
        cbOperations[0x10] = Operation("RL B", 1) { TODO() }
        cbOperations[0x11] = Operation("RL C", 1) { TODO() }
        cbOperations[0x12] = Operation("RL D", 1) { TODO() }
        cbOperations[0x13] = Operation("RL E", 1) { TODO() }
        cbOperations[0x14] = Operation("RL H", 1) { TODO() }
        cbOperations[0x15] = Operation("RL L", 1) { TODO() }
        cbOperations[0x06] = Operation("RLC (HL)", 1) { TODO() }
        cbOperations[0x07] = Operation("RLC A", 1) { TODO() }
        cbOperations[0x00] = Operation("RLC B", 1) { TODO() }
        cbOperations[0x01] = Operation("RLC C", 1) { TODO() }
        cbOperations[0x02] = Operation("RLC D", 1) { TODO() }
        cbOperations[0x03] = Operation("RLC E", 1) { TODO() }
        cbOperations[0x04] = Operation("RLC H", 1) { TODO() }
        cbOperations[0x05] = Operation("RLC L", 1) { TODO() }
        cbOperations[0x1e] = Operation("RR (HL)", 1) { TODO() }
        cbOperations[0x1f] = Operation("RR A", 1) { TODO() }
        cbOperations[0x18] = Operation("RR B", 1) { TODO() }
        cbOperations[0x19] = Operation("RR C", 1) { TODO() }
        cbOperations[0x1a] = Operation("RR D", 1) { TODO() }
        cbOperations[0x1b] = Operation("RR E", 1) { TODO() }
        cbOperations[0x1c] = Operation("RR H", 1) { TODO() }
        cbOperations[0x1d] = Operation("RR L", 1) { TODO() }
        cbOperations[0x0e] = Operation("RRC (HL)", 1) { TODO() }
        cbOperations[0x0f] = Operation("RRC A", 1) { TODO() }
        cbOperations[0x08] = Operation("RRC B", 1) { TODO() }
        cbOperations[0x09] = Operation("RRC C", 1) { TODO() }
        cbOperations[0x0a] = Operation("RRC D", 1) { TODO() }
        cbOperations[0x0b] = Operation("RRC E", 1) { TODO() }
        cbOperations[0x0c] = Operation("RRC H", 1) { TODO() }
        cbOperations[0x0d] = Operation("RRC L", 1) { TODO() }
        createSetOperations(cbOperations, 0xc6, 0)
        createSetOperations(cbOperations, 0xce, 1)
        createSetOperations(cbOperations, 0xd6, 2)
        createSetOperations(cbOperations, 0xde, 3)
        createSetOperations(cbOperations, 0xe6, 4)
        createSetOperations(cbOperations, 0xee, 5)
        createSetOperations(cbOperations, 0xf6, 6)
        createSetOperations(cbOperations, 0xfe, 7)



        operations[0x00] = Operation("NOP", 1) { NOP() }
        operations[0x10] = Operation("STOP", 1) { TODO() }
        operations[0xce] = Operation("ADC A,\$xx", 2) { adcA(readFromMemory(registers.sp + 1u)) }
        operations[0x8e] = Operation("ADC A,(HL)", 1) { adcA(readFromMemory(registers.hl)) }
        operations[0x8f] = Operation("ADC A,A", 1) { adcA(registers.a) }
        operations[0x88] = Operation("ADC A,B", 1) { adcA(registers.b) }
        operations[0x89] = Operation("ADC A,C", 1) { adcA(registers.c) }
        operations[0x8a] = Operation("ADC A,D", 1) { adcA(registers.d) }
        operations[0x8b] = Operation("ADC A,E", 1) { adcA(registers.e) }
        operations[0x8c] = Operation("ADC A,H", 1) { adcA(registers.h) }
        operations[0x8d] = Operation("ADC A,L", 1) { adcA(registers.l) }
        operations[0xc6] = Operation("ADD A,\$xx", 2) { addA(readFromMemory(registers.sp + 1u)) }
        operations[0x86] = Operation("ADD A,(HL)", 1) { addA(readFromMemory(registers.sp + 1u)) }
        operations[0x87] = Operation("ADD A,A", 1) { addA(registers.a) }
        operations[0x80] = Operation("ADD A,B", 1) { addA(registers.b) }
        operations[0x81] = Operation("ADD A,C", 1) { addA(registers.c) }
        operations[0x82] = Operation("ADD A,D", 1) { addA(registers.d) }
        operations[0x83] = Operation("ADD A,E", 1) { addA(registers.e) }
        operations[0x84] = Operation("ADD A,H", 1) { addA(registers.h) }
        operations[0x85] = Operation("ADD A,L", 1) { addA(registers.l) }
        operations[0x09] = Operation("ADD HL,BC", 1) { addHL(registers.bc) }
        operations[0x19] = Operation("ADD HL,DE", 1) { addHL(registers.de) }
        operations[0x29] = Operation("ADD HL,HL", 1) { addHL(registers.hl) }
        operations[0x39] = Operation("ADD HL,SP", 1) { addHL(registers.sp) }
        operations[0xe8] = Operation("ADD SP,\$xx", 2) { addSP(readFromMemory(registers.sp + 1u)) }
        operations[0xe6] = Operation("AND \$xx", 2) { andA(readFromMemory(registers.sp + 1u)) }
        operations[0xa6] = Operation("AND (HL)", 1) { andA(readFromMemory(registers.hl)) }
        operations[0xa7] = Operation("AND A", 1) { andA(registers.a) }
        operations[0xa0] = Operation("AND B", 1) { andA(registers.b) }
        operations[0xa1] = Operation("AND C", 1) { andA(registers.c) }
        operations[0xa2] = Operation("AND D", 1) { andA(registers.d) }
        operations[0xa3] = Operation("AND E", 1) { andA(registers.e) }
        operations[0xa4] = Operation("AND H", 1) { andA(registers.h) }
        operations[0xa5] = Operation("AND L", 1) { andA(registers.l) }
        operations[0xcb] = Operation("0xCB operations", 2) { TODO("move to next SP, load from cbOperations") }

    }

    private fun createSetOperations(cbOperations: Array<Operation>, startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = Operation("SET $bitIndex,(HL)", 1) { TODO() }
        cbOperations[startIndex + 1] = Operation("SET $bitIndex,A", 1) { TODO() }
        cbOperations[startIndex - 6] = Operation("SET $bitIndex,B", 1) { TODO() }
        cbOperations[startIndex - 5] = Operation("SET $bitIndex,C", 1) { TODO() }
        cbOperations[startIndex - 4] = Operation("SET $bitIndex,D", 1) { TODO() }
        cbOperations[startIndex - 3] = Operation("SET $bitIndex,E", 1) { TODO() }
        cbOperations[startIndex - 2] = Operation("SET $bitIndex,H", 1) { TODO() }
        cbOperations[startIndex - 1] = Operation("SET $bitIndex,L", 1) { TODO() }
    }

    private fun createResOperations(cbOperations: Array<Operation>, startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = Operation("RES $bitIndex,(HL)", 1) { res(bitIndex, storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[startIndex + 1] = Operation("RES $bitIndex,A", 1) { res(bitIndex, storeInRegisterA, { registers.a }) }
        cbOperations[startIndex - 6] = Operation("RES $bitIndex,A", 1) { res(bitIndex, storeInRegisterB, { registers.b }) }
        cbOperations[startIndex - 5] = Operation("RES $bitIndex,B", 1) { res(bitIndex, storeInRegisterC, { registers.c }) }
        cbOperations[startIndex - 4] = Operation("RES $bitIndex,C", 1) { res(bitIndex, storeInRegisterD, { registers.d }) }
        cbOperations[startIndex - 3] = Operation("RES $bitIndex,D", 1) { res(bitIndex, storeInRegisterE, { registers.e }) }
        cbOperations[startIndex - 2] = Operation("RES $bitIndex,H", 1) { res(bitIndex, storeInRegisterH, { registers.h }) }
        cbOperations[startIndex - 1] = Operation("RES $bitIndex,L", 1) { res(bitIndex, storeInRegisterL, { registers.l }) }
    }

    private fun createBitOperations(cbOperations: Array<Operation>, startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = Operation("BIT $bitIndex,(HL)", 1) { bit(bitIndex, readFromMemory(registers.hl)) }
        cbOperations[startIndex + 1] = Operation("BIT $bitIndex,A", 1) { bit(bitIndex, registers.a) }
        cbOperations[startIndex - 6] = Operation("BIT $bitIndex,B", 1) { bit(bitIndex, registers.b) }
        cbOperations[startIndex - 5] = Operation("BIT $bitIndex,C", 1) { bit(bitIndex, registers.c) }
        cbOperations[startIndex - 4] = Operation("BIT $bitIndex,D", 1) { bit(bitIndex, registers.d) }
        cbOperations[startIndex - 3] = Operation("BIT $bitIndex,E", 1) { bit(bitIndex, registers.e) }
        cbOperations[startIndex - 2] = Operation("BIT $bitIndex,H", 1) { bit(bitIndex, registers.h) }
        cbOperations[startIndex - 1] = Operation("BIT $bitIndex,L", 1) { bit(bitIndex, registers.l) }
    }

    private fun res(index: Int, save: (UByte) -> Unit, load: () -> UByte) {
        val mask = (1 shl index).toUByte()
        save.invoke(load.invoke() xor mask)
    }

    private fun bit(index: Int, source: () -> UByte) {
        return bit(index, source.invoke())
    }

    private fun bit(index: Int, source: UByte) {
        val bit = source and (1 shl index).toUByte()
        registers.zero = bit == 0u.toUByte()
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
            0xeau -> load(storeInMemory(readWordFromMemory(registers.pc)), registers.a)
            0x08u -> load(storeWordInMemory(registers.sp), readWordFromMemory(registers.pc))
            0xe0u -> load(storeInMemory(readFromMemory(registers.sp).invoke()), registers.a)
            0x02u -> load(storeInMemory(registers.bc), registers.a)
            0xe2u -> load(storeInMemory(registers.c), registers.a)
            0x12u -> load(storeInMemory(registers.de), registers.a)
            0x36u -> load(storeInMemory(registers.hl), readFromMemory(registers.pc))

            else -> TODO()
        }
    }

    fun NOP() {
        registers.tick()
    }

    fun STOP() {
        TODO()
    }

    private fun readFromMemory(address: UInt): () -> UByte {
        assert(address <= 0xFFFFu)
        return readFromMemory(address.toUShort())
    }

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

    val storeInRegisterA: (UByte) -> Unit = { v -> registers.a = v }
    val storeInRegisterB: (UByte) -> Unit = { v -> registers.b = v }
    val storeInRegisterC: (UByte) -> Unit = { v -> registers.c = v }
    val storeInRegisterD: (UByte) -> Unit = { v -> registers.d = v }
    val storeInRegisterE: (UByte) -> Unit = { v -> registers.e = v }
    val storeInRegisterH: (UByte) -> Unit = { v -> registers.h = v }
    val storeInRegisterL: (UByte) -> Unit = { v -> registers.l = v }

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

    private fun load(save: (UByte) -> Unit, value: UByte) {
        save.invoke(value)
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

    private fun addSP(byte: () -> UByte) {
        with(registers) {
            val result = sp + byte.invoke()
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

