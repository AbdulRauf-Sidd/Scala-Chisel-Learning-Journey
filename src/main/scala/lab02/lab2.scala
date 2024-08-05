package lab02

import chisel3._
import chisel3.util._
// Mux IO interface class
class Mux_2to1_IO extends Bundle {
    val in_A = Input(UInt(32.W))
    val in_B = Input(UInt(32.W))
    val select = Input(Bool())
    val out = Output(UInt())
}

// 2 to 1 Mux implementation
class Mux_2to1 extends Module {
    val io = IO(new Mux_2to1_IO)
    // update the output
    when(io.select === 0.B) {
        io.out := io.in_A
    }.otherwise {
        io.out := io.in_B
    }
}

class Mux_4to1_IO extends Bundle {
    val in_A = Input(UInt(32.W))
    val in_B = Input(UInt(32.W))
    val in_C = Input(UInt(32.W))
    val in_D = Input(UInt(32.W))
    val select = Input(UInt(2.W))
    val out = Output(UInt(32.W))
}

class Mux_4to1 extends Module {
    val io = IO(new Mux_4to1_IO)
    when(io.select === 0.U) {
        io.out := io.in_A
    }.elsewhen(io.select === 1.U) {
        io.out := io.in_B
    }.elsewhen(io.select === 2.U) {
        io.out := io.in_C
    }.otherwise {
        io.out := io.in_D
    }
}


class MuxLookupNested extends Module {
  val io = IO(new Bundle {
    val in0 = Input(Bool())
    val in1 = Input(Bool())
    val in2 = Input(Bool())
    val in3 = Input(Bool())
    val in4 = Input(Bool())
    val in5 = Input(Bool())
    val in6 = Input(Bool())
    val in7 = Input(Bool())
    val sel = Input(UInt(3.W))
    val out = Output(Bool())
  })

  // Break down the selection signal
  val sel_msb = io.sel(2) // Most significant bit
  val sel_lsb = io.sel(1, 0) // Least significant bits

  // First level MuxLookup
  io.out := MuxLookup(sel_msb, false.B, Array(
    0.U -> MuxLookup(sel_lsb, false.B, Array(
      0.U -> io.in0,
      1.U -> io.in1,
      2.U -> io.in2,
      3.U -> io.in3
    )),
    1.U -> MuxLookup(sel_lsb, false.B, Array(
      0.U -> io.in4,
      1.U -> io.in5,
      2.U -> io.in6,
      3.U -> io.in7
    ))
  ))
}


class mux_onehot_4to2 extends Module {
    val io = IO ( new Bundle {
        val sel = Input (UInt(4.W))
        val out = Output (UInt(2.W))
    })

    val out0 = Mux1H(Seq(
        (io.sel === 1.U) -> 0.U,
        (io.sel === 2.U) -> 1.U,
        (io.sel === 4.U) -> 0.U,
        (io.sel === 8.U) -> 1.U
    ))

    val out1 = Mux1H(Seq(
        (io.sel === 1.U) -> 0.U,
        (io.sel === 2.U) -> 0.U,
        (io.sel === 4.U) -> 1.U,
        (io.sel === 8.U) -> 1.U
    ))

    io.out := Cat(out1, out0).asUInt
}


class LM_IO_Interface extends Bundle {
    val s0 = Input(Bool())
    val s1 = Input(Bool())
    val s2 = Input(Bool())
    val out = Output(UInt())
}

class Mux_5to1 extends Module {
    val io = IO(new LM_IO_Interface)
    val sel = Cat(io.s1, io.s0).asUInt
    val ip1 = MuxLookup(sel, false.B, Array(
        0.U -> 0.U,
        1.U -> 8.U,
        2.U -> 16.U,
        3.U -> 24.U,
    ))

    io.out := MuxLookup(io.s2, false.B, Array(
        0.U -> ip1,
        1.U -> 32.U
    ))
}


class barrel_shift extends Module {
    val io = IO ( new Bundle {
        val in = Vec(4, Input(Bool()))
        val sel = Vec(2, Input(Bool()))
        val shift_type = Input(Bool())
        val out = Vec(4, Output(Bool()))
    })
    // Start you code here
    val mux1 = Mux(io.shift_type, io.in(0), 0.B)
    val mux2 = Mux(io.shift_type, io.in(1), false.B)
    val mux3 = Mux(io.shift_type, io.in(2), false.B)
    

    io.out(0) := MuxCase(false.B, Seq(
        (io.sel(0) === 0.U && io.sel(1) === 0.U) -> io.in(0),
        (io.sel(0) === 1.U && io.sel(1) === 0.U) -> io.in(1),
        (io.sel(0) === 0.U && io.sel(1) === 1.U) -> io.in(2),
        (io.sel(0) === 1.U && io.sel(1) === 1.U) -> io.in(3)
    ))

    io.out(1) := MuxCase(false.B, Array(
        (io.sel(0) === 0.U && io.sel(1) === 0.U) -> io.in(1),
        (io.sel(0) === 1.U && io.sel(1) === 0.U) -> io.in(2),
        (io.sel(0) === 0.U && io.sel(1) === 1.U) -> io.in(3),
        (io.sel(0) === 1.U && io.sel(1) === 1.U) -> mux1
    ))

    io.out(2) := MuxCase(false.B, Array(
        (io.sel(0) === 0.U && io.sel(1) === 0.U) -> io.in(2),
        (io.sel(0) === 1.U && io.sel(1) === 0.U) -> io.in(3),
        (io.sel(0) === 0.U && io.sel(1) === 1.U) -> mux1,
        (io.sel(0) === 1.U && io.sel(1) === 1.U) -> mux2
    ))


    io.out(3) := MuxCase(false.B, Array(
        (io.sel(0) === 0.U && io.sel(1) === 0.U) -> io.in(3),
        (io.sel(0) === 1.U && io.sel(1) === 0.U) -> mux1,
        (io.sel(0) === 0.U && io.sel(1) === 1.U) -> mux2,
        (io.sel(0) === 1.U && io.sel(1) === 1.U) -> mux3
    ))

    // End your code here
}
