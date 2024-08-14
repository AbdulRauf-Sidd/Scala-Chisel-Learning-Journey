package pipelined
import chisel3._
import chisel3.util._

class Program_Counter extends Module {
    val io = IO (new Bundle {
        val in = Input(UInt(32.W))
        val reset = Input(Bool())
        val out = Output(UInt(32.W))
    })

    when (io.reset) {
        io.out := 0.U
    }.otherwise {
        io.out := io.in
    }
}