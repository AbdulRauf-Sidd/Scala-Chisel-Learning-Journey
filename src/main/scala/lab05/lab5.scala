package lab05

import chisel3._
import chisel3.util._

class IO_Interface(width: Int) extends Bundle {
  val alu_oper = Input(UInt(4.W))  
  val arg_x = Input(UInt(width.W))
  val arg_y = Input(UInt(width.W))
  val alu_out = Output(UInt(width.W))
}

class ALU(width_parameter: Int) extends Module {
  val io = IO(new IO_Interface(width_parameter))
  io.alu_out := 0.U
  val index = log2Ceil(width_parameter)

  when(io.alu_oper === "b0000".U) {
    io.alu_out := io.arg_x & io.arg_y
  }.elsewhen(io.alu_oper === "b0001".U) {
    io.alu_out := io.arg_x | io.arg_y
  }.elsewhen(io.alu_oper === "b0010".U) { 
    io.alu_out := io.arg_x + io.arg_y
  }.elsewhen(io.alu_oper === "b0011".U) {
    io.alu_out := io.arg_x - io.arg_y
  }.elsewhen(io.alu_oper === "b0100".U) {
    io.alu_out := io.arg_x ^ io.arg_y
  }.elsewhen(io.alu_oper === "b0101".U) {
    io.alu_out := io.arg_x << io.arg_y(index - 1)
  }.elsewhen(io.alu_oper === "b0110".U) {
    io.alu_out := io.arg_x >> io.arg_y(index - 1)
  }.elsewhen(io.alu_oper === "b0111".U) {
    io.alu_out := (io.arg_x.asSInt >> io.arg_y(index - 1)).asUInt
  }.elsewhen(io.alu_oper === "b1000".U) {
    io.alu_out := (io.arg_x.asSInt < io.arg_y.asSInt).asUInt
  }.elsewhen(io.alu_oper === "b1001".U) {
    io.alu_out := (io.arg_x < io.arg_y).asUInt
  }
}



class eMux_IO [T <: Data](typ : T) extends Bundle {
    val out = Output ( typ )
    val in1 = Input ( typ )
    val in2 = Input ( typ )
    val sel = Input ( Bool () )
}

class eMux [ T <: Data ]( gen : T ) extends Module {
    val io = IO(new eMux_IO(gen))

    io.out := Mux(io.sel, io.in1, io.in2)
}

class Operator[T <: Data](n: Int, generic: T)(op: (T, T) => T) extends Module {
    require(n > 0) 
    
    val io = IO(new Bundle {
      val in1 = Input(Vec(n, generic)) 
      val in2 = Input(Vec(n, generic)) 
      val out = Output(Vec(n, generic))
    })

    for (i <- 0 until n) {
      io.out(i) := op(io.in1(i), io.in2(i))
    }
}

class Adder_IO (w: Int) extends Bundle {
        val in0 = Input(SInt(w.W))
        val in1 = Input(SInt(w.W))
        val sum = Output(SInt(w.W))
}

class Adder ( Width : Int ) extends Module {
    require ( Width >= 0)
    // your code begin from here
    val io = IO(new Adder_IO(Width))
    io.sum := io.in0 + io.in1    
    // your code end here
}


// class Datain[T <: Data](gen: T, width: Int) extends Bundle {
//   val addr = UInt(10.W)
//   val data = gen(width.W)
// }


    

// class Router[T <: Data](gen: T, w: Int) extends Module {
//   val io = IO(new Bundle {
//     val in = Input(new Datain(gen, w))
//     val out = Output(gen)})

//     when (io.in.addr === 10.U) {
//         io.out := io.in.data
//     }.otherwise {
//         io.out := io.in.data.asUInt + 2.U
//     }
// }
  