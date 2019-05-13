package com.malcolmcrum.gameboy

@ExperimentalUnsignedTypes
data class Operation(
        val name: String,
        val instructionBytes: Int,
        val operation: () -> Unit
) {
    override fun toString() = name
}

@ExperimentalUnsignedTypes
class OperationBuilder(val registers: Registers, val mmu: MMU, val interrupts: (Boolean) -> Unit) {
    val operations: Array<Operation> = Array(256) { Operation("MISSING", 1) { TODO() } }

    init {
        // instructionBytes is 1 (not 2) for all CB operations, because the first byte has already been read
        val cbOperations: Array<Operation> = Array(256) { Operation("MISSING", 1) { TODO() } }
        createBitOperations(cbOperations, 0x46, 0)
        createBitOperations(cbOperations, 0x4e, 1)
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

        operations[0xce] = Operation("ADC A,\$xx", 2) { adcA(readFromArgument()) }
        operations[0x8e] = Operation("ADC A,(HL)", 1) { adcA(readFromMemory(registers.hl)) }
        operations[0x8f] = Operation("ADC A,A", 1) { adcA(registers.a) }
        operations[0x88] = Operation("ADC A,B", 1) { adcA(registers.b) }
        operations[0x89] = Operation("ADC A,C", 1) { adcA(registers.c) }
        operations[0x8a] = Operation("ADC A,D", 1) { adcA(registers.d) }
        operations[0x8b] = Operation("ADC A,E", 1) { adcA(registers.e) }
        operations[0x8c] = Operation("ADC A,H", 1) { adcA(registers.h) }
        operations[0x8d] = Operation("ADC A,L", 1) { adcA(registers.l) }
        operations[0xc6] = Operation("ADD A,\$xx", 2) { addA(readFromArgument()) }
        operations[0x86] = Operation("ADD A,(HL)", 1) { addA(readFromMemory(registers.hl)) }
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
        operations[0xe8] = Operation("ADD SP,\$xx", 2) { addSP(readFromArgument()) }
        operations[0xe6] = Operation("AND \$xx", 2) { andA(readFromArgument()) }
        operations[0xa6] = Operation("AND (HL)", 1) { andA(readFromMemory(registers.hl)) }
        operations[0xa7] = Operation("AND A", 1) { andA(registers.a) }
        operations[0xa0] = Operation("AND B", 1) { andA(registers.b) }
        operations[0xa1] = Operation("AND C", 1) { andA(registers.c) }
        operations[0xa2] = Operation("AND D", 1) { andA(registers.d) }
        operations[0xa3] = Operation("AND E", 1) { andA(registers.e) }
        operations[0xa4] = Operation("AND H", 1) { andA(registers.h) }
        operations[0xa5] = Operation("AND L", 1) { andA(registers.l) }
        operations[0xcd] = Operation("CALL \$aabb", 3) { call(readWordArgument()) }
        operations[0xdc] = Operation("CALL C,\$aabb", 3) { call(readWordArgument(), registers.carry) }
        operations[0xd4] = Operation("CALL NC,\$aabb", 3) { call(readWordArgument(), !registers.carry) }
        operations[0xc4] = Operation("CALL NZ,\$aabb", 3) { call(readWordArgument(), !registers.zero) }
        operations[0xcc] = Operation("CALL Z,\$aabb", 3) { call(readWordArgument(), registers.zero) }
        operations[0x3f] = Operation("CCF", 1) { ccf() }
        operations[0xfe] = Operation("CP \$xx", 2) { cp(readFromMemory(registers.sp + 1u)) }
        operations[0xbe] = Operation("CP (HL)", 1) { cp(readFromMemory(registers.hl)) }
        operations[0xbf] = Operation("CP A", 1) { cp(registers.a) }
        operations[0xb8] = Operation("CP B", 1) { cp(registers.b) }
        operations[0xb9] = Operation("CP C", 1) { cp(registers.c) }
        operations[0xba] = Operation("CP D", 1) { cp(registers.d) }
        operations[0xbb] = Operation("CP E", 1) { cp(registers.e) }
        operations[0xbc] = Operation("CP H", 1) { cp(registers.h) }
        operations[0xbd] = Operation("CP L", 1) { cp(registers.l) }
        operations[0x2f] = Operation("CPL", 1) { cpl() }
        operations[0x27] = Operation("DAA", 1) { daa() }
        operations[0x35] = Operation("DEC (HL)", 1) { dec(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        operations[0x3D] = Operation("DEC A", 1) { dec(storeInRegisterA(), registers.a) }
        operations[0x05] = Operation("DEC B", 1) { dec(storeInRegisterB(), registers.b) }
        operations[0x0b] = Operation("DEC BC", 1) { dec(storeInRegisterBC(), registers.bc) }
        operations[0x0d] = Operation("DEC C", 1) { dec(storeInRegisterC(), registers.c) }
        operations[0x15] = Operation("DEC D", 1) { dec(storeInRegisterD(), registers.d) }
        operations[0x1b] = Operation("DEC DE", 1) { dec(storeInRegisterDE(), registers.de) }
        operations[0x1d] = Operation("DEC E", 1) { dec(storeInRegisterE(), registers.e) }
        operations[0x25] = Operation("DEC H", 1) { dec(storeInRegisterH(), registers.h) }
        operations[0x2b] = Operation("DEC HL", 1) { dec(storeInRegisterHL(), registers.hl) }
        operations[0x2d] = Operation("DEC L", 1) { dec(storeInRegisterL(), registers.l) }
        operations[0x3b] = Operation("DEC SP", 1) { dec(storeInRegisterSP(), registers.sp) }
        operations[0xf3] = Operation("DI", 1) { di() }
        operations[0xfb] = Operation("EI", 1) { ei() }
        operations[0x76] = Operation("HALT", 1) { halt() }
        operations[0x34] = Operation("INC (HL)", 1) { inc(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        operations[0x3c] = Operation("INC A", 1) { inc(storeInRegisterA(), registers.a) }
        operations[0x04] = Operation("INC B", 1) { inc(storeInRegisterB(), registers.b) }
        operations[0x03] = Operation("INC BC", 1) { inc(storeInRegisterBC(), registers.bc) }
        operations[0x0c] = Operation("INC C", 1) { inc(storeInRegisterC(), registers.c) }
        operations[0x14] = Operation("INC D", 1) { inc(storeInRegisterD(), registers.d) }
        operations[0x13] = Operation("INC DE", 1) { inc(storeInRegisterDE(), registers.de) }
        operations[0x1c] = Operation("INC E", 1) { inc(storeInRegisterE(), registers.e) }
        operations[0x24] = Operation("INC H", 1) { inc(storeInRegisterH(), registers.h) }
        operations[0x23] = Operation("INC HL", 1) { inc(storeInRegisterHL(), registers.hl) }
        operations[0x2c] = Operation("INC L", 1) { inc(storeInRegisterL(), registers.l) }
        operations[0x33] = Operation("INC SP", 1) { inc(storeInRegisterSP(), registers.sp) }
        operations[0xc3] = Operation("JP \$aabb", 3) { jp(readWordArgument()) }
        operations[0xe9] = Operation("JP (HL)", 1) { jp({ registers.hl }) }
        operations[0xda] = Operation("JP C,\$aabb", 3) { jp(readWordArgument(), registers.carry) }
        operations[0xd2] = Operation("JP NC,\$aabb", 3) { jp(readWordArgument(), !registers.carry) }
        operations[0xc2] = Operation("JP NZ,\$aabb", 3) { jp(readWordArgument(), !registers.zero) }
        operations[0xca] = Operation("JP Z,\$aabb", 3) { jp(readWordArgument(), registers.zero) }
        operations[0x18] = Operation("JR \$xx", 2) { jr(readFromArgument()) }
        operations[0x38] = Operation("JR C,\$xx", 2) { jr(readFromArgument(), registers.carry) }
        operations[0x30] = Operation("JR NC,\$xx", 2) { jr(readFromArgument(), !registers.carry) }
        operations[0x20] = Operation("JR NZ,\$xx", 2) { jr(readFromArgument(), !registers.zero) }
        operations[0x28] = Operation("JR Z,\$xx", 2) { jr(readFromArgument(), registers.zero) }
        operations[0xea] = Operation("LD (\$aabb),A", 3) { load(storeInMemory(readWordArgument().invoke()), registers.a) }
        operations[0x08] = Operation("LD (\$aabb),SP", 3) { load(storeWordInMemory(readWordArgument().invoke()), registers.sp) }
        operations[0xe0] = Operation("LD (\$xx),A", 2) { load(storeInMemory(readFromArgument().invoke()), registers.a) }
        operations[0x02] = Operation("LD (BC),A", 1) { load(storeInMemory(registers.bc), registers.a) }
        operations[0xe2] = Operation("LD (C),A", 1) { load(storeInMemory(registers.c), registers.a) }
        operations[0x12] = Operation("LD (DE),A", 1) { load(storeInMemory(registers.de), registers.a) }
        operations[0x36] = Operation("LD (HL),\$xx", 2) { load(storeInMemory(registers.hl), readFromArgument()) }
        operations[0x77] = Operation("LD (HL),A", 1) { load(storeInMemory(registers.hl), registers.a) }
        operations[0x70] = Operation("LD (HL),B", 1) { load(storeInMemory(registers.hl), registers.b) }
        operations[0x71] = Operation("LD (HL),C", 1) { load(storeInMemory(registers.hl), registers.c) }
        operations[0x72] = Operation("LD (HL),D", 1) { load(storeInMemory(registers.hl), registers.d) }
        operations[0x73] = Operation("LD (HL),E", 1) { load(storeInMemory(registers.hl), registers.e) }
        operations[0x74] = Operation("LD (HL),H", 1) { load(storeInMemory(registers.hl), registers.h) }
        operations[0x75] = Operation("LD (HL),L", 1) { load(storeInMemory(registers.hl), registers.l) }
        operations[0x32] = Operation("LD (HL-),A", 1) { load(storeInMemory(registers.hl), registers.a) { registers.hl-- } }
        operations[0x22] = Operation("LD (HL+),A", 1) { load(storeInMemory(registers.hl), registers.a) { registers.hl++ } }
        operations[0x3e] = Operation("LD A,\$xx", 2) { load(storeInRegisterA(), readFromArgument()) }
        operations[0xfa] = Operation("LD A,(\$aabb)", 3) { load(storeInRegisterA(), readFromMemory(readWordArgument().invoke())) }
        operations[0xf0] = Operation("LD A,(BC)", 1) { load(storeInRegisterA(), readFromMemory(registers.bc)) }
        operations[0xf0] = Operation("LD A,(C)", 1) { load(storeInRegisterA(), readFromMemory(registers.c)) }
        operations[0xf0] = Operation("LD A,(DE)", 1) { load(storeInRegisterA(), readFromMemory(registers.de)) }
        operations[0xf0] = Operation("LD A,(HL)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) }
        operations[0xf0] = Operation("LD A,(HL-)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) { registers.hl-- } }
        operations[0xf0] = Operation("LD A,(HL+)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) { registers.hl++ } }
        createLDOperations("A", storeInRegisterA(), 0x7F)
        operations[0x06] = Operation("LD B,\$xx", 2) { load(storeInRegisterB(), readFromArgument()) }
        operations[0x46] = Operation("LD B,(HL)", 1) { load(storeInRegisterB(), readFromMemory(registers.hl)) }
        createLDOperations("B", storeInRegisterB(), 0x47)
        operations[0x01] = Operation("LD C,\$xx", 2) { load(storeInRegisterC(), readFromArgument()) }
        operations[0x0e] = Operation("LD C,(HL)", 1) { load(storeInRegisterC(), readFromMemory(registers.hl)) }
        createLDOperations("C", storeInRegisterC(), 0x4f)
        operations[0x16] = Operation("LD D,\$xx", 2) { load(storeInRegisterD(), readFromArgument()) }
        operations[0x56] = Operation("LD D,(HL)", 1) { load(storeInRegisterD(), readFromMemory(registers.hl)) }
        createLDOperations("D", storeInRegisterD(), 0x57)
        operations[0x11] = Operation("LD E,\$xx", 2) { load(storeInRegisterE(), readFromArgument()) }
        operations[0x1e] = Operation("LD E,(HL)", 1) { load(storeInRegisterE(), readFromMemory(registers.hl)) }
        createLDOperations("E", storeInRegisterE(), 0x5f)
        operations[0x26] = Operation("LD H,\$xx", 2) { load(storeInRegisterH(), readFromArgument()) }
        operations[0x66] = Operation("LD H,(HL)", 1) { load(storeInRegisterH(), readFromMemory(registers.hl)) }
        createLDOperations("H", storeInRegisterH(), 0x67)
        operations[0x21] = Operation("LD HL,\$aabb", 3) { load(storeInRegisterHL(), readWordArgument()) }
        operations[0xf8] = Operation("LD HL,SP", 1) { load(storeInRegisterHL(), registers.sp) }
        operations[0x2e] = Operation("LD L,\$xx", 2) { load(storeInRegisterL(), readFromArgument()) }
        operations[0x6e] = Operation("LD L,(HL)", 1) { load(storeInRegisterL(), readFromMemory(registers.hl)) }
        operations[0x68] = Operation("LD L,B", 1) { load(storeInRegisterL(), registers.b) }
        operations[0x69] = Operation("LD L,C", 1) { load(storeInRegisterL(), registers.c) }
        operations[0x6a] = Operation("LD L,D", 1) { load(storeInRegisterL(), registers.d) }
        operations[0x6b] = Operation("LD L,E", 1) { load(storeInRegisterL(), registers.e) }
        operations[0x6c] = Operation("LD L,H", 1) { load(storeInRegisterL(), registers.h) }
        operations[0x6d] = Operation("LD L,L", 1) { load(storeInRegisterL(), registers.l) }
        operations[0x31] = Operation("LD SP,\$aabb", 3) { load(storeInRegisterSP(), readWordArgument()) }
        operations[0xf9] = Operation("LD SP,HL", 1) { load(storeInRegisterSP(), registers.hl) }
        operations[0x00] = Operation("NOP", 1) { nop() }
        operations[0xf6] = Operation("OR \$xx", 2) { or(readFromArgument()) }
        operations[0xb6] = Operation("OR (HL)", 1) { or(readFromMemory(registers.hl)) }
        operations[0xb7] = Operation("OR A", 1) { or(registers.a) }
        operations[0xb0] = Operation("OR B", 1) { or(registers.b) }
        operations[0xb1] = Operation("OR C", 1) { or(registers.c) }
        operations[0xb2] = Operation("OR D", 1) { or(registers.d) }
        operations[0xb3] = Operation("OR E", 1) { or(registers.e) }
        operations[0xb4] = Operation("OR H", 1) { or(registers.h) }
        operations[0xb5] = Operation("OR L", 1) { or(registers.l) }
        operations[0xf1] = Operation("POP AF", 1) { pop(storeInRegisterAF()) }
        operations[0xc1] = Operation("POP BC", 1) { pop(storeInRegisterBC()) }
        operations[0xd1] = Operation("POP DE", 1) { pop(storeInRegisterDE()) }
        operations[0xe1] = Operation("POP HL", 1) { pop(storeInRegisterHL()) }
        operations[0xf5] = Operation("PUSH AF", 1) { push(registers.af) }
        operations[0xc5] = Operation("PUSH BC", 1) { push(registers.bc) }
        operations[0xd5] = Operation("PUSH DE", 1) { push(registers.de) }
        operations[0xe5] = Operation("PUSH HL", 1) { push(registers.hl) }
        operations[0xc9] = Operation("RET", 1) { ret() }
        operations[0xd8] = Operation("RET C", 1) { ret { registers.carry}}
        operations[0xd0] = Operation("RET NC", 1) { ret { !registers.carry}}
        operations[0xc0] = Operation("RET NZ", 1) { ret { !registers.zero} }
        operations[0xc8] = Operation("RET Z", 1) { ret { registers.zero }}
        operations[0xc8] = Operation("RETI", 1) { reti() }
        operations[0x17] = Operation("RLA", 1) { rla() }
        operations[0x07] = Operation("RLCA", 1) { rlca() }
        operations[0x1f] = Operation("RRA", 1) { rra() }
        operations[0x0f] = Operation("RRCA", 1) { rrca() }
        operations[0xc7] = Operation("RST $00", 1) { rst(0x00u) }
        operations[0xcf] = Operation("RST $08", 1) { rst(0x08u) }
        operations[0xd7] = Operation("RST $10", 1) { rst(0x10u) }
        operations[0xdf] = Operation("RST $18", 1) { rst(0x18u) }
        operations[0xe7] = Operation("RST $20", 1) { rst(0x20u) }
        operations[0xef] = Operation("RST $28", 1) { rst(0x28u) }
        operations[0xf7] = Operation("RST $30", 1) { rst(0x30u) }
        operations[0xff] = Operation("RST $38", 1) { rst(0x38u) }
        operations[0xde] = Operation("SBC A,\$xx", 2) { sbca(readFromMemory(registers.sp + 1u)) }
        operations[0x9e] = Operation("SBC A,(HL)", 1) { sbca(readFromMemory(registers.hl)) }
        operations[0x9f] = Operation("SBC A,A", 1) { sbca(registers.a) }
        operations[0x98] = Operation("SBC A,B", 1) { sbca(registers.b) }
        operations[0x99] = Operation("SBC A,C", 1) { sbca(registers.c) }
        operations[0x9a] = Operation("SBC A,D", 1) { sbca(registers.d) }
        operations[0x9b] = Operation("SBC A,E", 1) { sbca(registers.e) }
        operations[0x9c] = Operation("SBC A,H", 1) { sbca(registers.h) }
        operations[0x9d] = Operation("SBC A,L", 1) { sbca(registers.l) }
        operations[0x37] = Operation("SCF", 1) { scf() }
        operations[0x10] = Operation("STOP", 1) { stop() }
        operations[0xd6] = Operation("SUB \$xx", 2) { sub(readFromMemory(registers.sp + 1u))}
        operations[0x97] = Operation("SUB A", 1) { sub(registers.a)}
        operations[0x90] = Operation("SUB B", 1) { sub(registers.b)}
        operations[0x91] = Operation("SUB C", 1) { sub(registers.c)}
        operations[0x92] = Operation("SUB D", 1) { sub(registers.d)}
        operations[0x93] = Operation("SUB E", 1) { sub(registers.e)}
        operations[0x94] = Operation("SUB H", 1) { sub(registers.h)}
        operations[0x95] = Operation("SUB L", 1) { sub(registers.l)}
        operations[0xEE] = Operation("XOR \$xx", 1) { xor(readFromMemory(registers.sp + 1u))}
        operations[0xAE] = Operation("XOR (HL)", 1) { xor(readFromMemory(registers.hl))}
        operations[0xAF] = Operation("XOR A", 1) { xor(registers.a)}
        operations[0xA8] = Operation("XOR B", 1) { xor(registers.b)}
        operations[0xA9] = Operation("XOR C", 1) { xor(registers.c)}
        operations[0xAA] = Operation("XOR D", 1) { xor(registers.d)}
        operations[0xAB] = Operation("XOR E", 1) { xor(registers.e)}
        operations[0xAC] = Operation("XOR H", 1) { xor(registers.h)}
        operations[0xAD] = Operation("XOR L", 1) { xor(registers.l)}


        operations[0xcb] = Operation("0xCB operations", 2) { cbOperations[registers.sp++.toInt()] }
    }

    private fun readWordArgument() = readWordFromMemory(registers.pc + 1u)

    private fun readFromArgument() = readFromMemory(registers.pc + 1u)

    private fun xor(byte: () -> UByte) {
        xor(byte.invoke())
    }

    private fun xor(byte: UByte) {
        with(registers) {
            a = a xor byte
            setFlags(zero = a.toUInt() == 0u)
            tick()
        }
    }

    private fun sub(byte: () -> UByte) {
        sub(byte.invoke())
    }

    private fun sub(byte: UByte) {
        with(registers) {
            val result = a - byte
            setFlags(zero = result and 0xFFu == 0u, carry = byte > a, subtract = true)
            a = result.toUByte()
            tick()
        }
    }

    private fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun scf() {
        registers.carry = true
    }

    private fun sbca(byte: () -> UByte) {
        sbca(byte.invoke())
    }

    private fun sbca(byte: UByte) {
        with(registers) {
            val result = (a - byte - if (carry) 1u else 0u).toInt()
            setFlags(subtract = true, zero = result and 0xFF == 0, carry = result < 0)
            a = result.toUByte()
            tick()
        }
    }

    private fun rst(destination: UByte) {
        with(registers) {
            storeInMemory(sp - 1u).invoke(registers.pc.upperByte)
            storeInMemory(sp - 2u).invoke(registers.pc.lowerByte)
            pc = createUShort(0u, destination)
            sp = (sp - 2u).toUShort()
        }
    }

    private fun rra() {
        with (registers) {
            val newHighBit = if (carry) 1 else 0
            f = 0u
            carry = a.getBit(0)
            a = ((a.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            tick()
        }
    }

    private fun rrca() {
        with (registers) {
            val newHighBit = if (carry) 1 else 0
            f = 0u
            carry = a.getBit(0)
            a = ((a.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            tick()
        }
    }

    private fun rla() {
        with (registers) {
            val newLowBit = if (carry) 1 else 0
            f = 0u
            carry = a.getBit(7)
            a = ((a.toInt() shl 1) + newLowBit).toUByte()
            tick()
        }
    }

    private fun rlca() {
        with (registers) {
            f = 0u
            carry = a.getBit(7)
            val newLowBit = if (carry) 1 else 0
            a = ((a.toInt() shl 1) + newLowBit).toUByte()
            tick()
        }
    }

    private fun reti() {
        ret()
        interrupts.invoke(true)
    }

    private fun ret(condition: () -> Boolean = { true }) {
        if (condition.invoke()) {
            val lowerByte = readFromMemory(registers.sp).invoke()
            val upperByte = readFromMemory(registers.sp + 1u).invoke()
            registers.pc = createUShort(upperByte, lowerByte)
            registers.sp = (registers.sp + 2u).toUShort()
            registers.tick(2)
        }
        registers.tick()
    }

    private fun push(short: UShort) {
        registers.sp = (registers.sp - 2u).toUShort()
        storeWordInMemory(registers.sp).invoke(short)
        registers.tick()
    }

    private fun pop(destination: (UShort) -> Unit) {
        val result = readWordFromMemory(registers.sp).invoke()
        destination.invoke(result)
        registers.sp = (registers.sp + 2u).toUShort()
        registers.tick()
    }

    private fun or(source: () -> UByte) {
        or(source.invoke())
    }

    private fun or(source: UByte) {
        registers.a = registers.a or source
        registers.tick()
    }

    private fun createLDOperations(reg: String, save: (UByte) -> Unit, startIndex: Int) {
        operations[startIndex] = Operation("LD $reg,A", 1) { load(save, registers.a) }
        operations[startIndex - 7] = Operation("LD $reg,B", 1) { load(save, registers.b) }
        operations[startIndex - 6] = Operation("LD $reg,C", 1) { load(save, registers.c) }
        operations[startIndex - 5] = Operation("LD $reg,D", 1) { load(save, registers.d) }
        operations[startIndex - 4] = Operation("LD $reg,E", 1) { load(save, registers.e) }
        operations[startIndex - 3] = Operation("LD $reg,H", 1) { load(save, registers.h) }
        operations[startIndex - 2] = Operation("LD $reg,L", 1) { load(save, registers.l) }
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
        cbOperations[startIndex + 1] = Operation("RES $bitIndex,A", 1) { res(bitIndex, storeInRegisterA(), { registers.a }) }
        cbOperations[startIndex - 6] = Operation("RES $bitIndex,A", 1) { res(bitIndex, storeInRegisterB(), { registers.b }) }
        cbOperations[startIndex - 5] = Operation("RES $bitIndex,B", 1) { res(bitIndex, storeInRegisterC(), { registers.c }) }
        cbOperations[startIndex - 4] = Operation("RES $bitIndex,C", 1) { res(bitIndex, storeInRegisterD(), { registers.d }) }
        cbOperations[startIndex - 3] = Operation("RES $bitIndex,D", 1) { res(bitIndex, storeInRegisterE(), { registers.e }) }
        cbOperations[startIndex - 2] = Operation("RES $bitIndex,H", 1) { res(bitIndex, storeInRegisterH(), { registers.h }) }
        cbOperations[startIndex - 1] = Operation("RES $bitIndex,L", 1) { res(bitIndex, storeInRegisterL(), { registers.l }) }
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

    private fun jr(offset: () -> UByte, condition: Boolean = true) {
        if (condition) {
            registers.pc = (registers.pc + offset.invoke()).toUShort()
            registers.tick()
        }
        registers.tick()
    }

    private fun jp(source: () -> UShort, condition: Boolean = true) {
        if (condition) {
            registers.pc = source.invoke()
            registers.tick()
        }
        registers.tick()
    }

    // TODO: half-carry
    private fun inc(save: (UByte) -> Unit, source: UByte) {
        with(registers) {
            val result = source + 1u
            setFlags(zero = result and 0xFFu == 0u)
            save.invoke(result.toUByte())
            tick()
        }
    }

    private fun inc(save: (UByte) -> Unit, source: () -> UByte) {
        inc(save, source.invoke())
    }

    // TODO: half-carry
    private fun inc(save: (UShort) -> Unit, source: UShort) {
        with(registers) {
            val result = source + 1u
            setFlags(zero = result and 0xFFFFu == 0u)
            save.invoke(result.toUShort())
            tick(2)
        }
    }

    private fun halt() {
        TODO()
    }

    private fun di() {
        interrupts.invoke(false)
        registers.tick()
    }

    private fun ei() {
        interrupts.invoke(true)
        registers.tick()
    }

    private fun dec(save: (UByte) -> Unit, source: UByte) {
        with(registers) {
            val result = source - 1u
            setFlags(zero = result and 0xFFu == 0u, subtract = true)
            save.invoke(result.toUByte())
            tick()
        }
    }

    private fun dec(save: (UByte) -> Unit, source: () -> UByte) {
        dec(save, source.invoke())
    }

    // TODO - set half carry flag
    private fun dec(save: (UShort) -> Unit, source: UShort) {
        with(registers) {
            val result = source - 1u
            setFlags(zero = result and 0xFFu == 0u, subtract = true)
            save.invoke(result.toUShort())
            tick(2)
        }
    }

    // Converts A into packed BCD (e.g. 0x0B -> 0x1 in upper nibble and 0x2 in lower nibble)
    // thanks to https://github.com/stan-roelofs/Kotlin-Gameboy-Emulator/blob/master/src/main/kotlin/cpu/instructions/miscellaneous/DAA.kt
    private fun daa() {
        with(registers) {
            if (!subtract) {
                if (carry || a > 0x99u) {
                    a = (a + 0x60u).toUByte()
                    carry = true
                }
                if (halfCarry || (a and 0x0Fu) > 0x09u) {
                    a = (a + 0x06u).toUByte()
                }
            } else {
                if (carry) {
                    a = (a - 0x60u).toUByte()
                }
                if (halfCarry) {
                    a = (a - 0x06u).toUByte()
                }
            }
            zero = (a == 0u.toUByte())
            halfCarry = false

            tick()
        }
    }

    // Flip carry flag
    private fun ccf() {
        registers.carry = !registers.carry
        registers.tick()
    }

    private fun cp(byte: () -> UByte) {
        cp(byte.invoke())
    }

    // Fake subtraction - doesn't store result, but does set flags
    private fun cp(byte: UByte) {
        with(registers) {
            val result = a - byte
            setFlags(zero = result and 0xFFu == 0u, subtract = true, carry = byte > a)
            tick()
        }
    }

    // Flip bits in A
    private fun cpl() {
        with(registers) {
            a = a xor 0xffu
            f = 0u
            if (a == 0u.toUByte()) zero = true
            tick()
        }
    }

    private fun call(destination: () -> UShort, conditional: Boolean = true) {
        with(registers) {
            if (conditional) {
                pc = destination.invoke()
                sp = (sp - 2u).toUShort()
                tick(2)
            }
            tick(2)
        }
    }

    private fun res(index: Int, save: (UByte) -> Unit, load: () -> UByte) {
        val mask = (1 shl index).toUByte()
        save.invoke(load.invoke() xor mask)
        registers.tick()
    }

    private fun bit(index: Int, source: () -> UByte) {
        return bit(index, source.invoke())
    }

    private fun bit(index: Int, source: UByte) {
        val bit = source and (1 shl index).toUByte()
        registers.zero = bit == 0u.toUByte()
        registers.tick()
    }

    private fun readFromMemory(address: UInt): () -> UByte {
        assert(address <= 0xFFFFu)
        return readFromMemory(address.toUShort())
    }

    private fun readFromMemory(address: UShort): () -> UByte = {
        registers.tick()
        mmu[address]
    }

    private fun readFromMemory(offset: UByte): () -> UByte = {
        readFromMemory(offset).invoke()
    }

    private fun readWordFromMemory(absolute: UInt): () -> UShort {
        assert(absolute <= 0xFFFFu)
        return readWordFromMemory(absolute.toUShort())
    }

    private fun readWordFromMemory(absolute: UShort): () -> UShort = {
        val lowerByte = mmu[absolute]
        val upperByte = mmu[absolute + 1u]
        registers.tick(2)
        createUShort(upperByte, lowerByte)
    }

    private fun storeInRegisterA(): (UByte) -> Unit = { v -> registers.a = v }
    private fun storeInRegisterB(): (UByte) -> Unit = { v -> registers.b = v }
    private fun storeInRegisterC(): (UByte) -> Unit = { v -> registers.c = v }
    private fun storeInRegisterD(): (UByte) -> Unit = { v -> registers.d = v }
    private fun storeInRegisterE(): (UByte) -> Unit = { v -> registers.e = v }
    private fun storeInRegisterH(): (UByte) -> Unit = { v -> registers.h = v }
    private fun storeInRegisterL(): (UByte) -> Unit = { v -> registers.l = v }
    private fun storeInRegisterAF(): (UShort) -> Unit = { v -> registers.af = v }
    private fun storeInRegisterBC(): (UShort) -> Unit = { v -> registers.bc = v }
    private fun storeInRegisterDE(): (UShort) -> Unit = { v -> registers.de = v }
    private fun storeInRegisterHL(): (UShort) -> Unit = { v -> registers.hl = v }
    private fun storeInRegisterSP(): (UShort) -> Unit = { v -> registers.sp = v }

    private fun storeInMemory(address: UShort) = { value: UByte ->
        mmu[address] = value
        registers.tick()
    }

    private fun storeInMemory(indirectAddress: UInt): (UByte) -> Unit {
        assert(indirectAddress <= 0xFFFFu)
        return storeInMemory(indirectAddress.toUShort())
    }

    private fun storeInMemory(offset: UByte) = { value: UByte ->
        mmu[0xFF00u + offset] = value
        registers.tick()
    }

    private fun storeWordInMemory(address: UShort): (UShort) -> Unit = { value ->
        mmu[address + 1u] = value.upperByte
        mmu[address] = value.lowerByte
        registers.tick(2)
    }

    private fun load(destination: (UShort) -> Unit, source: UShort) {
        destination.invoke(source)
        registers.tick(2)
    }

    @JvmName("load16")
    private fun load(save: (UShort) -> Unit, load: () -> UShort) {
        val short = load.invoke()
        save.invoke(short)
    }

    private fun load(save: (UByte) -> Unit, load: () -> UByte, then: () -> Unit = {}) {
        val byte = load.invoke()
        save.invoke(byte)
        then.invoke()
        registers.tick()
    }

    private fun load(save: (UByte) -> Unit, value: UByte, then: () -> Unit = {}) {
        save.invoke(value)
        then.invoke()
        registers.tick()
    }

    private fun adcA(source: () -> UByte) {
        return adcA(source.invoke())
    }

    private fun adcA(byte: UByte) {
        with(registers) {
            val result = a + byte + if (registers.carry) 1u else 0u
            setFlags(zero = result and 0xFFu == 0u, carry = result > 0xFFu)
            a = result.toUByte()
            tick()
        }
    }

    fun nop() {
        registers.tick()
    }

    private fun addA(source: () -> UByte) {
        return addA(source.invoke())
    }

    private fun addA(byte: UByte) {
        with(registers) {
            val result = a + byte
            setFlags(zero = result and 0xFFu == 0u, carry = result > 0xFFu)
            a = result.toUByte()
            tick()
        }
    }

    private fun addHL(short: UShort) {
        with(registers) {
            val result = hl + short
            setFlags(zero = result and 0xFFFFu == 0u, carry = result > 0xFFFFu)
            hl = result.toUShort()
            tick(2)
        }
    }

    private fun andA(source: () -> UByte) {
        return andA(source.invoke())
    }

    private fun andA(byte: UByte) {
        with(registers) {
            a = a and byte
            if (a == 0u.toUByte()) zero = true
            tick()
        }
    }

    private fun addSP(byte: () -> UByte) {
        with(registers) {
            val result = sp + byte.invoke()
            setFlags(zero = result and 0xFFFFu == 0u, carry = result > 0xFFFFu)
            hl = result.toUShort()
            tick()
        }
    }
}