package single_cycle

import chisel3._
import chisel3.util._

class Processor extends Module {
    val io = IO(new Bundle {
        val reset = Input(Bool())
        val reg_zero = Input(Bool())
    })

    val pc = Module(new Program_Counter)
    val IM = Module(new Instruction_Memory)
    val pc_adder = Module(new PC_Adder)
    val IG = Module(new Immediate_Generation)
    val alu_control = Module(new ALUControl)
    val control_unit = Module(new Control_Unit)
    val register = Module(new RegisterFile)
    val ALU = Module(new ALU)
    val RAM = Module(new RAM)


    val pc_value = WireInit(0.U(32.U))

    //PC
    pc.io.reset := reset
    pc.io.in := RegNext(pc_value, 0.U)

    //PC Adder
    pc_adder.io.in := pc.io.out
    val pc_add_val = pc_adder.io.out
    

    //Instruction Mem
    IM.io.in := pc.io.out(21, 2)
    val ins = IM.io.out

    //Control Unit
    control_unit.io.in := ins(6, 0)
    val memwrite = control_unit.io.memWrite
    val branch = control_unit.io.branch
    val memread = control_unit.io.memRead
    val regwrite = control_unit.io.regWrite
    val memtoreg = control_unit.io.memtoReg
    val aluoperation = control_unit.io.aluOperation
    val operandA_sel = control_unit.io.operand_A_sel
    val operandB_sel = control_unit.io.operand_B_sel
    val extend_sel = control_unit.io.extend_sel
    val next_PC_sel = control_unit.io.next_PC_sel
    

    //Immediate Gen
    IG.io.inst := ins
    IG.io.pc := pc.io.out

    val IG_Mux = WireInit(0.S(32.W))

    when (extend_sel === 0.U) {
        IG_Mux := IG.io.IType
    }.elsewhen (extend_sel === 1.U) {
        IG_Mux := IG.io.SType
    }.elsewhen( extend_sel === 2.U) {
        IG_Mux := IG.io.UType
    }

    //ALU Control
    alu_control.io.ALUOp := aluoperation
    alu_control.io.funct3 := ins(14, 12)
    alu_control.io.funct7 := ins(30)


    //Register File
    register.io.read1 := ins(19, 15)
    register.io.read2 := ins(24, 20)
    register.io.regwrite := regwrite
    register.io.writereg := ins(11, 7)
    register.io.clear := io.reg_zero


    val alu1 = WireInit(0.S(32.W))
    val alu2 = WireInit(0.S(32.W))

    when (operandA_sel === 0.U) {
        alu1 := register.io.readdata1
    }.elsewhen (operandA_sel === 1.U) {
        alu1 := pc_add_val.asSInt
    }.elsewhen (operandA_sel === 2.U) {
        alu1 := pc.io.out.asSInt
    }.otherwise {
        alu1 := register.io.readdata1
    }

    when (operandB_sel === 0.U) {
        alu2 := register.io.readdata2
    }.otherwise {
        alu2 := IG_Mux
    }



    //ALU
    ALU.io.in1 := alu1
    ALU.io.in2 := alu2
    ALU.io.alu_control := alu_control.io.outputSignal

    val and1 = ALU.io.branch && control_unit.io.branch
    val pc_imm = register.io.readdata1 + IG_Mux

    val pc_mux1 = Mux(and1, IG.io.SBType, pc_add_val.asSInt)
    val pc_mux2 = WireInit(0.S(32.W))

    when (control_unit.io.next_PC_sel === 0.U) {
        pc_mux2 := pc_add_val.asSInt
    }.elsewhen (control_unit.io.next_PC_sel === 1.U) {
        pc_mux2 := pc_mux1
    }.elsewhen (control_unit.io.next_PC_sel === 2.U) {
        pc_mux2 := IG.io.UJType
    }.otherwise {
        pc_mux2 := pc_imm
    }

    pc_value := pc_mux2.asUInt
    // pc.io.in := pc_mux2.asUInt

    //RAM
    RAM.io.Addr := ALU.io.output(9, 2)
    RAM.io.wr_en := control_unit.io.memWrite
    RAM.io.rd_en := control_unit.io.memRead
    RAM.io.wrData := register.io.readdata2

    register.io.writedata := Mux(memtoreg, RAM.io.out, ALU.io.output)
}