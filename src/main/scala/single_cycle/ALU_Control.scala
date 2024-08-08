package single_cycle

import chisel3._
import chisel3.util._


class ALUControl extends Module {
  val io = IO(new Bundle {
    val ALUOp = Input(UInt(3.W))
    val funct7 = Input(UInt(1.W))
    val funct3 = Input(UInt(3.W))
    val outputSignal = Output(UInt(4.W))  // Assuming the output is 4 bits as a placeholder
  })

  // Intermediate wires
  val notALUOp0 = ~io.ALUOp(0)
  val notALUOp1 = ~io.ALUOp(1)
  val notALUOp2 = ~io.ALUOp(2)
  val notFunct7 = ~io.funct7
  val notFunct3_0 = ~io.funct3(0)
  val notFunct3_1 = ~io.funct3(1)
  val notFunct3_2 = ~io.funct3(2)

  // Creating the logic as per the diagram
  // Outputs are created using a combination of AND, OR gates
  val andWires = Wire(Vec(7, Bool()))
  andWires(0) := ~io.ALUOp(2) & io.ALUOp(1) & io.ALUOp(0)
  andWires(1) := ~io.ALUOp(2) & io.ALUOp(1) & ~io.ALUOp(0)
  andWires(2) := ~io.ALUOp(2) & ~io.ALUOp(1) & ~io.ALUOp(0) & ~io.funct7
  andWires(3) := ~io.ALUOp(2) & ~io.ALUOp(1) & ~io.ALUOp(0) & io.funct7
  andWires(4) := ~io.ALUOp(2) & ~io.ALUOp(1) & io.ALUOp(0)
  andWires(5) := io.funct3(2) & ~io.funct3(1) & io.funct3(0)
  andWires(6) := ~io.ALUOp(2) & ~io.ALUOp(1) & io.ALUOp(0)

  val and1 = andWires(4) & ~io.funct7 & andWires(5)
  val and2 = andWires(4) & andWires(5) & io.funct7
  val and3 = andWires(4) & andWires(5)

  val and4 = ~andWires(0) & ~andWires(1) & ~andWires(2) & ~andWires(3) & ~and1 & ~and2 & ~and3 & ~andWires(6)

  val mux_sel = WireInit(0.U(4.W))

  when (and4 === 1.B) {
    mux_sel := 9.U
  }.elsewhen (andWires(6) === 1.B) {
    mux_sel := 8.U
  }.elsewhen (and3 === 1.B) {
    mux_sel := 7.U
  }.elsewhen (and2 === 1.B) {
    mux_sel := 6.U
  }.elsewhen (and1 === 1.B) {
    mux_sel := 5.U
  }.elsewhen (andWires(3) === 1.B) {
    mux_sel := 3.U
  }.elsewhen (andWires(2) === 1.B) {
    mux_sel := 2.U
  }.elsewhen (andWires(1) === 1.B) {
    mux_sel := 1.U
  }.elsewhen (andWires(0) === 1.B) {
    mux_sel := 0.U
  }

  val out1 = WireInit(0.U(5.W))

  when (mux_sel === 0.U) {
    out1 := 31.U(5.W)
  }.elsewhen (mux_sel === 1.U) {
    out1 := Cat(2.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 2.U) {
    out1 := Cat(0.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 3.U) {
    out1 := Cat(1.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 4.U) {
    out1 := Cat(0.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 6.U) {
    out1 := Cat(1.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 7.U) {
    out1 := Cat(0.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 8.U) {
    out1 := Cat(0.U(2.W), io.funct3)
  }.elsewhen (mux_sel === 9.U) {
    out1 := 0.U(5.W)
  }

  io.outputSignal := out1
}
