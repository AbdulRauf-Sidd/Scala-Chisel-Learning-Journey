package pipelined
import chisel3._
import chisel3.util._

class Structural_Hazard_Detection_Unit extends  Module {
    val io = IO(new Bundle {
        val rs1_sel = Input(UInt(5.W))
        val rs2_sel = Input(UInt(5.W))
        val mem_rd_sel = Input(UInt(5.W))
        val mem_regWrite = Input(Bool())

        val fwd1 = Output(Bool())
        val fwd2 = Output(Bool())
    })

    val wire1 = WireInit(0.B)
    val wire2 = WireInit(0.B)

    when (io.mem_regWrite) {
        when (io.mem_rd_sel === io.rs1_sel && io.mem_rd_sel === io.rs2_sel) {
            wire1 := 1.B
            wire2 := 1.B
        }.elsewhen (io.mem_rd_sel === io.rs1_sel) {
            wire1 := 1.B
        }.elsewhen (io.mem_rd_sel === io.rs2_sel) {
            wire2 := 1.B
        }
    }

    io.fwd1 := wire1
    io.fwd2 := wire2
}