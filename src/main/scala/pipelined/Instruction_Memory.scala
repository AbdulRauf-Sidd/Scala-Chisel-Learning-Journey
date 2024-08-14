package pipelined

import chisel3._
import chisel3.util._

// class Instruction_Memory extends Module {
//     val io = IO(new Bundle {
//         val in = Input(UInt(20.W))
//         val out = Output(UInt(32.W))
//     })

//     val regs = Reg(Vec(2000, UInt(32.W)))
    
//     regs(5) := "b00000000000000100000000011100011".U
//     regs(1) := "b00000000100100000000000010010011".U //9 in 1 // rs0 + 9
//     regs(2) := "b00000000010000001000000100010011".U //13 in 2 // rs1 + 4
//     regs(3) := "b00000000001000001010000000100011".U //store 13 in mem(9) // mem(rs1 + imm) = rs2
//     regs(4) := "b00000000000000001010001000000011".U //load 13 in 4 // rs4 = mem(rs1 + imm)
//     // regs(5) := "b00000000011100100000001100010011".U //20 in 6 // rs6 = 7 + rs4
//     regs(6) := "b00000000000100000000010000010011".U //1 in 8 // rs8 = 1 + rs0
//     regs(8) := "b00000000000100100000010010010011".U //10 in 9 // rs9 = 1 + rs4
//     io.out := regs(io.in)
// }


class Instruction_Memory extends Module {
val io = IO(new Bundle {
        val in = Input(UInt(32.W))
        val out = Output(UInt(32.W))
    })

    val mem = Mem(200 / 4, UInt(32.W)) 

    val readAddr = (io.in / 2.U).asUInt
    mem(1) := "b00000000100100000000000010010011".U
    //   mem(1) := "b00000000100100000000000010010011".U 
    mem(2) := "b00000000010000001000000100010011".U 
    mem(3) := "b00000000001000001010000000100011".U 
    mem(4) := "b00000000000000001010001000000011".U 
    mem(5) := "b00000000000000100000000011100011".U
    // regs(5) := "b00000000011100100000001100010011".U 
    mem(6) := "b00000000000100000000010000010011".U 
    mem(8) := "b00000000000100100000010010010011".U //10 in 9 // rs9 = 1 + rs4
    io.out := mem(io.in)
}