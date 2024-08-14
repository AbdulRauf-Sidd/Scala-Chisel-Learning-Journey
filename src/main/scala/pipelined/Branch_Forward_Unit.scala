package pipelined
import chisel3._
import chisel3.util._

class Branch_Forward_Unit extends  Module {
    val io = IO(new Bundle {
        val inst_in = Input(UInt(32.W))
        val current_pc_in = Input(UInt(32.W))
        val pc_in = Input(UInt(32.W))

        val id_rdsel = Input(UInt(5.W))
        val id_memRead = Input(Bool())
        val ex_rdsel = Input(UInt(5.W))
        val ex_memRead = Input(Bool())
        val mem_rdsel = Input(UInt(5.W))
        val mem_memRead = Input(Bool())

        val ctrl_branch = Input(Bool())

        val fwd1_out = Output(UInt(4.W))
        val fwd2_out = Output(UInt(3.W))
        
        val inst_out = Output(UInt(32.W))
        val ctrl_fwd_out = Output(Bool())
        val inst_fwd_out = Output(Bool())
        val pc_fwd_out = Output(Bool())
        val pc_out = Output(UInt(32.W))
        val current_pc_out = Output(UInt(32.W)) 
    })

    val out1 = WireInit(0.U(4.W))
    val out2 = WireInit(0.U(3.W))
    val alu_hazard1 = WireInit(0.B)
    val alu_hazard2 = WireInit(0.B)
    val mem_hazard1 = WireInit(0.B)
    val mem_hazard2 = WireInit(0.B)

    val rs1_in = io.inst_in(19, 15).asUInt
    val rs2_in = io.inst_in(24, 20).asUInt

    val stall = WireInit(false.B)

    val ctrl_out = WireInit(0.B)
    val inst_out = WireInit(0.B)
    val pc_out = WireInit(0.B)
    
    
    when (io.ctrl_branch) {
        when (io.id_memRead && (io.id_rdsel === rs1_in || io.id_rdsel === rs2_in)) {
            ctrl_out := 1.B
            inst_out := 1.B
            pc_out := 1.B
            stall := true.B
        }.otherwise {
            ctrl_out := 0.B
            inst_out := 0.B
            pc_out := 0.B
            stall := false.B
        }

        when (~stall) {
            when (io.id_rdsel === rs1_in && io.id_rdsel === rs2_in) {
                out1 := 1.U
                out2 := 1.U
                alu_hazard1 := 1.B
                alu_hazard2 := 1.B
            }.elsewhen (io.id_rdsel === rs1_in) {
                out1 := 1.U
                alu_hazard1 := 1.B
            }.elsewhen(io.id_rdsel === rs2_in) {
                out2 := 1.U
                alu_hazard2 := 1.B
            }    
                
                
            when (~alu_hazard1 && ~alu_hazard2 && io.ex_rdsel === rs1_in && io.ex_rdsel === rs2_in) {
                out1 := 2.U
                out2 := 2.U
                mem_hazard1 := 1.B
                mem_hazard2 := 1.B
            }.elsewhen (~alu_hazard1 && io.ex_rdsel === rs1_in) {
                out1 := 2.U
                mem_hazard1 := 1.B
            }.elsewhen (~alu_hazard2 && io.ex_rdsel === rs2_in) {
                out2 := 2.U
                mem_hazard2 := 1.B}    
                
            when (~alu_hazard1 && ~alu_hazard2 && ~mem_hazard1 && ~mem_hazard2 && io.mem_rdsel === rs1_in && io.mem_rdsel === rs2_in) {
                out1 := 3.U
                out2 := 3.U
            }.elsewhen (~alu_hazard1 && ~mem_hazard1 && io.mem_rdsel === rs1_in) {
                out1 := 3.U
            }.elsewhen (~alu_hazard2 && ~mem_hazard2 && io.mem_rdsel === rs2_in) {
                out2 := 3.U
            }
        
            when (io.ex_memRead) {
                when (io.ex_rdsel === rs1_in && io.ex_rdsel === rs2_in) {
                    out1 := 4.U
                    out2 := 4.U
                }.elsewhen (io.ex_rdsel === rs1_in) {
                    out1 := 4.U
                }.elsewhen (io.ex_rdsel === rs2_in) {
                    out2 := 4.U
                }
            }
        
            when (io.mem_memRead) {
                when (io.mem_rdsel === rs1_in && io.mem_rdsel === rs2_in) {
                    out1 := 5.U
                    out2 := 5.U
                }.elsewhen (io.mem_rdsel === rs1_in) {
                    out1 := 5.U
                }.elsewhen (io.mem_rdsel === rs2_in) {
                    out2 := 5.U
                }
            }
        }
    }.otherwise {
        when (io.id_memRead && io.id_rdsel === rs1_in) {
            // out1 := 8.U
            alu_hazard1 := 1.B
            ctrl_out := 1.B
            inst_out := 1.B
            pc_out := 1.B
            stall := true.B
        }.elsewhen (io.id_rdsel === rs1_in) {
            out1 := 1.U
            alu_hazard1 := 1.B
        }.elsewhen (~alu_hazard1 && io.ex_rdsel === rs1_in) {
            out1 := 2.U
            mem_hazard1 := 1.B
        }.elsewhen (~alu_hazard1 && ~mem_hazard1 && io.mem_rdsel === rs1_in) {
            out1 := 3.U
        }
    }

    io.fwd1_out := out1
    io.fwd2_out := out2

    io.ctrl_fwd_out := ctrl_out
    io.inst_fwd_out := inst_out
    io.pc_fwd_out := pc_out

    io.inst_out := io.inst_in
    io.pc_out := io.pc_in
    io.current_pc_out := io.current_pc_in
}

