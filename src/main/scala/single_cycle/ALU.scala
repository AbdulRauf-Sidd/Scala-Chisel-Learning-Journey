package single_cycle

import chisel3._
import chisel3.util._

class ALU extends Module {
    val io = IO(new Bundle {
        val in1 = Input(SInt(32.W))
        val in2 = Input(SInt(32.W))
        val alu_control = Input(UInt(5.W))
        val output = Output(SInt(32.W))
        val branch = Output(Bool())
    })

    val addition = io.in1+ io.in2
    val SLL = (io.in1 << io.in2(4, 0))
    val mux_sel1 = io.in1 < io.in2
    val mux_sel2 = io.in1 === io.in2
    val mux_sel3 = ((io.in1 > io.in2) || mux_sel2)

    val SLT = Mux(mux_sel1, 1.S(32.W), 0.S(32.W))
    val BEQ = Mux(mux_sel2, 1.S(32.W), 0.S(32.W))
    val BGE = Mux(mux_sel3, 1.S(32.W), 0.S(32.W))

    val XOR_o = (io.in1 ^ io.in2)
    val SRL = (io.in1 >> io.in2(4, 0))
    val OR_o = (io.in1 | io.in2)
    val AND = (io.in1 & io.in2)
    val SUB = io.in1 - io.in2

    val SRA = Cat(io.in1(31), (io.in1(31, 1) >> io.in2(4, 0))).asSInt
    val SLTU = Mux(io.in1.asUInt < io.in2.asUInt, 1.S(32.W), 0.S(32.W))
    val BGEU = Mux(io.in1.asUInt === io.in2.asUInt || io.in1.asUInt > io.in2.asUInt, 1.S(32.W), 0.S(32.W))

    val BNE = Mux(io.in1 =/= io.in2, 1.S(32.W), 0.S(32.W))

    val BLT = Mux(mux_sel1, 1.S(32.W), 0.S(32.W))

    val JAL = io.in1

    val mux_out = WireInit(0.S(32.W))

    when (io.alu_control === 0.U) {
        mux_out := addition
    }.elsewhen (io.alu_control === 1.U) {
        mux_out := SLL
    }.elsewhen (io.alu_control === 2.U) {
        mux_out := SLT
    }.elsewhen (io.alu_control === 3.U) {
        mux_out := SLTU
    }.elsewhen (io.alu_control === 4.U) {
        mux_out := XOR_o
    }.elsewhen (io.alu_control === 5.U) {
        mux_out := OR_o
    }.elsewhen (io.alu_control === 6.U) {
        mux_out := AND
    }.elsewhen (io.alu_control === 7.U) {
        mux_out := SUB
    }.elsewhen (io.alu_control === 8.U) {
        mux_out := SLL
    }.elsewhen (io.alu_control === 13.U) {
        mux_out := SRA
    }.elsewhen (io.alu_control === 16.U) {
        mux_out := BEQ
    }.elsewhen (io.alu_control === 17.U) {
        mux_out := BNE
    }.elsewhen (io.alu_control === 20.U) {
        mux_out := BLT
    }.elsewhen (io.alu_control === 21.U) {
        mux_out := BGE
    }.elsewhen (io.alu_control === 22.U) {
        mux_out := SLTU
    }.elsewhen (io.alu_control === 23.U) {
        mux_out := BGEU
    }.elsewhen (io.alu_control === 31.U) {
        mux_out := JAL
    }

    io.branch := ((io.alu_control(4, 3) === 2.U) && (mux_out === 1.S))
    io.output := mux_out
}