package single_cycle

import chisel3._
import chisel3.util._

class Control_Unit extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(7.W))
        val memWrite = Output(Bool())
        val branch = Output(Bool())
        val memRead = Output(Bool())
        val regWrite = Output(Bool())
        val memtoReg = Output(Bool())
        val aluOperation = Output(UInt(3.W))
        val operand_A_sel = Output(UInt(2.W))
        val operand_B_sel = Output(Bool())
        val extend_sel = Output(UInt(2.W))
        val next_PC_sel = Output(UInt(2.W)) 
    })
    val rformat = WireInit(0.B)
    val load = WireInit(0.B)
    val store = WireInit(0.B)
    val branch = WireInit(0.B)
    val itype = WireInit(0.B)
    val jalr = WireInit(0.B)
    val jal = WireInit(0.B)
    val lui = WireInit(0.B)

    when (io.in === 51.U) {
        rformat := 1.B
    }.elsewhen (io.in === 3.U) {
        load := 1.B
    }.elsewhen (io.in === 19.U) {
        itype := 1.B
    }.elsewhen (io.in === 103.U) {
        jalr := 1.B
    }.elsewhen (io.in === 111.U) {
        jal := 1.B
    }.elsewhen (io.in === 55.U) {
        lui := 1.B
    }.elsewhen (io.in === 35.U) {
        store := 1.B
    }.elsewhen (io.in === 99.U) {
        branch := 1.B
    }

    val alu_bit2 = (~rformat && ~branch && ~itype && ~jalr && ~jal) 
    val alu_bit1 = (~branch && ~load && ~store && ~itype)
    val alu_bit0 = (~rformat && ~load && ~branch && ~lui)

    val op_a_bit1 = ((~lui && ~jalr && jal) || (~lui && jalr && ~jalr) || (lui && ~jalr && ~jal))
    val op_a_bit0 = (lui && ~jalr && ~jal)
    
    val pc_sel_bit1 = ((~branch && ~jal && jalr) || (~branch && jal && ~jalr))
    val pc_sel_bit0 = ((~branch && ~jal && jalr) || (branch && ~jal && ~jalr))
    
    io.memWrite := store
    io.branch := branch
    io.memRead := load
    io.regWrite := rformat | load | itype | jalr | jal | lui
    io.memtoReg := load
    io.aluOperation := Cat(alu_bit2, alu_bit1, alu_bit0)
    io.operand_A_sel := Cat(op_a_bit1, op_a_bit0)
    io.operand_B_sel := (lui || itype || store || load)
    io.extend_sel := Cat(store, lui)
    io.next_PC_sel := Cat(pc_sel_bit1, pc_sel_bit0)

}