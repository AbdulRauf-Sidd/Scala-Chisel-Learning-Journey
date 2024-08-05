package single_cycle

import chisel3._
import chisel3.util._

class Program_Counter extends Module {
    val io = IO (new Bundle {
        val in = Input(UInt(32.W))
        val reset = Input(Bool())
        val out = Output(UInt(32.W))
    })

    when (reset) {
        io.out := 0.U
    }.otherwise {
        io.out := io.in
    }
}

class PC_Adder extends Module {
    val io = IO (new Bundle {
        val in = Input(UInt(32.W))
        val out = Output(UInt(32.W))
    })

    val io.out := io.in + 4.U
}

class ROM extends Module {
    val io = IO(new Bundle {
        val in = Input(UInt(20.W))
        val out = Output(UInt(32.W))
    })

    val regs = Reg(Vec(1048576, UInt(32.W)))
    io.out := regs(io.in)
}

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
}


class Memory extends Module {
    val io = IO(new Bundle {
        val readAdd1 = Input(UInt(5.W))
        val readAdd2 = Input(UInt(5.W))
        val writeAdd = Input(UInt(5.W))
        val writeData = Input(UInt(32.W))
        val clear = Input(Bool())
        val readData1 = Output(UInt(32.W))
        val readData2 = Output(UInt(32.W))
    })

    
}