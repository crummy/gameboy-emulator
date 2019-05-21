package com.malcolmcrum.gameboy.emulator

import com.malcolmcrum.gameboy.util.*
import kotlin.experimental.and

@ExperimentalUnsignedTypes
abstract class Z80Operation(val mnemonic: String, val instructionBytes: Int) {
    abstract fun invoke(pc: UShort): UShort
    override fun toString() = mnemonic
}

@ExperimentalUnsignedTypes
open class Operation(
        name: String,
        instructionBytes: Int,
        private val operation: () -> Unit
) : Z80Operation(name, instructionBytes) {
    override fun invoke(pc: UShort): UShort {
        operation.invoke()
        return (pc + instructionBytes.toUInt()).toUShort()
    }
}

@ExperimentalUnsignedTypes
class CBOperation(
        name: String,
        operation: () -> Unit
) : Operation(name, 2, operation)

@ExperimentalUnsignedTypes
class Jump(name: String, instructionBytes: Int, private val operation: () -> UShort?) : Z80Operation(name, instructionBytes) {
    override fun invoke(pc: UShort): UShort {
        val destination = operation.invoke()
        return destination ?: (pc + instructionBytes.toUInt()).toUShort()
    }
}

@ExperimentalUnsignedTypes
class Operations(val registers: Registers, val mmu: MMU) {
    private val operations: Array<Z80Operation> = Array(256) { x -> Operation("MISSING ${x.toUByte().hex()}", 1) { TODO(x.toUByte().hex()) } }
    private val cbOperations: Array<CBOperation> = Array(256) { x -> CBOperation("MISSING ${x.toUByte().hex()}") { TODO(x.toUByte().hex()) } }

    operator fun get(address: UShort, forDebugPurposes: Boolean = true): Pair<UByte, Z80Operation> {
        val opcode = mmu[address, forDebugPurposes]
        return if (opcode != CB_OPCODE) {
            Pair(opcode, operations[opcode.toInt()])
        } else {
            val cbOpcode = readFromMemory(address + 1u).invoke()
            Pair(cbOpcode, cbOperations[cbOpcode.toInt()])
        }
    }

    init {
        createBitOperations(0x46, 0)
        createBitOperations(0x4e, 1)
        createBitOperations(0x56, 2)
        createBitOperations(0x5e, 3)
        createBitOperations(0x66, 4)
        createBitOperations(0x6e, 5)
        createBitOperations(0x76, 6)
        createBitOperations(0x7e, 7)
        createResOperations(0x86, 0)
        createResOperations(0x8e, 1)
        createResOperations(0x96, 2)
        createResOperations(0x9e, 3)
        createResOperations(0xa6, 4)
        createResOperations(0xae, 5)
        createResOperations(0xb6, 6)
        createResOperations(0xbe, 7)
        cbOperations[0x16] = CBOperation("RL (HL)") { rl(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x17] = CBOperation("RL A") { rl(storeInRegisterA(), registers.a) }
        cbOperations[0x10] = CBOperation("RL B") { rl(storeInRegisterB(), registers.b) }
        cbOperations[0x11] = CBOperation("RL C") { rl(storeInRegisterC(), registers.c) }
        cbOperations[0x12] = CBOperation("RL D") { rl(storeInRegisterD(), registers.d) }
        cbOperations[0x13] = CBOperation("RL E") { rl(storeInRegisterE(), registers.e) }
        cbOperations[0x14] = CBOperation("RL H") { rl(storeInRegisterH(), registers.h) }
        cbOperations[0x15] = CBOperation("RL L") { rl(storeInRegisterL(), registers.l) }
        cbOperations[0x06] = CBOperation("RLC (HL)") { rlc(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x07] = CBOperation("RLC A") { rlc(storeInRegisterA(), registers.a) }
        cbOperations[0x00] = CBOperation("RLC B") { rlc(storeInRegisterB(), registers.b) }
        cbOperations[0x01] = CBOperation("RLC C") { rlc(storeInRegisterC(), registers.c) }
        cbOperations[0x02] = CBOperation("RLC D") { rlc(storeInRegisterD(), registers.d) }
        cbOperations[0x03] = CBOperation("RLC E") { rlc(storeInRegisterE(), registers.e) }
        cbOperations[0x04] = CBOperation("RLC H") { rlc(storeInRegisterH(), registers.h) }
        cbOperations[0x05] = CBOperation("RLC L") { rlc(storeInRegisterL(), registers.l) }
        cbOperations[0x1e] = CBOperation("RR (HL)") { rr(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x1f] = CBOperation("RR A") { rr(storeInRegisterA(), registers.a) }
        cbOperations[0x18] = CBOperation("RR B") { rr(storeInRegisterB(), registers.b) }
        cbOperations[0x19] = CBOperation("RR C") { rr(storeInRegisterC(), registers.c) }
        cbOperations[0x1a] = CBOperation("RR D") { rr(storeInRegisterD(), registers.d) }
        cbOperations[0x1b] = CBOperation("RR E") { rr(storeInRegisterE(), registers.e) }
        cbOperations[0x1c] = CBOperation("RR H") { rr(storeInRegisterH(), registers.h) }
        cbOperations[0x1d] = CBOperation("RR L") { rr(storeInRegisterL(), registers.l) }
        cbOperations[0x0e] = CBOperation("RRC (HL)") { rrc(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x0f] = CBOperation("RRC A") { rrc(storeInRegisterA(), registers.a) }
        cbOperations[0x08] = CBOperation("RRC B") { rrc(storeInRegisterB(), registers.b) }
        cbOperations[0x09] = CBOperation("RRC C") { rrc(storeInRegisterC(), registers.c) }
        cbOperations[0x0a] = CBOperation("RRC D") { rrc(storeInRegisterD(), registers.d) }
        cbOperations[0x0b] = CBOperation("RRC E") { rrc(storeInRegisterE(), registers.e) }
        cbOperations[0x0c] = CBOperation("RRC H") { rrc(storeInRegisterH(), registers.h) }
        cbOperations[0x0d] = CBOperation("RRC L") { rrc(storeInRegisterL(), registers.l) }
        createSetOperations(0xc6, 0)
        createSetOperations(0xce, 1)
        createSetOperations(0xd6, 2)
        createSetOperations(0xde, 3)
        createSetOperations(0xe6, 4)
        createSetOperations(0xee, 5)
        createSetOperations(0xf6, 6)
        createSetOperations(0xfe, 7)
        cbOperations[0x26] = CBOperation("SLA (HL)") { sla(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x27] = CBOperation("SLA A") { sla(storeInRegisterA(), { registers.a }) }
        cbOperations[0x20] = CBOperation("SLA B") { sla(storeInRegisterB(), { registers.b }) }
        cbOperations[0x21] = CBOperation("SLA C") { sla(storeInRegisterC(), { registers.c }) }
        cbOperations[0x22] = CBOperation("SLA D") { sla(storeInRegisterD(), { registers.d }) }
        cbOperations[0x23] = CBOperation("SLA E") { sla(storeInRegisterE(), { registers.e }) }
        cbOperations[0x24] = CBOperation("SLA H") { sla(storeInRegisterH(), { registers.h }) }
        cbOperations[0x25] = CBOperation("SLA L") { sla(storeInRegisterL(), { registers.l }) }
        cbOperations[0x2e] = CBOperation("SRA (HL)") { sra(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x2f] = CBOperation("SRA A") { sra(storeInRegisterA(), { registers.a }) }
        cbOperations[0x28] = CBOperation("SRA B") { sra(storeInRegisterB(), { registers.b }) }
        cbOperations[0x29] = CBOperation("SRA C") { sra(storeInRegisterC(), { registers.c }) }
        cbOperations[0x2a] = CBOperation("SRA D") { sra(storeInRegisterD(), { registers.d }) }
        cbOperations[0x2b] = CBOperation("SRA E") { sra(storeInRegisterE(), { registers.e }) }
        cbOperations[0x2c] = CBOperation("SRA H") { sra(storeInRegisterH(), { registers.h }) }
        cbOperations[0x2d] = CBOperation("SRA L") { sra(storeInRegisterL(), { registers.l }) }
        cbOperations[0x3e] = CBOperation("SRL (HL)") { sra(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x3f] = CBOperation("SRL A") { srl(storeInRegisterA(), { registers.a }) }
        cbOperations[0x38] = CBOperation("SRL B") { srl(storeInRegisterB(), { registers.b }) }
        cbOperations[0x39] = CBOperation("SRL C") { srl(storeInRegisterC(), { registers.c }) }
        cbOperations[0x3a] = CBOperation("SRL D") { srl(storeInRegisterD(), { registers.d }) }
        cbOperations[0x3b] = CBOperation("SRL E") { srl(storeInRegisterE(), { registers.e }) }
        cbOperations[0x3c] = CBOperation("SRL H") { srl(storeInRegisterH(), { registers.h }) }
        cbOperations[0x3d] = CBOperation("SRL L") { srl(storeInRegisterL(), { registers.l }) }
        cbOperations[0x36] = CBOperation("SWAP (HL)") { swap(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[0x37] = CBOperation("SWAP A") { swap(storeInRegisterA(), { registers.a }) }
        cbOperations[0x30] = CBOperation("SWAP B") { swap(storeInRegisterB(), { registers.b }) }
        cbOperations[0x31] = CBOperation("SWAP C") { swap(storeInRegisterC(), { registers.c }) }
        cbOperations[0x32] = CBOperation("SWAP D") { swap(storeInRegisterD(), { registers.d }) }
        cbOperations[0x33] = CBOperation("SWAP E") { swap(storeInRegisterE(), { registers.e }) }
        cbOperations[0x34] = CBOperation("SWAP H") { swap(storeInRegisterH(), { registers.h }) }
        cbOperations[0x35] = CBOperation("SWAP L") { swap(storeInRegisterL(), { registers.l }) }

        operations[0xce] = Operation("ADC A,n8", 2) { adcA(readFromArgument()) }
        operations[0x8e] = Operation("ADC A,(HL)", 1) { adcA(readFromMemory(registers.hl)) }
        operations[0x8f] = Operation("ADC A,A", 1) { adcA(registers.a) }
        operations[0x88] = Operation("ADC A,B", 1) { adcA(registers.b) }
        operations[0x89] = Operation("ADC A,C", 1) { adcA(registers.c) }
        operations[0x8a] = Operation("ADC A,D", 1) { adcA(registers.d) }
        operations[0x8b] = Operation("ADC A,E", 1) { adcA(registers.e) }
        operations[0x8c] = Operation("ADC A,H", 1) { adcA(registers.h) }
        operations[0x8d] = Operation("ADC A,L", 1) { adcA(registers.l) }
        operations[0xc6] = Operation("ADD A,n8", 2) { addA(readFromArgument()) }
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
        operations[0xe8] = Operation("ADD SP,e8", 2) { addSP(readFromArgument()) }
        operations[0xe6] = Operation("AND n8", 2) { andA(readFromArgument()) }
        operations[0xa6] = Operation("AND (HL)", 1) { andA(readFromMemory(registers.hl)) }
        operations[0xa7] = Operation("AND A", 1) { andA(registers.a) }
        operations[0xa0] = Operation("AND B", 1) { andA(registers.b) }
        operations[0xa1] = Operation("AND C", 1) { andA(registers.c) }
        operations[0xa2] = Operation("AND D", 1) { andA(registers.d) }
        operations[0xa3] = Operation("AND E", 1) { andA(registers.e) }
        operations[0xa4] = Operation("AND H", 1) { andA(registers.h) }
        operations[0xa5] = Operation("AND L", 1) { andA(registers.l) }
        operations[0xcd] = Jump("CALL n16", 3) { call(readWordArgument()) }
        operations[0xdc] = Jump("CALL C,n16", 3) { call(readWordArgument(), registers.carry) }
        operations[0xd4] = Jump("CALL NC,n16", 3) { call(readWordArgument(), !registers.carry) }
        operations[0xc4] = Jump("CALL NZ,n16", 3) { call(readWordArgument(), !registers.zero) }
        operations[0xcc] = Jump("CALL Z,n16", 3) { call(readWordArgument(), registers.zero) }
        operations[0x3f] = Operation("CCF", 1) { ccf() }
        operations[0xfe] = Operation("CP n8", 2) { cp(readFromArgument()) }
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
        operations[0x3D] = Operation("DEC A", 1) { dec(storeInRegisterA(), {registers.a}) }
        operations[0x05] = Operation("DEC B", 1) { dec(storeInRegisterB(), {registers.b}) }
        operations[0x0b] = Operation("DEC BC", 1) { dec(storeInRegisterBC(), readFromRegisterBC()) }
        operations[0x0d] = Operation("DEC C", 1) { dec(storeInRegisterC(), {registers.c}) }
        operations[0x15] = Operation("DEC D", 1) { dec(storeInRegisterD(), {registers.d}) }
        operations[0x1b] = Operation("DEC DE", 1) { dec(storeInRegisterDE(), readFromRegisterDE()) }
        operations[0x1d] = Operation("DEC E", 1) { dec(storeInRegisterE(), {registers.e}) }
        operations[0x25] = Operation("DEC H", 1) { dec(storeInRegisterH(), {registers.h}) }
        operations[0x2b] = Operation("DEC HL", 1) { dec(storeInRegisterHL(), readFromRegisterHL()) }
        operations[0x2d] = Operation("DEC L", 1) { dec(storeInRegisterL(), {registers.l}) }
        operations[0x3b] = Operation("DEC SP", 1) { dec(storeInRegisterSP(), readFromRegisterSP()) }
        operations[0xf3] = Operation("DI", 1) { di() }
        operations[0xfb] = Operation("EI", 1) { ei() }
        operations[0x76] = Operation("HALT", 1) { halt() }
        operations[0x34] = Operation("INC (HL)", 1) { inc(storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        operations[0x3c] = Operation("INC A", 1) { inc(storeInRegisterA(), { registers.a }) }
        operations[0x04] = Operation("INC B", 1) { inc(storeInRegisterB(), { registers.b }) }
        operations[0x03] = Operation("INC BC", 1) { inc(storeInRegisterBC(), readFromRegisterBC()) }
        operations[0x0c] = Operation("INC C", 1) { inc(storeInRegisterC(), { registers.c }) }
        operations[0x14] = Operation("INC D", 1) { inc(storeInRegisterD(), { registers.d }) }
        operations[0x13] = Operation("INC DE", 1) { inc(storeInRegisterDE(), readFromRegisterDE()) }
        operations[0x1c] = Operation("INC E", 1) { inc(storeInRegisterE(), { registers.e }) }
        operations[0x24] = Operation("INC H", 1) { inc(storeInRegisterH(), { registers.h }) }
        operations[0x23] = Operation("INC HL", 1) { inc(storeInRegisterHL(), readFromRegisterHL()) }
        operations[0x2c] = Operation("INC L", 1) { inc(storeInRegisterL(), { registers.l }) }
        operations[0x33] = Operation("INC SP", 1) { inc(storeInRegisterSP(), readFromRegisterSP()) }
        operations[0xc3] = Jump("JP n16", 3) { jp(readWordArgument()) }
        operations[0xe9] = Jump("JP HL", 1) { jp(registers.hl) }
        operations[0xda] = Jump("JP C,n16", 3) { jp(readWordArgument(), registers.carry) }
        operations[0xd2] = Jump("JP NC,n16", 3) { jp(readWordArgument(), !registers.carry) }
        operations[0xc2] = Jump("JP NZ,n16", 3) { jp(readWordArgument(), !registers.zero) }
        operations[0xca] = Jump("JP Z,n16", 3) { jp(readWordArgument(), registers.zero) }
        operations[0x18] = Jump("JR e8", 2) { jr(readFromArgument()) }
        operations[0x38] = Jump("JR C,e8", 2) { jr(readFromArgument(), registers.carry) }
        operations[0x30] = Jump("JR NC,e8", 2) { jr(readFromArgument(), !registers.carry) }
        operations[0x20] = Jump("JR NZ,e8", 2) { jr(readFromArgument(), !registers.zero) }
        operations[0x28] = Jump("JR Z,e8", 2) { jr(readFromArgument(), registers.zero) }
        operations[0xea] = Operation("LD (n16),A", 3) { load(storeInMemory(readWordArgument().invoke()), registers.a) }
        operations[0x08] = Operation("LD (n16),SP", 3) { load(storeWordInMemory(readWordArgument().invoke()), registers.sp) }
        operations[0xe0] = Operation("LDH (n8),A", 2) { load(storeInHighMemory(readFromArgument().invoke()), registers.a) }
        operations[0x02] = Operation("LD (BC),A", 1) { load(storeInMemory(registers.bc), registers.a) }
        operations[0xe2] = Operation("LDH (C),A", 1) { load(storeInHighMemory(registers.c), registers.a) }
        operations[0x12] = Operation("LD (DE),A", 1) { load(storeInMemory(registers.de), registers.a) }
        operations[0x36] = Operation("LD (HL),n8", 2) { load(storeInMemory(registers.hl), readFromArgument()) }
        operations[0x77] = Operation("LD (HL),A", 1) { load(storeInMemory(registers.hl), registers.a) }
        operations[0x70] = Operation("LD (HL),B", 1) { load(storeInMemory(registers.hl), registers.b) }
        operations[0x71] = Operation("LD (HL),C", 1) { load(storeInMemory(registers.hl), registers.c) }
        operations[0x72] = Operation("LD (HL),D", 1) { load(storeInMemory(registers.hl), registers.d) }
        operations[0x73] = Operation("LD (HL),E", 1) { load(storeInMemory(registers.hl), registers.e) }
        operations[0x74] = Operation("LD (HL),H", 1) { load(storeInMemory(registers.hl), registers.h) }
        operations[0x75] = Operation("LD (HL),L", 1) { load(storeInMemory(registers.hl), registers.l) }
        operations[0x32] = Operation("LD (HL-),A", 1) { load(storeInMemory(registers.hl), registers.a) { registers.hl-- } }
        operations[0x22] = Operation("LD (HL+),A", 1) { load(storeInMemory(registers.hl), registers.a) { registers.hl++ } }
        operations[0x3e] = Operation("LD A,n8", 2) { load(storeInRegisterA(), readFromArgument()) }
        operations[0xfa] = Operation("LD A,(n16)", 3) { load(storeInRegisterA(), readFromMemory(readWordArgument().invoke())) }
        operations[0xf0] = Operation("LDH A,(n8)", 2) { load(storeInRegisterA(), readFromHighMemory(readFromArgument().invoke())) }
        operations[0x0a] = Operation("LD A,(BC)", 1) { load(storeInRegisterA(), readFromMemory(registers.bc)) }
        operations[0xf2] = Operation("LD A,(C)", 1) { load(storeInRegisterA(), readFromHighMemory(registers.c)) }
        operations[0x1a] = Operation("LD A,(DE)", 1) { load(storeInRegisterA(), readFromMemory(registers.de)) }
        operations[0x7e] = Operation("LD A,(HL)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) }
        operations[0x3a] = Operation("LD A,(HL-)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) { registers.hl-- } }
        operations[0x2a] = Operation("LD A,(HL+)", 1) { load(storeInRegisterA(), readFromMemory(registers.hl)) { registers.hl++ } }
        createLDOperations("A", storeInRegisterA(), 0x7F)
        operations[0x06] = Operation("LD B,n8", 2) { load(storeInRegisterB(), readFromArgument()) }
        operations[0x46] = Operation("LD B,(HL)", 1) { load(storeInRegisterB(), readFromMemory(registers.hl)) }
        createLDOperations("B", storeInRegisterB(), 0x47)
        operations[0x01] = Operation("LD BC,n16", 3) { load(storeInRegisterBC(), readWordArgument()) }
        operations[0x0e] = Operation("LD C,n8", 2) { load(storeInRegisterC(), readFromArgument()) }
        operations[0x4e] = Operation("LD C,(HL)", 1) { load(storeInRegisterC(), readFromMemory(registers.hl)) }
        createLDOperations("C", storeInRegisterC(), 0x4f)
        operations[0x16] = Operation("LD D,n8", 2) { load(storeInRegisterD(), readFromArgument()) }
        operations[0x56] = Operation("LD D,(HL)", 1) { load(storeInRegisterD(), readFromMemory(registers.hl)) }
        createLDOperations("D", storeInRegisterD(), 0x57)
        operations[0x11] = Operation("LD DE,n16", 3) { load(storeInRegisterDE(), readWordArgument()) }
        operations[0x1e] = Operation("LD E,n8", 2) { load(storeInRegisterE(), readFromArgument()) }
        operations[0x5e] = Operation("LD E,(HL)", 1) { load(storeInRegisterE(), readFromMemory(registers.hl)) }
        createLDOperations("E", storeInRegisterE(), 0x5f)
        operations[0x26] = Operation("LD H,n8", 2) { load(storeInRegisterH(), readFromArgument()) }
        operations[0x66] = Operation("LD H,(HL)", 1) { load(storeInRegisterH(), readFromMemory(registers.hl)) }
        createLDOperations("H", storeInRegisterH(), 0x67)
        operations[0x21] = Operation("LD HL,n16", 3) { load(storeInRegisterHL(), readWordArgument()) }
        operations[0xf8] = Operation("LD HL,SP+e8", 2) { loadSigned(storeInRegisterHL(), { registers.sp.toShort() + readSignedArgument() }) }
        operations[0x2e] = Operation("LD L,n8", 2) { load(storeInRegisterL(), readFromArgument()) }
        operations[0x6e] = Operation("LD L,(HL)", 1) { load(storeInRegisterL(), readFromMemory(registers.hl)) }
        operations[0x6f] = Operation("LD L,A", 1) { load(storeInRegisterA(), registers.a) }
        operations[0x68] = Operation("LD L,B", 1) { load(storeInRegisterL(), registers.b) }
        operations[0x69] = Operation("LD L,C", 1) { load(storeInRegisterL(), registers.c) }
        operations[0x6a] = Operation("LD L,D", 1) { load(storeInRegisterL(), registers.d) }
        operations[0x6b] = Operation("LD L,E", 1) { load(storeInRegisterL(), registers.e) }
        operations[0x6c] = Operation("LD L,H", 1) { load(storeInRegisterL(), registers.h) }
        operations[0x6d] = Operation("LD L,L", 1) { load(storeInRegisterL(), registers.l) }
        operations[0x31] = Operation("LD SP,n16", 3) { load(storeInRegisterSP(), readWordArgument()) }
        operations[0xf9] = Operation("LD SP,HL", 1) { load(storeInRegisterSP(), registers.hl) }
        operations[0x00] = Operation("NOP", 1) { nop() }
        operations[0xf6] = Operation("OR n8", 2) { or(readFromArgument()) }
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
        operations[0xc9] = Jump("RET", 1) { ret() }
        operations[0xd8] = Jump("RET C", 1) { ret { registers.carry } }
        operations[0xd0] = Jump("RET NC", 1) { ret { !registers.carry } }
        operations[0xc0] = Jump("RET NZ", 1) { ret { !registers.zero } }
        operations[0xc8] = Jump("RET Z", 1) { ret { registers.zero } }
        operations[0xd9] = Operation("RETI", 1) { reti() }
        operations[0x17] = Operation("RLA", 1) { rla() }
        operations[0x07] = Operation("RLCA", 1) { rlca() }
        operations[0x1f] = Operation("RRA", 1) { rra() }
        operations[0x0f] = Operation("RRCA", 1) { rrca() }
        operations[0xc7] = Jump("RST $00", 1) { rst(0x00u) }
        operations[0xcf] = Jump("RST $08", 1) { rst(0x08u) }
        operations[0xd7] = Jump("RST $10", 1) { rst(0x10u) }
        operations[0xdf] = Jump("RST $18", 1) { rst(0x18u) }
        operations[0xe7] = Jump("RST $20", 1) { rst(0x20u) }
        operations[0xef] = Jump("RST $28", 1) { rst(0x28u) }
        operations[0xf7] = Jump("RST $30", 1) { rst(0x30u) }
        operations[0xff] = Jump("RST $38", 1) { rst(0x38u) }
        operations[0xde] = Operation("SBC A,n8", 2) { sbca(readFromArgument()) }
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
        operations[0xd6] = Operation("SUB n8", 2) { sub(readFromArgument()) }
        operations[0x96] = Operation("SUB (HL)", 2) { sub(readFromMemory(registers.hl)) }
        operations[0x97] = Operation("SUB A", 1) { sub(registers.a) }
        operations[0x90] = Operation("SUB B", 1) { sub(registers.b) }
        operations[0x91] = Operation("SUB C", 1) { sub(registers.c) }
        operations[0x92] = Operation("SUB D", 1) { sub(registers.d) }
        operations[0x93] = Operation("SUB E", 1) { sub(registers.e) }
        operations[0x94] = Operation("SUB H", 1) { sub(registers.h) }
        operations[0x95] = Operation("SUB L", 1) { sub(registers.l) }
        operations[0xEE] = Operation("XOR n8", 1) { xor(readFromArgument()) }
        operations[0xAE] = Operation("XOR (HL)", 1) { xor(readFromMemory(registers.hl)) }
        operations[0xAF] = Operation("XOR A", 1) { xor(registers.a) }
        operations[0xA8] = Operation("XOR B", 1) { xor(registers.b) }
        operations[0xA9] = Operation("XOR C", 1) { xor(registers.c) }
        operations[0xAA] = Operation("XOR D", 1) { xor(registers.d) }
        operations[0xAB] = Operation("XOR E", 1) { xor(registers.e) }
        operations[0xAC] = Operation("XOR H", 1) { xor(registers.h) }
        operations[0xAD] = Operation("XOR L", 1) { xor(registers.l) }
    }

    private inline fun sla(write: (UByte) -> Unit, read: () -> UByte) {
        with(registers) {
            val value = read.invoke()
            val newCarry = value.getBit(7)
            val result = (value.toUInt() shl 1).toUByte()
            setFlags(zero = result == 0u.toUByte(), carry = newCarry)
            write.invoke(result)
            tick()
        }
    }

    private inline fun sra(write: (UByte) -> Unit, read: () -> UByte) {
        with(registers) {
            val value = read.invoke()
            val newCarry = value.getBit(0)
            val newLeftBit = if (value.getBit(7)) 1u else 0u
            val result = (value.toUInt() shr 1 or (newLeftBit shl 7)).toUByte()
            setFlags(zero = result == 0u.toUByte(), carry = newCarry)
            write.invoke(result)
            tick()
        }
    }

    private inline fun srl(write: (UByte) -> Unit, read: () -> UByte) {
        with(registers) {
            val value = read.invoke()
            val newCarry = value.getBit(0)
            val result = (value.toUInt() shr 1).toUByte()
            setFlags(zero = result == 0u.toUByte(), carry = newCarry)
            write.invoke(result)
            tick()
        }
    }

    private inline fun swap(write: (UByte) -> Unit, read: () -> UByte) {
        val value = read.invoke()
        val result = createUByte(value.lowerNibble, value.upperNibble)
        write.invoke(result)
        registers.tick()
    }

    private fun readWordArgument() = readWordFromMemory(registers.pc + 1u)

    private fun readFromArgument() = readFromMemory(registers.pc + 1u)

    private fun readSignedArgument(): Byte {
        registers.tick()
        val address = registers.pc + 1u
        return mmu[address].toByte()
    }

    private inline fun xor(byte: () -> UByte) {
        xor(byte.invoke())
    }

    private fun xor(byte: UByte) {
        with(registers) {
            a = a xor byte
            setFlags(zero = a.toUInt() == 0u)
            tick()
        }
    }

    private inline fun sub(byte: () -> UByte) {
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
        registers.stopped = true
        registers.tick()
    }

    private fun scf() {
        registers.carry = true
        registers.tick()
    }

    private inline fun sbca(byte: () -> UByte) {
        sbca(byte.invoke())
    }

    private fun sbca(byte: UByte) {
        with(registers) {
            val result = (a - byte - if (carry) 1u else 0u).toInt()
            setFlags(subtract = true, zero = result and 0xFF == 0, carry = result < 0, halfCarry = halfCarrySub(a, byte))
            a = result.toUByte()
            tick()
        }
    }

    private fun rst(destination: UByte): UShort? {
        with(registers) {
            storeInMemory(sp - 1u).invoke(registers.pc.upperByte)
            storeInMemory(sp - 2u).invoke(registers.pc.lowerByte)
            sp = (sp - 2u).toUShort()
            tick(2)
            return createUShort(0u, destination)
        }
    }

    private fun rra() {
        with(registers) {
            val newHighBit = if (carry) 1 else 0
            setFlags(carry = a.getBit(0))
            a = ((a.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            tick()
        }
    }

    private inline fun rr(store: (UByte) -> Unit, read: () -> UByte) {
        rr(store, read.invoke())
    }

    private inline fun rr(store: (UByte) -> Unit, value: UByte) {
        with(registers) {
            val newHighBit = if (carry) 1 else 0
            setFlags(carry = a.getBit(0))
            val result = ((value.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            store.invoke(result)
            tick()
        }
    }

    private fun rrca() {
        with(registers) {
            val newHighBit = if (carry) 1 else 0
            setFlags(carry = a.getBit(0))
            a = ((a.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            tick()
        }
    }

    private inline fun rrc(store: (UByte) -> Unit, read: () -> UByte) {
        rrc(store, read.invoke())
    }

    private inline fun rrc(store: (UByte) -> Unit, value: UByte) {
        with(registers) {
            val newHighBit = if (carry) 1 else 0
            setFlags(carry = value.getBit(0))
            val result = ((value.toInt() ushr 1) + (newHighBit shr 7)).toUByte()
            store.invoke(result)
            tick()
        }
    }

    private fun rla() {
        with(registers) {
            val newLowBit = if (carry) 1 else 0
            setFlags(carry = a.getBit(7))
            a = ((a.toInt() shl 1) + newLowBit).toUByte()
            tick()
        }
    }

    private fun rlca() {
        with(registers) {
            setFlags(carry = a.getBit(7))
            val newLowBit = if (carry) 1 else 0
            a = ((a.toInt() shl 1) + newLowBit).toUByte()
            tick()
        }
    }

    private inline fun rl(store: (UByte) -> Unit, read: () -> UByte) {
        rl(store, read.invoke())
    }

    private inline fun rl(store: (UByte) -> Unit, value: UByte) {
        with(registers) {
            val newLowBit = if (carry) 1 else 0
            setFlags(carry = value.getBit(0))
            val result = ((value.toInt() shl 1) + newLowBit).toUByte()
            store.invoke(result)
            tick()
        }
    }

    private inline fun rlc(store: (UByte) -> Unit, read: () -> UByte) {
        rlc(store, read.invoke())
    }

    private inline fun rlc(store: (UByte) -> Unit, value: UByte) {
        with(registers) {
            setFlags(carry = value.getBit(7))
            val newLowBit = if (carry) 1 else 0
            val result = ((value.toInt() shl 1) + newLowBit).toUByte()
            store.invoke(result)
            tick()
        }
    }

    private fun reti() {
        ret()
        mmu.interrupts.interruptsEnabled = true
    }

    private inline fun ret(condition: () -> Boolean): UShort? {
        registers.tick()
        return if (condition.invoke()) {
            ret()
        } else {
            registers.tick()
            null
        }
    }

    private fun ret(): UShort {
        val lowerByte = readFromMemory(registers.sp).invoke()
        val upperByte = readFromMemory(registers.sp + 1u).invoke()
        registers.sp = (registers.sp + 2u).toUShort()
        registers.tick(2)
        return createUShort(upperByte, lowerByte)
    }

    private fun push(short: UShort) {
        registers.sp = (registers.sp - 2u).toUShort()
        storeWordInMemory(registers.sp).invoke(short)
        registers.tick(2)
    }

    private inline fun pop(destination: (UShort) -> Unit) {
        val result = readWordFromMemory(registers.sp).invoke()
        destination.invoke(result)
        registers.sp = (registers.sp + 2u).toUShort()
    }

    private inline fun or(source: () -> UByte) {
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

    private fun createSetOperations(startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = CBOperation("SET $bitIndex,(HL)") { set(bitIndex, storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[startIndex + 1] = CBOperation("SET $bitIndex,A") { set(bitIndex, storeInRegisterA(), { registers.a }) }
        cbOperations[startIndex - 6] = CBOperation("SET $bitIndex,B") { set(bitIndex, storeInRegisterB(), { registers.b }) }
        cbOperations[startIndex - 5] = CBOperation("SET $bitIndex,C") { set(bitIndex, storeInRegisterC(), { registers.c }) }
        cbOperations[startIndex - 4] = CBOperation("SET $bitIndex,D") { set(bitIndex, storeInRegisterD(), { registers.d }) }
        cbOperations[startIndex - 3] = CBOperation("SET $bitIndex,E") { set(bitIndex, storeInRegisterE(), { registers.e }) }
        cbOperations[startIndex - 2] = CBOperation("SET $bitIndex,H") { set(bitIndex, storeInRegisterH(), { registers.h }) }
        cbOperations[startIndex - 1] = CBOperation("SET $bitIndex,L") { set(bitIndex, storeInRegisterL(), { registers.l }) }
    }

    private fun createResOperations(startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = CBOperation("RES $bitIndex,(HL)") { res(bitIndex, storeInMemory(registers.hl), readFromMemory(registers.hl)) }
        cbOperations[startIndex + 1] = CBOperation("RES $bitIndex,A") { res(bitIndex, storeInRegisterA(), { registers.a }) }
        cbOperations[startIndex - 6] = CBOperation("RES $bitIndex,A") { res(bitIndex, storeInRegisterB(), { registers.b }) }
        cbOperations[startIndex - 5] = CBOperation("RES $bitIndex,B") { res(bitIndex, storeInRegisterC(), { registers.c }) }
        cbOperations[startIndex - 4] = CBOperation("RES $bitIndex,C") { res(bitIndex, storeInRegisterD(), { registers.d }) }
        cbOperations[startIndex - 3] = CBOperation("RES $bitIndex,D") { res(bitIndex, storeInRegisterE(), { registers.e }) }
        cbOperations[startIndex - 2] = CBOperation("RES $bitIndex,H") { res(bitIndex, storeInRegisterH(), { registers.h }) }
        cbOperations[startIndex - 1] = CBOperation("RES $bitIndex,L") { res(bitIndex, storeInRegisterL(), { registers.l }) }
    }

    private fun createBitOperations(startIndex: Int, bitIndex: Int) {
        cbOperations[startIndex] = CBOperation("BIT $bitIndex,(HL)") { bit(bitIndex, readFromMemory(registers.hl)) }
        cbOperations[startIndex + 1] = CBOperation("BIT $bitIndex,A") { bit(bitIndex, registers.a) }
        cbOperations[startIndex - 6] = CBOperation("BIT $bitIndex,B") { bit(bitIndex, registers.b) }
        cbOperations[startIndex - 5] = CBOperation("BIT $bitIndex,C") { bit(bitIndex, registers.c) }
        cbOperations[startIndex - 4] = CBOperation("BIT $bitIndex,D") { bit(bitIndex, registers.d) }
        cbOperations[startIndex - 3] = CBOperation("BIT $bitIndex,E") { bit(bitIndex, registers.e) }
        cbOperations[startIndex - 2] = CBOperation("BIT $bitIndex,H") { bit(bitIndex, registers.h) }
        cbOperations[startIndex - 1] = CBOperation("BIT $bitIndex,L") { bit(bitIndex, registers.l) }
    }

    private inline fun jr(offset: () -> UByte, condition: Boolean = true): UShort? {
        // Two things:
        // JR uses a *signed* jump, to allow for backwards jumps. So we have to convert to bytes first.
        // Add two for the instruction bytes - not 100% sure why only here and not jp() though.
        val address = (registers.pc.toShort() + offset.invoke().toByte() + 2).toUShort()
        registers.tick()
        return if (condition) {
            registers.tick()
            return address
        } else {
            null
        }
    }

    private fun jp(destination: UShort): UShort {
        registers.tick()
        return destination
    }

    private inline fun jp(source: () -> UShort, condition: Boolean = true): UShort? {
        val destination = source.invoke()
        registers.tick()
        return if (condition) {
            registers.tick()
            destination
        } else {
            null
        }
    }

    private inline fun inc(save: (UByte) -> Unit, source: () -> UByte) {
        with(registers) {
            val value = source.invoke()
            val result = value + 1u
            setFlags(zero = result and 0xFFu == 0u, halfCarry = halfCarry(value, 1u))
            save.invoke(result.toUByte())
            tick()
        }
    }

    private fun halfCarry(a: UByte, b: UByte) = (a and 0xfu) + (b and 0xfu) > 0xfu

    private fun halfCarry(a: UShort, b: UShort) = (a and 0xfffu) + (b and 0xfffu) > 0xfffu

    private fun halfCarrySub(a: UByte, b: UByte) = (a and 0xfu).toByte() - (b and 0xfu).toByte() < 0

    private fun halfCarrySub(a: UShort, b: UShort) = (a and 0xfffu).toShort() - (b and 0xfffu).toShort() < 0


    @JvmName("incWord")
    private inline fun inc(save: (UShort) -> Unit, source: () -> UShort) {
        val short = source.invoke()
        val result = short + 1u
        registers.setFlags(zero = result and 0xFFFFu == 0u, halfCarry = halfCarry(short, 1u))
        save.invoke(result.toUShort())
    }

    private fun halt() {
        registers.halted = true
        registers.tick()
    }

    private fun di() {
        mmu.interrupts.interruptsEnabled = false
        registers.tick()
    }

    private fun ei() {
        mmu.interrupts.interruptsEnabled = true
        registers.tick()
    }

    private inline fun dec(save: (UByte) -> Unit, source: () -> UByte) {
        with(registers) {
            val short = source.invoke()
            val result = short - 1u
            setFlags(zero = result and 0xFFu == 0u, subtract = true, halfCarry = halfCarrySub(short, 1u))
            save.invoke(result.toUByte())
            tick()
        }
    }

    @JvmName("decWord")
    private inline fun dec(save: (UShort) -> Unit, source: () -> UShort) {
        with(registers) {
            val short = source.invoke()
            val result = short - 1u
            setFlags(zero = result and 0xFFu == 0u, subtract = true, halfCarry = halfCarrySub(short, 1u))
            save.invoke(result.toUShort())
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

    private inline fun cp(byte: () -> UByte) {
        cp(byte.invoke())
    }

    // Fake subtraction - doesn't store result, but does set flags
    private fun cp(byte: UByte) {
        with(registers) {
            val result = a - byte
            setFlags(zero = result and 0xFFu == 0u, subtract = true, carry = byte > a, halfCarry = halfCarrySub(a, byte))
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

    private inline fun call(address: () -> UShort, conditional: Boolean = true): UShort? {
        with(registers) {
            tick()
            val destination = address.invoke()
            return if (conditional) {
                sp = (sp - 2u).toUShort()
                tick(3)
                return destination
            } else {
                null
            }
        }
    }

    private inline fun set(index: Int, save: (UByte) -> Unit, load: () -> UByte) {
        val mask = (1 shl index).toUByte()
        val result = load.invoke() or mask
        save.invoke(result)
        registers.tick()
    }

    private inline fun res(index: Int, save: (UByte) -> Unit, load: () -> UByte) {
        val mask = (1 shl index).toUByte()
        val result = load.invoke() xor mask
        save.invoke(result)
        registers.tick()
    }

    private inline fun bit(index: Int, source: () -> UByte) {
        registers.tick()
        return bit(index, source.invoke())
    }

    private fun bit(index: Int, source: UByte) {
        val bit = source and (1 shl index).toUByte()
        registers.setFlags(zero = bit == 0u.toUByte(), halfCarry = true)
        registers.tick()
    }

    private fun readFromMemory(address: UInt): () -> UByte {
        assert(address <= 0xFFFFu)
        return readFromMemory(address.toUShort())
    }

    private fun readFromHighMemory(address: UByte): () -> UByte = {
        registers.tick()
        mmu[0xFF00u + address]
    }

    private fun readFromMemory(address: UShort): () -> UByte = {
        registers.tick()
        mmu[address]
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
    private fun storeInRegisterAF(): (UShort) -> Unit = { v -> registers.af = v; registers.tick() }
    private fun storeInRegisterBC(): (UShort) -> Unit = { v -> registers.bc = v; registers.tick() }
    private fun storeInRegisterDE(): (UShort) -> Unit = { v -> registers.de = v; registers.tick() }
    private fun storeInRegisterHL(): (UShort) -> Unit = { v -> registers.hl = v; registers.tick() }
    private fun storeInRegisterSP(): (UShort) -> Unit = { v -> registers.sp = v; registers.tick() }
    private fun readFromRegisterBC(): () -> UShort = { registers.tick(); registers.bc }
    private fun readFromRegisterDE(): () -> UShort = { registers.tick(); registers.de }
    private fun readFromRegisterHL(): () -> UShort = { registers.tick(); registers.hl }
    private fun readFromRegisterSP(): () -> UShort = { registers.tick(); registers.sp }

    private fun storeInMemory(address: UShort) = { value: UByte ->
        mmu[address] = value
        registers.tick()
    }

    private fun storeInMemory(indirectAddress: UInt): (UByte) -> Unit {
        assert(indirectAddress <= 0xFFFFu) { "Address ${indirectAddress.hex()} is out of range" }
        return storeInMemory(indirectAddress.toUShort())
    }

    private fun storeInHighMemory(offset: UByte) = { value: UByte ->
        mmu[0xFF00u + offset] = value
        registers.tick()
    }

    private fun storeWordInMemory(address: UShort) = { value: UShort ->
        mmu[address + 1u] = value.upperByte
        mmu[address] = value.lowerByte
        registers.tick(2)
    }

    private inline fun load(destination: (UShort) -> Unit, source: UShort) {
        destination.invoke(source)
        registers.tick(1)
    }

    @JvmName("load16")
    private inline fun load(save: (UShort) -> Unit, load: () -> UShort) {
        val short = load.invoke()
        save.invoke(short)
    }

    private inline fun loadSigned(save: (UShort) -> Unit, load: () -> Int) {
        val short = load.invoke().toUShort()
        save.invoke(short)
        registers.tick()
    }

    private inline fun load(save: (UByte) -> Unit, load: () -> UByte, then: () -> Unit = {}) {
        val byte = load.invoke()
        save.invoke(byte)
        then.invoke()
        registers.tick()
    }

    private inline fun load(save: (UByte) -> Unit, value: UByte, then: () -> Unit = {}) {
        save.invoke(value)
        then.invoke()
        registers.tick()
    }

    private inline fun adcA(source: () -> UByte) {
        return adcA(source.invoke())
    }

    private fun adcA(byte: UByte) {
        with(registers) {
            val result = a + byte + if (registers.carry) 1u else 0u
            setFlags(zero = result and 0xFFu == 0u, carry = result > 0xFFu, halfCarry = halfCarry(a, (byte + 1u).toUByte()))
            a = result.toUByte()
            tick()
        }
    }

    private fun nop() {
        registers.tick()
    }

    private inline fun addA(source: () -> UByte) {
        return addA(source.invoke())
    }

    private fun addA(byte: UByte) {
        with(registers) {
            val result = a + byte
            setFlags(zero = result and 0xFFu == 0u, carry = result > 0xFFu, halfCarry = halfCarry(a, byte))
            a = result.toUByte()
            tick()
        }
    }

    private fun addHL(short: UShort) {
        with(registers) {
            val result = hl + short
            setFlags(zero = result and 0xFFFFu == 0u, carry = result > 0xFFFFu, halfCarry = halfCarry(hl, short))
            hl = result.toUShort()
            tick(2)
        }
    }

    private inline fun andA(source: () -> UByte) {
        return andA(source.invoke())
    }

    private fun andA(byte: UByte) {
        with(registers) {
            a = a and byte
            if (a == 0u.toUByte()) zero = true
            tick()
        }
    }

    private inline fun addSP(signedOffset: () -> UByte) {
        with(registers) {
            val offset = signedOffset.invoke().toByte()
            val result = (sp.toInt() + offset)
            setFlags(zero = result and 0xFFFF == 0, carry = result > 0xFFFF, halfCarry = (sp.toInt() and 0xf) + (offset and 0xf) > 0xf)
            hl = result.toUShort()
            tick(3)
        }
    }

    companion object {
        const val CB_OPCODE: UByte = 0xcbu
    }
}