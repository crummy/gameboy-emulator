package com.malcolmcrum.gameboy.operations

import com.malcolmcrum.gameboy.Registers.Companion.CARRY_FLAG
import com.malcolmcrum.gameboy.Registers.Companion.ZERO_FLAG
import com.malcolmcrum.gameboy.utils.State
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import java.util.stream.Stream
import java.util.stream.Stream.of

@ExperimentalUnsignedTypes
class ADC : OperationTest(0xce) {
    override fun parameters(): Stream<Arguments> = of(
            arguments(State(a = 0u), State(a = 0u), listOf(0x00)),
            arguments(State(a = 0u), State(a = 1u), listOf(0x01)),
            arguments(State(a = 1u, f = CARRY_FLAG), State(a = 2u), listOf(0x00)),
            arguments(State(a = 0xFFu), State(a = 0u, f = CARRY_FLAG or ZERO_FLAG), listOf(0x01))
    )
}

@ExperimentalUnsignedTypes
class ADC_A_B : OperationTest(0x88) {
    override fun parameters(): Stream<Arguments> = of(
            arguments(State(a = 0u, b = 0u), State(a = 0u, b = 0u), listOf<Int>()),
            arguments(State(a = 1u, b = 3u), State(a = 4u, b = 3u), listOf<Int>()),
            arguments(State(a = 1u, f = CARRY_FLAG), State(a = 2u), listOf<Int>()),
            arguments(State(a = 0xFFu), State(a = 0xFFu), listOf<Int>())
    )

}