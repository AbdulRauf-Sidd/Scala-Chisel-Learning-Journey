package pipelined
import chisel3._
import chisel3.util._

class Hazard_Detection_Unit extends  Module {
    val io = IO(new Bundle {
        val inst_in = Input(UInt(32.W))
        val memRead_in = Input(Bool())
        val rd_sel_in = Input(UInt(5.W))
        val current_pc_in = Input(UInt(32.W))
        val pc_in = Input(UInt(32.W))

        val inst_out = Output(UInt(32.W))
        val ctrl_fwd_out = Output(Bool())
        val inst_fwd_out = Output(Bool())
        val pc_fwd_out = Output(Bool())
        val pc_out = Output(UInt(32.W))
        val current_pc_out = Output(UInt(32.W)) 
    })
    
    val rs1_sel = io.inst_in(19, 15).asUInt
    val rs2_sel = io.inst_in(24, 20).asUInt

    when (io.memRead_in && (io.rd_sel_in === rs1_sel || io.rd_sel_in === rs2_sel)) {
        io.ctrl_fwd_out := 1.B
        io.inst_fwd_out := 1.B
        io.pc_fwd_out := 1.B
    }.otherwise {
        io.ctrl_fwd_out := 0.B
        io.inst_fwd_out := 0.B
        io.pc_fwd_out := 0.B
    }

    io.inst_out := io.inst_in
    io.pc_out := io.pc_in
    io.current_pc_out := io.current_pc_in
}