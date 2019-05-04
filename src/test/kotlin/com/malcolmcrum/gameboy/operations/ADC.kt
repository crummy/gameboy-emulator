package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class ADC : OperationTest(0xce) {
    override fun parameters(): Stream<Arguments> = Stream.of(
        Arguments.arguments(State(a = 0u), State(a = 0u), listOf(0x00)),
        Arguments.arguments(State(a = 0u), State(a = 1u), listOf(0x01)),
        Arguments.arguments(State(a = 1u), State(a = 1u), listOf(0x00)),
        Arguments.arguments(State(a = 0xFFu), State(a = 0u, f = Registers.CARRY_FLAG or Registers.ZERO_FLAG), listOf(0x01))
    )
}