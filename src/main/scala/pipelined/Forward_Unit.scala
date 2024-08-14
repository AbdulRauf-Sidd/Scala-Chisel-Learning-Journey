package pipelined
import chisel3._
import chisel3.util._

class Forward_Unit extends  Module {
    val io = IO(new Bundle {
        val ex_regWrite_in = Input(Bool())
        val mem_regWrite_in = Input(Bool())
        val ex_rd_sel_in = Input(UInt(5.W))
        val mem_rd_sel_in = Input(UInt(5.W))
        val ex_memRead = Input(Bool())
        val rs1_sel_in = Input(UInt(5.W))
        val rs2_sel_in = Input(UInt(5.W))

        val forward_a_out = Output(UInt(2.W))
        val forward_b_out = Output(UInt(2.W))

    })
    
    val ex_hazard1 = WireInit(0.B)
    val ex_hazard2 = WireInit(0.B)
    val out1 = WireInit(0.U(2.W))
    val out2 = WireInit(0.U(2.W))

    when (io.ex_regWrite_in) {
        when(io.ex_memRead) {
            when (io.ex_rd_sel_in === io.rs1_sel_in && io.ex_rd_sel_in === io.rs2_sel_in) {
                out1 := 3.U
                out2 := 3.U
                ex_hazard1 := 1.B
                ex_hazard2 := 1.B
            }.elsewhen (io.ex_rd_sel_in === io.rs1_sel_in) {
                out1 := 3.U
                ex_hazard1 := 1.B
            }.elsewhen (io.ex_rd_sel_in === io.rs2_sel_in) {
                out2 := 3.U
                ex_hazard2 := 1.B
            }
        }.otherwise {
            when (io.ex_rd_sel_in === io.rs1_sel_in && io.ex_rd_sel_in === io.rs2_sel_in) {
                out1 := 1.U
                out2 := 1.U
                ex_hazard1 := 1.B
                ex_hazard2 := 1.B
            }.elsewhen (io.ex_rd_sel_in === io.rs1_sel_in) {
                out1 := 1.U
                ex_hazard1 := 1.B
            }.elsewhen (io.ex_rd_sel_in === io.rs2_sel_in) {
                out2 := 1.U
                ex_hazard2 := 1.B
            }
        }
        
    }

    when (io.mem_regWrite_in) {
        when (~ex_hazard1 && ~ex_hazard2 && io.mem_rd_sel_in === io.rs1_sel_in && io.mem_rd_sel_in === io.rs2_sel_in) {
            out1 := 2.U
            out2 := 2.U
        }.elsewhen (~ex_hazard1 && io.mem_rd_sel_in === io.rs1_sel_in) {
            out1 := 2.U
        }.elsewhen (~ex_hazard2 && io.mem_rd_sel_in === io.rs2_sel_in) {
            out2 := 2.U
        }
    }
    io.forward_a_out := out1
    io.forward_b_out := out2
}