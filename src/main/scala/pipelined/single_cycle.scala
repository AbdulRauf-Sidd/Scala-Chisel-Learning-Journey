package pipelined

import chisel3._
import chisel3.util._
import java.time.chrono.ThaiBuddhistDate

class Processor extends Module {
    val io = IO(new Bundle {
        val reset = Input(Bool())
        val reg_zero = Input(Bool())
        val pc_value = Output(UInt())
        val writeback = Output(SInt())
        val destination_address = Output(UInt())    
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

    val ifid = Module(new IFID_Reg)
    val idex = Module(new IDEX)
    val exmem = Module(new EXMEM)
    val memwb = Module(new MEMWB)

    val hd = Module(new Hazard_Detection_Unit)
    val bf = Module(new Branch_Forward_Unit)
    val bl = Module(new Branch_Logic_Unit)
    val sd = Module(new Structural_Hazard_Detection_Unit)
    val fu = Module(new Forward_Unit)

    //HD mux control wires
    val hd_wire_inst_out = WireInit(0.U(32.W))
    val hd_wire_pc_out = WireInit(0.U(32.W))
    val hd_wire_current_pc_out = WireInit(0.U(32.W))
    val hd_wire_ctrl_fwd_out = WireInit(0.B)
    val hd_wire_inst_fwd_out = WireInit(0.B)
    val hd_wire_pc_fwd_out = WireInit(0.B)


    //BF mux control wires
    val bf_wire_id_rdsel = WireInit(0.U(5.W))
    val bf_wire_id_memRead = WireInit(0.B)
    val bf_wire_ex_rdsel = WireInit(0.U(5.W))
    val bf_wire_ex_memRead = WireInit(0.B)
    val bf_wire_mem_rdsel = WireInit(0.U(5.W))
    val bf_wire_mem_memRead = WireInit(0.B)

    val bf_wire_fwd1_out = WireInit(0.U(4.W))
    val bf_wire_fwd2_out = WireInit(0.U(3.W))
    val bf_wire_inst_out = WireInit(0.U(32.W))
    val bf_wire_inst_fwd_out = WireInit(0.B)
    val bf_wire_ctrl_fwd_out = WireInit(0.B)
    val bf_wire_pc_fwd_out = WireInit(0.B)
    val bf_wire_pc_out = WireInit(0.U(32.W))
    val bf_wire_current_pc_out = WireInit(0.U(32.W))

    //BL Mux Control Wires
    val bl_wire_rs1_in = WireInit(0.U(32.W))
    val bl_wire_rs2_in = WireInit(0.U(32.W))
    val bl_wire_funct3_in = WireInit(0.U(3.W))
    val bl_wire_out = WireInit(0.B)

    //SD Mux Control Wires
    val sd_wire_mem_rd_sel = WireInit(0.U(5.W))
    val sd_wire_mem_regWrite = WireInit(0.B)
    val sd_wire_fwd1 = WireInit(0.B)
    val sd_wire_fwd2 = WireInit(0.B)

    val data1 = WireInit(0.S(32.W))
    val data2 = WireInit(0.S(32.W))
    val writebackdata = WireInit(0.S(32.W))
    val bl_in1 = WireInit(0.S(32.W))
    val bl_in2 = WireInit(0.S(32.W))
    val yeah = WireInit(0.S(32.W))
    val jalr_out = WireInit(0.S(32.W))

    
    //idex Wires

    val idex_wire_memRead = WireInit(0.B)
    val idex_wire_regWrite = WireInit(0.B)
    val idex_wire_memtoReg = WireInit(0.B)
    val idex_wire_rd_sel = WireInit(0.U(5.W))


    //EXMem Wires

    val exmem_wire_aludata = WireInit(0.S(32.W))
    val exmem_wire_rd_sel = WireInit(0.U(5.W))
    val exmem_wire_memRead = WireInit(0.B)
    val exmem_wire_regWrite = WireInit(0.B)

    val exmem_wire_memtoReg = WireInit(0.B)
    val exmem_wire_memWrite = WireInit(0.B)
    val exmem_wire_rs2 = WireInit(0.S(32.W))

    //MemWB Wires

    val memwb_wire_memRead = WireInit(0.B)
    val memwb_wire_regWrite = WireInit(0.B)
    val memwb_wire_memtoReg = WireInit(0.B)
    val memwb_wire_rd_sel = WireInit(0.U(5.W))

    val alu_out = WireInit(0.S(32.W))
    val memory_out = WireInit(0.S(32.W))


    //PC
    pc.io.reset := io.reset

    //PC Adder
    pc_adder.io.in := pc.io.out

    io.pc_value := pc.io.out
    
    val pc_add_val = pc_adder.io.out
    

    //Instruction Mem
    IM.io.in := pc.io.out(21, 2)
    val ins = IM.io.out

    val ifid_pc_in = Mux(hd_wire_inst_fwd_out || bf_wire_inst_fwd_out, hd_wire_current_pc_out, pc.io.out)
    val ifid_pc_4_in = Mux(hd_wire_inst_fwd_out || bf_wire_inst_fwd_out, hd_wire_pc_out, pc_add_val)
    val ifid_inst_in = Mux(hd_wire_inst_fwd_out || bf_wire_inst_fwd_out, hd_wire_inst_out, ins)


    ifid.io.inst_in := ifid_inst_in
    ifid.io.pc_in := ifid_pc_in
    ifid.io.pc_4_in := ifid_pc_4_in

    //////////////////////////////////////////////////////////      IFID       /////////////////////////////////////////////////////////////

    //Hazard detection unit
    hd.io.inst_in := ifid.io.inst_out
    hd.io.memRead_in := idex_wire_memRead
    hd.io.rd_sel_in := idex_wire_rd_sel
    hd.io.current_pc_in := ifid.io.pc_out
    hd.io.pc_in := ifid.io.pc_4_out

    hd_wire_inst_out := hd.io.inst_out
    hd_wire_inst_fwd_out := hd.io.inst_fwd_out
    hd_wire_ctrl_fwd_out := hd.io.ctrl_fwd_out
    hd_wire_pc_fwd_out := hd.io.pc_fwd_out
    hd_wire_pc_out := hd.io.pc_out
    hd_wire_current_pc_out := hd.io.current_pc_out


    //Control Unit
    control_unit.io.in := ifid.io.inst_out(6, 0)
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

    val temp = WireInit(0.B)
    val jump_taken = WireInit(0.B)


    //Branch Forward Unit
    bf.io.inst_in := ifid.io.inst_out
    bf.io.current_pc_in := ifid.io.pc_out
    bf.io.pc_in := ifid.io.pc_4_out
    bf.io.id_rdsel := idex_wire_rd_sel
    bf.io.id_memRead := idex_wire_memRead
    bf.io.ex_rdsel := exmem_wire_rd_sel
    bf.io.ex_memRead := exmem_wire_memRead
    bf.io.mem_rdsel := memwb_wire_rd_sel
    bf.io.mem_memRead := memwb_wire_memRead
    bf.io.ctrl_branch := branch

    bf_wire_fwd1_out := bf.io.fwd1_out
    bf_wire_fwd2_out := bf.io.fwd2_out
    bf_wire_inst_out := bf.io.inst_out
    bf_wire_inst_fwd_out := bf.io.inst_fwd_out
    bf_wire_ctrl_fwd_out := bf.io.ctrl_fwd_out
    bf_wire_pc_fwd_out := bf.io.pc_fwd_out
    bf_wire_pc_out := bf.io.pc_out
    bf_wire_current_pc_out := bf.io.current_pc_out
     

    //Immediate Gen
    IG.io.inst := ifid.io.inst_out
    IG.io.pc := ifid.io.pc_out

    val IG_Mux = WireInit(0.S(32.W))

    when (extend_sel === 0.U) {
        IG_Mux := IG.io.IType
    }.elsewhen (extend_sel === 1.U) {
        IG_Mux := IG.io.UType
    }.elsewhen( extend_sel === 2.U) {
        IG_Mux := IG.io.SType
    }


    //Register File
    register.io.read1 := ifid.io.inst_out(19, 15)
    register.io.read2 := ifid.io.inst_out(24, 20)
    register.io.regwrite := memwb_wire_regWrite
    register.io.writedata := writebackdata
    register.io.writereg := ifid.io.inst_out(11, 7)
    register.io.clear := io.reg_zero

     
    //Structural Detection
    sd.io.rs1_sel := ifid.io.inst_out(19, 15)
    sd.io.rs2_sel := ifid.io.inst_out(24, 20)
    sd.io.mem_rd_sel := memwb_wire_rd_sel
    sd.io.mem_regWrite := memwb_wire_regWrite

   
    data1 := Mux(sd.io.fwd1, writebackdata, register.io.readdata1)   
    data2 := Mux(sd.io.fwd2, writebackdata, register.io.readdata2)

    
    bl_in1 := MuxCase(data1, Seq(
        (bf.io.fwd1_out === 0.U) -> data1,
        (bf.io.fwd1_out === 1.U) -> alu_out,
        (bf.io.fwd1_out === 2.U) -> exmem_wire_aludata,
        (bf.io.fwd1_out === 3.U) -> writebackdata,
        (bf.io.fwd1_out === 4.U) -> memory_out,
        (bf.io.fwd1_out === 5.U) -> writebackdata
    ))

    bl_in2 := MuxCase(data2, Seq(
        (bf.io.fwd2_out === 0.U) -> data2,
        (bf.io.fwd2_out === 1.U) -> alu_out,
        (bf.io.fwd2_out === 2.U) -> exmem_wire_aludata,
        (bf.io.fwd2_out === 3.U) -> writebackdata,
        (bf.io.fwd2_out === 4.U) -> memory_out,
        (bf.io.fwd2_out === 5.U) -> writebackdata
    ))


    //Branch Logic Unit
    bl.io.rs2_in := bl_in2
    bl.io.rs1_in := bl_in1
    bl.io.funct3_in := ifid.io.inst_out(14, 12)
    bl_wire_out := bl.io.out 

    jalr_out := bl_in1 + IG_Mux //TO CHANGE

    val pc_mux2 = WireInit(0.S(32.W))

    
    //// Branch Upper
    
    val mux_wire1 = Mux(branch && bl_wire_out, IG.io.SBType, pc_add_val.asSInt)

    when (control_unit.io.next_PC_sel === 0.U) {
        pc_mux2 := pc_add_val.asSInt
    }.elsewhen (control_unit.io.next_PC_sel === 1.U) {
        pc_mux2 := mux_wire1.asSInt
    }.elsewhen (control_unit.io.next_PC_sel === 2.U) {
        pc_mux2 := IG.io.UJType
    }.otherwise {
        pc_mux2 := jalr_out
    }

    val next_pc = RegNext(pc_mux2.asUInt, 0.U) 
    val stall = RegNext(hd_wire_pc_fwd_out || bf_wire_pc_fwd_out)


    pc.io.in := Mux(stall, hd_wire_pc_out, next_pc)
    
    
    when (next_PC_sel === "b11".U || (branch && bl_wire_out)) {
        temp := 1.B
    }.otherwise {
        temp := 0.B
    }

    jump_taken := RegNext(temp)



    
    idex.io.memWrite_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, memwrite)
    idex.io.branch_in:= Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, branch)
    idex.io.memRead_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, memread)
    idex.io.regWrite_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, regwrite)
    idex.io.memtoReg_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, memtoreg)
    idex.io.aluOperation_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, aluoperation)
    idex.io.operand_A_sel_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, operandA_sel)
    idex.io.operand_B_sel_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.B, operandB_sel)
    
    idex.io.pc_4_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.pc_4_out)
    idex.io.pc_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.pc_out)
    idex.io.rs1_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.S, data1)
    idex.io.rs2_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.S, data2)
    idex.io.rd_sel_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.inst_out(11, 7).asUInt)
    idex.io.rs1_sel_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.inst_out(19, 15).asUInt)
    idex.io.rs2_sel_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.inst_out(24, 20).asUInt)
    idex.io.imm_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.S, IG_Mux)
    idex.io.funct3_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.inst_out(14, 12).asUInt)
    idex.io.funct7_in := Mux(bf_wire_ctrl_fwd_out || jump_taken, 0.U, ifid.io.inst_out(30).asUInt)



    /////////////////////////////////////// IDEX ///////////////////////

    idex_wire_memRead := idex.io.memRead_out
    idex_wire_rd_sel := idex.io.rd_sel_out

    //Forwarding Unit
    fu.io.ex_memRead := exmem.io.memRead_out
    fu.io.ex_regWrite_in := exmem_wire_regWrite
    fu.io.mem_regWrite_in := memwb_wire_regWrite
    fu.io.ex_rd_sel_in := exmem_wire_rd_sel
    fu.io.mem_rd_sel_in := memwb_wire_rd_sel
    fu.io.rs1_sel_in := idex.io.rs1_sel_out
    fu.io.rs2_sel_in := idex.io.rs2_sel_out


    val forward1 = WireInit(0.S(32.W))
    val forward2 = WireInit(0.S(32.W))

    when (fu.io.forward_a_out === 0.U) {
        forward1 := idex.io.rs1_out
    }.elsewhen (fu.io.forward_a_out === 1.U) {
        forward1 := exmem_wire_aludata
    }.elsewhen (fu.io.forward_a_out === 2.U) {
        forward1 := writebackdata
    }.elsewhen (fu.io.forward_a_out === 3.U) {
        forward1 := memory_out
    }

    when (fu.io.forward_b_out === 0.U) {
        forward2 := idex.io.rs2_out
    }.elsewhen (fu.io.forward_b_out === 1.U) {
        forward2 := exmem_wire_aludata
    }.elsewhen (fu.io.forward_b_out === 2.U) {
        forward2 := writebackdata
    }    

    //ALU Control
    alu_control.io.ALUOp := idex.io.aluOperation_out
    alu_control.io.funct3 := idex.io.funct3_out
    alu_control.io.funct7 := idex.io.funct7_out

    val alu1 = WireInit(0.S(32.W))
    val alu2 = WireInit(0.S(32.W))

    when (idex.io.operand_A_sel_out === 0.U) {
        alu1 := forward1
    }.elsewhen (idex.io.operand_A_sel_out === 1.U) {
        alu1 := idex.io.pc_out.asSInt
    }.elsewhen (idex.io.operand_A_sel_out === 2.U) {
        alu1 := idex.io.pc_4_out.asSInt
    }.otherwise {
        alu1 := forward1
    }

    when (idex.io.operand_B_sel_out === 0.B) {
        alu2 := forward1
    }.otherwise {
        alu2 := idex.io.imm_out
    }
 
    //ALU
    ALU.io.in1 := alu1
    ALU.io.in2 := alu2
    ALU.io.alu_control := alu_control.io.outputSignal
    alu_out := ALU.io.output


    exmem.io.memWrite_in := idex.io.memWrite_out
    exmem.io.branch_in := idex.io.branch_out
    exmem.io.memRead_in := idex.io.memRead_out
    exmem.io.regWrite_in := idex.io.regWrite_out
    exmem.io.memtoReg_in := idex.io.memtoReg_out
    exmem.io.rs2_in := forward2
    exmem.io.rd_sel_in := idex.io.rd_sel_out
    exmem.io.alu_in := alu_out


    ////////////////////////////////// EX MEM ////////////////////////////////


    exmem_wire_aludata := exmem.io.alu_out
    exmem_wire_rd_sel := exmem.io.rd_sel_out
    exmem_wire_memRead := exmem.io.memRead_out
    exmem_wire_regWrite := exmem.io.regWrite_out
    exmem_wire_memtoReg := exmem.io.memtoReg_out
    exmem_wire_memWrite := exmem.io.memWrite_out
    exmem_wire_rs2 := exmem.io.rs2_out

    //RAM
    RAM.io.Addr := exmem_wire_aludata.asUInt
    RAM.io.wr_en := exmem_wire_memWrite
    RAM.io.rd_en := exmem_wire_memRead
    RAM.io.wrData := exmem_wire_rs2

    memory_out := RAM.io.out

    memwb.io.memRead_in := exmem_wire_memRead
    memwb.io.regWrite_in := exmem_wire_regWrite
    memwb.io.memtoReg_in := exmem_wire_memtoReg
    memwb.io.rd_sel_in := exmem_wire_rd_sel
    memwb.io.dataMem_in := memory_out
    memwb.io.alu_in := exmem_wire_aludata

    //////////////////////////////// Mem WB //////////////////////////////

    memwb_wire_memRead := memwb.io.memRead_out
    memwb_wire_regWrite := memwb.io.regWrite_out
    memwb_wire_memtoReg := memwb.io.memtoReg_out
    memwb_wire_rd_sel := memwb.io.rd_sel_out

    writebackdata := Mux(memwb_wire_memtoReg, memwb.io.dataMem_out, memwb.io.alu_out)
    
    io.writeback := writebackdata
    io.destination_address := memwb_wire_rd_sel

    
}
