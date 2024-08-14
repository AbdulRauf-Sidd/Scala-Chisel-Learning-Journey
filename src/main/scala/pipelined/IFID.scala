package pipelined
import chisel3._
import chisel3.util._

class IFID_Reg extends  Module {
    val io = IO(new Bundle {
        val pc_in = Input(UInt(32.W))
        val pc_4_in = Input(UInt(32.W))
        val inst_in = Input(UInt(32.W))
        val pc_out = Output(UInt(32.W))
        val pc_4_out = Output(UInt(32.W))
        val inst_out = Output(UInt(32.W))
    })

    io.pc_out := RegNext(io.pc_in)
    io.pc_4_out := RegNext(io.pc_4_in)
    io.inst_out := RegNext(io.inst_in)
}