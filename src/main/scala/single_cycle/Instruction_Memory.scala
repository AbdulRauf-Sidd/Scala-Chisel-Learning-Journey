package single_cycle

import chisel3._
import chisel3.util._

class Instruction_Memory extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(20.W))
        val out = Output(UInt(32.W))
    })

    val regs = Reg(Vec(2000, UInt(32.W)))
    io.out := regs(io.in)
}