package pipelined

import chisel3._
import chisel3.util._

class PC_Adder extends Module {
    val io = IO (new Bundle {
        val in = Input(UInt(32.W))
        val out = Output(UInt(32.W))
    })
    io.out := io.in + 4.U
}