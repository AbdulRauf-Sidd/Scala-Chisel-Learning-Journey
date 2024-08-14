package single_cycle

import chisel3._
import chisel3.util._

class Instruction_Memory extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(20.W))
        val out = Output(UInt(32.W))
    })

    val regs = Reg(Vec(2000, UInt(32.W)))
    regs(0) := "b00000000100100000000000000010011".U
    regs(1) := "b00000000010000000000000010010011".U
    regs(2) := "b00000000000100000010000000100011".U
    regs(3) := "b00000000000000000010001000000011".U
    regs(4) := "b00000000011100100000001100010011".U
    io.out := regs(io.in)
}