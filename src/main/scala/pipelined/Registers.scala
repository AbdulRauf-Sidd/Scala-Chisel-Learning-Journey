package pipelined
import chisel3._
import chisel3.util._


class RegisterFile extends Module {
  val io = IO(new Bundle {
    val read1 = Input(UInt(5.W))       
    val read2 = Input(UInt(5.W))       
    val writedata = Input(SInt(32.W))  
    val regwrite = Input(Bool())       
    val writereg = Input(UInt(5.W))    
    val clear = Input(Bool())          
    val readdata1 = Output(SInt(32.W)) 
    val readdata2 = Output(SInt(32.W)) 

    val test = Output(SInt(32.W))
  })

  
  val registers = RegInit(VecInit(Seq.fill(32)(0.S(32.W))))

  
  when(io.clear) {
    for (i <- 0 until 32) {
      registers(i) := 0.S
    }
  } .otherwise {
    
  when(io.regwrite) {
      registers(io.writereg) := io.writedata
    }
  }

  io.test := registers(0)
  when (io.read1 === 0.U) {
    io.readdata1 := 0.S
  }.otherwise {
    io.readdata1 := registers(io.read1)
  }

  when (io.read2 === 0.U) {
    io.readdata2 := 0.S
  }.otherwise {
    io.readdata2 := registers(io.read2)
  }
  
  // io.readdata2 := registers(io.read2)
}


