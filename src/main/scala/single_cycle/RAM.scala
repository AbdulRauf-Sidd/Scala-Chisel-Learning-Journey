package single_cycle

import chisel3._
import chisel3.util._

class RAM extends Module {
  val io = IO(new Bundle {
    val out = Output(SInt(32.W))
    val Addr = Input(UInt(32.W))
    val wrData = Input(SInt(32.W))
    val wr_en = Input(Bool())
    val rd_en = Input(Bool())
  })

  
  val memory = Mem(256, SInt(32.W))
  val memData = WireInit(0.S(32.W)) 

  when (io.rd_en){
    memData := memory.read(io.Addr)
  }
  

  when(io.wr_en) {
        memory.write(io.Addr, io.wrData)
  }

  io.out := memData

}