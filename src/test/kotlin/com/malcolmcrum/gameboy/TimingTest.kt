package com.malcolmcrum.gameboy

import assertk.assertThat
import assertk.assertions.contains
import com.malcolmcrum.gameboy.emulator.Joypad
import com.malcolmcrum.gameboy.emulator.MMU
import com.malcolmcrum.gameboy.emulator.Operations
import com.malcolmcrum.gameboy.emulator.Registers
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
internal class TimingTest {
    val registers = Registers()
    val mmu = MMU(joypad = Joypad())
    val operations = Operations(registers, mmu)

    @BeforeEach
    fun `reset registers`() {
        registers.reset()
        mmu[0x100u] // exit bios mode
        registers.c = 0xFFu
        registers.sp = 0x0010u
        mmu[0x0001u] = 0xFFu
    }

    @ParameterizedTest
    @MethodSource
    fun unprefixed(op: OpCode) {
        val code = Integer.decode(op.addr)
        if (code == 0xcb) return // 0xcb prefixed codes are tested in `CB prefixed` test
        mmu[0x0000u] = code.toUByte()
        val operation= operations[0x00u]
        operation.invoke(0u)
        assertThat(op.cycles, "${op.addr}: ${operation.mnemonic}").contains(registers.t.toInt())
    }

    fun unprefixed(): Collection<OpCode> {
        val json = Files.readAllBytes(file).toString(Charset.defaultCharset())
        val opcodes = Json.parse<OpCodes>(json)
        return opcodes.unprefixed.values
    }

    @ParameterizedTest
    @MethodSource
    fun `CB prefixed`(op: OpCode) {
        val code = Integer.decode(op.addr)
        mmu[0x00u] = 0xcbu
        mmu[0x01u] = code.toUByte()
        val operation = operations[0x00u]
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
)
