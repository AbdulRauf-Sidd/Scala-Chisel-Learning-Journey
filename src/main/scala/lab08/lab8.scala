package lab08
import chisel3._
import chisel3.util._

class MaskedReadWriteSmem extends Module {
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(10.W))
    val mask = Input(Vec(4, Bool()))
    val dataIn = Input(Vec(4, UInt(8.W)))
    val dataOut = Output(Vec(4, UInt(8.W)))
  })

  val mem = SyncReadMem(1024, Vec(4, UInt(8.W)))
  val memData = mem.read(io.addr, io.enable)
  when(io.write) {
    val writeData = Wire(Vec(4, UInt(8.W)))
    for (i <- 0 until 4) {
      writeData(i) := Mux(io.mask(i), io.dataIn(i), memData(i))
    }
    mem.write(io.addr, writeData)
  }
  io.dataOut := memData
}


class twoBankForwarding extends Module {
  val io = IO(new Bundle {
    val sel = Input(UInt(1.W))
    val out1 = Output(UInt(32.W))
    val out2 = Output(UInt(32.W))
    val rdAddr = Input(UInt(10.W))
    val wrAddr = Input(UInt(10.W))
    val wrData = Input(UInt(32.W))
    val wr_en = Input(Bool())
  })

  
  val memory1 = SyncReadMem(1024, UInt(32.W))
  val memory2 = SyncReadMem(1024, UInt(32.W))
  val rdAddrReg = RegNext(io.rdAddr)
  val doForwardReg = RegNext(io.wrAddr === io.rdAddr && io.wr_en)
  val memData1 = memory1.read(io.rdAddr, io.wr_en)
  val memData2 = memory2.read(io.rdAddr, io.wr_en)

  when(io.wr_en) {
    when (io.sel === 0.U) {
        memory1.write(io.wrAddr, io.wrData)
    }.otherwise {
        memory2.write(io.wrAddr, io.wrData)
    }
  }

  val outData1 = Mux(doForwardReg, io.wrData, memData1)
  val outData2 = Mux(doForwardReg, io.wrData, memData2)
  io.out1 := outData1
  io.out2 := outData2
}




class memory_assignment extends Module {
    val io = IO ( new Bundle {
        val memory_out = Vec (4 , Output ( UInt (32. W ) ) )
        val requestor = Vec (4 , Flipped ( Decoupled ( UInt (32. W ) ) ) )
        val Readaddr = Input ( UInt (5. W ) )
        val Writeaddr = Input ( UInt (5. W ) )
        val wr_en = Input(Bool())
    })
    
    // Start your code from here
    val memory0 = SyncReadMem(32, UInt(32.W))
    val memory1 = SyncReadMem(32, UInt(32.W))
    val memory2 = SyncReadMem(32, UInt(32.W))
    val memory3 = SyncReadMem(32, UInt(32.W))
    val queue0 = Module(new Queue(UInt(32.W), 4))
    val queue1 = Module(new Queue(UInt(32.W), 4))
    val queue2 = Module(new Queue(UInt(32.W), 4))
    val queue3 = Module(new Queue(UInt(32.W), 4))
    queue0.io.enq <> io.requestor(0)
    queue1.io.enq <> io.requestor(1)
    queue2.io.enq <> io.requestor(2)
    queue3.io.enq <> io.requestor(3)

    val arbiter = Module(new Arbiter(UInt(32.W), 4))
    arbiter.io.in(0) <> queue0.io.deq
    arbiter.io.in(1) <> queue1.io.deq
    arbiter.io.in(2) <> queue2.io.deq
    arbiter.io.in(3) <> queue3.io.deq

    arbiter.io.out.ready := true.B

    val memData0 = memory0.read(io.Readaddr, io.wr_en)
    val memData1 = memory1.read(io.Readaddr, io.wr_en)
    val memData2 = memory2.read(io.Readaddr, io.wr_en)
    val memData3 = memory3.read(io.Readaddr, io.wr_en)

    val bits = arbiter.io.out.bits
    val reg = RegInit(bits)
    val valid = arbiter.io.out.valid
    val chosen = arbiter.io.chosen
    val doForwardReg = RegNext(io.Readaddr === io.Writeaddr && io.wr_en)

    when (valid) {
        when (io.wr_en) {
            when (chosen === 0.U) {
                memory0.write(io.Writeaddr, bits)
            }.elsewhen (chosen === 1.U) {
                memory1.write(io.Writeaddr, bits)
            }.elsewhen (chosen === 2.U) {
                memory2.write(io.Writeaddr, bits)
            }.otherwise {
                memory3.write(io.Writeaddr, bits)
            }
        }
    }

    printf(p"Arbiter chosen: ${chosen}\n")
  printf(p"Arbiter out valid: ${valid}\n")
  printf(p"Arbiter out bits: ${bits}\n")
  printf(p"Arbiter enable: ${io.wr_en}\n")
  printf(p"\n")
  

    io.memory_out(0) := Mux(doForwardReg, bits, memData0)
    io.memory_out(1) := Mux(doForwardReg, bits, memData1)
    io.memory_out(2) := Mux(doForwardReg, bits, memData2)
    io.memory_out(3) := Mux(doForwardReg, bits, memData3)
    // End your code here
}

