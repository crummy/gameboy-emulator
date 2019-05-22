package com.malcolmcrum.gameboy

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.malcolmcrum.gameboy.emulator.Joypad
import com.malcolmcrum.gameboy.emulator.MMU
import com.malcolmcrum.gameboy.emulator.Operations
import com.malcolmcrum.gameboy.emulator.Registers
import com.malcolmcrum.gameboy.emulator.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.HALF_CARRY_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.SUBTRACT_FLAG
import com.malcolmcrum.gameboy.emulator.Registers.Companion.ZERO_FLAG
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

@ExperimentalUnsignedTypes
@ImplicitReflectionSerializer
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// TODO separate into FlagTest and TimingTest
internal class TimingTest {
    val registers = Registers()
    val mmu = MMU(joypad = Joypad()).apply { inBios = false }
    val operations = Operations(registers, mmu)

    @BeforeEach
    fun `reset registers`() {
        registers.reset()
        registers.c = 0xFFu
        registers.sp = 0x0010u
        mmu[0x0001u] = 0xFFu
    }

    @ParameterizedTest
    @MethodSource("unprefixed")
    // This is not a complete test. It only validates untouched flags and flags that are always set.
    fun `set flags, from 0xff`(op: OpCode) {
        val code = Integer.decode(op.addr)
        mmu[0x0000u] = code.toUByte()
        val (_, operation) = operations[0x00u]

        registers.f = 0xffu
        operation.invoke(0u)
        checkFlag("Z", op.ZERO, 0xffu, ZERO_FLAG)
        checkFlag("N", op.SUBTRACT, 0xffu, SUBTRACT_FLAG)
        checkFlag("H", op.HALF_CARRY, 0xffu, HALF_CARRY_FLAG)
        checkFlag("C", op.CARRY, 0xffu, CARRY_FLAG)
    }

    @ParameterizedTest
    @MethodSource("unprefixed")
    // This is not a complete test. It only validates untouched flags and flags that are always set.
    fun `set flags, from 0x00`(op: OpCode) {
        val code = Integer.decode(op.addr)
        mmu[0x0000u] = code.toUByte()
        val (_, operation) = operations[0x00u]

        registers.f = 0x0u
        operation.invoke(0u)
        checkFlag("Z", op.ZERO, 0u, ZERO_FLAG)
        checkFlag("N", op.SUBTRACT, 0u, SUBTRACT_FLAG)
        checkFlag("H", op.HALF_CARRY, 0u, HALF_CARRY_FLAG)
        checkFlag("C", op.CARRY, 0u, CARRY_FLAG)
    }

    private fun checkFlag(key: String, flag: String, initialValue: UByte, mask: UByte) {
        when (flag) {
            "-" -> assertThat(registers.f and mask, key).isEqualTo(initialValue and mask)
            "1" -> assertThat(registers.f and mask, key).isGreaterThan(0u.toUByte())
            "0" -> assertThat(registers.f and mask, key).isEqualTo(0u.toUByte())
            key -> null // no assertion possible without knowing inputs of calculation
        }
    }

    @ParameterizedTest
    @MethodSource
    fun unprefixed(op: OpCode) {
        val code = Integer.decode(op.addr)
        mmu[0x0000u] = code.toUByte()
        val (_, operation) = operations[0x00u]
        operation.invoke(0u)
        assertThat(op.cycles, "${op.addr}: ${operation.mnemonic}").contains(registers.t.toInt())
    }

    fun unprefixed(): Collection<OpCode> {
        val json = Files.readAllBytes(file).toString(Charset.defaultCharset())
        val opcodes = Json.parse<OpCodes>(json)
        return opcodes.unprefixed.values.filter { it.addr != "0xcb" }
    }

    @ParameterizedTest
    @MethodSource
    fun `CB prefixed`(op: OpCode) {
        val code = Integer.decode(op.addr)
        mmu[0x00u] = 0xcbu
        mmu[0x01u] = code.toUByte()
        val (_, operation) = operations[0x00u]
        operation.invoke(0u)
        assertThat(op.cycles,"${op.addr}: ${operation.mnemonic}").contains(registers.t.toInt())
    }

    fun `CB prefixed`(): Collection<OpCode> {
        val json = Files.readAllBytes(file).toString(Charset.defaultCharset())
        val opcodes = Json.parse<OpCodes>(json)
        return opcodes.cbprefixed.values
    }

    companion object {
        val file = Paths.get("src/test/resources/opcodes.json")!!
    }
}

@Serializable
data class OpCodes(
        val unprefixed: Map<String, OpCode>,
        val cbprefixed: Map<String, OpCode>
)

@Serializable
data class OpCode(
        val mnemonic: String,
        val length: Int,
        val cycles: List<Int>,
        val flags: List<String>,
        val addr: String,
        val operand1: String? = null,
        val operand2: String? = null
) {
    val ZERO = flags[0]
    val SUBTRACT = flags[1]
    val HALF_CARRY = flags[2]
    val CARRY = flags[3]
}