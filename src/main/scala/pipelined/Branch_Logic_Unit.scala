package pipelined
import chisel3._
import chisel3.util._

class Branch_Logic_Unit extends  Module {
    val io = IO(new Bundle {
        val rs1_in = Input(SInt(32.W))
        val rs2_in = Input(SInt(32.W))
        val funct3_in = Input(UInt(3.W))

        val out = Output(UInt(2.W))
        
    })

    val wire = WireInit(0.B)

    when (io.funct3_in === 0.U) {
        wire := io.rs1_in === io.rs2_in
    }.elsewhen (io.funct3_in === 1.U) {
        wire := io.rs1_in =/= io.rs2_in
    }.elsewhen (io.funct3_in === 2.U) {
        wire := io.rs1_in > io.rs2_in
    }.elsewhen (io.funct3_in === 3.U) {
        wire := io.rs1_in.asUInt > io.rs2_in.asUInt
    }.elsewhen (io.funct3_in === 4.U) {
        wire := io.rs1_in < io.rs2_in
    }.elsewhen (io.funct3_in === 5.U) {
        wire := io.rs1_in >= io.rs2_in
    }.elsewhen (io.funct3_in === 6.U) {
        wire := io.rs1_in.asUInt < io.rs2_in.asUInt
    }.elsewhen (io.funct3_in === 7.U) {
        wire := io.rs1_in.asUInt >= io.rs2_in.asUInt
    }

    io.out := wire
    
    
}