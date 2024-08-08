package single_cycle

import chisel3._
import chisel3.util._

class Immediate_Generation extends Module {
    val io = IO(new Bundle {
        val inst = Input(UInt(32.W))
        val pc = Input(UInt(32.W))
        val SType = Output(SInt(32.W))
        val SBType = Output(SInt(32.W))
        val UType = Output(SInt(32.W))
        val UJType = Output(SInt(32.W))
        val IType = Output(SInt(32.W))
    })

    val wire1 = io.inst(11, 7).asUInt
    val wire2 = io.inst(31, 25).asUInt
    val combined1 = Cat(wire2, wire1).asUInt
    val combined2 = Cat(wire2(6), wire1(0), wire2(5, 0), wire1(4, 1), 0.U(1.W))
    val combined3 = io.inst(31, 12)
    val combined4 = Cat(io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 21), 0.U(1.W))
    val combined5 = io.inst(31, 20)

    val sExtend1 = WireInit(0.S(32.W))
    val sExtend2 = WireInit(0.S(32.W))
    val sExtend3 = WireInit(0.S(32.W))
    val sExtend4 = WireInit(0.S(32.W))
    val sExtend5 = WireInit(0.S(32.W))

    when (combined1(11) === 0.B) {
        sExtend1 := Cat("b00000000000000000000".U, combined1).asSInt
    }.otherwise {
        sExtend1 := Cat("b11111111111111111111".U, combined1).asSInt
    }

    when (combined2(12) === 0.B) {
        sExtend2 := Cat("b0000000000000000000".U, combined2).asSInt
    }.otherwise {
        sExtend2 := Cat("b1111111111111111111".U, combined2).asSInt
    }

    when (combined3(19) === 0.B) {
        sExtend3 := Cat("b000000000000".U, combined3).asSInt
    }.otherwise {
        sExtend3 := Cat("b111111111111".U, combined3).asSInt
    }

    when (combined4(20) === 0.B) {
        sExtend4 := Cat("b00000000000".U, combined4).asSInt
    }.otherwise {
        sExtend4 := Cat("b11111111111".U, combined4).asSInt
    }

    when (combined1(11) === 0.B) {
        sExtend5 := Cat("b00000000000000000000".U, combined5).asSInt
    }.otherwise {
        sExtend5 := Cat("b11111111111111111111".U, combined5).asSInt
    }    

    io.SType := sExtend1
    io.SBType := sExtend2 + io.pc.asSInt
    io.UType := (sExtend3 << 5.U)
    io.UJType := sExtend4 + io.pc.asSInt
    io.IType := sExtend5
}