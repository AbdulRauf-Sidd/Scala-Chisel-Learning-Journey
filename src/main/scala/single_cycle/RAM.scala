package single_cycle

import chisel3._
import chisel3.util._

class RAM extends Module {
  val io = IO(new Bundle {
    val out = Output(SInt(32.W))
    val Addr = Input(UInt(8.W))
    val wrData = Input(SInt(32.W))
    val wr_en = Input(Bool())
    val rd_en = Input(Bool())
  })

  
  val memory = SyncReadMem(256, SInt(32.W))
  val memData = memory.read(io.Addr, io.rd_en)
  

  when(io.wr_en) {
        memory.write(io.Addr, io.wrData)
  }

  io.out := memData

}