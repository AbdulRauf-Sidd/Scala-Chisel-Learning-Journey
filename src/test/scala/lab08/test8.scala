package lab08

import chisel3._
import org.scalatest._
import chiseltest._


class Lab08Ex02Test extends FreeSpec with ChiselScalatestTester {
  "Lab08Ex02Test" in {
    test(new twoBankForwarding()) { c =>
      c.io.sel.poke(0.U)
      c.io.wrAddr.poke(5.U)
      c.io.rdAddr.poke(5.U)
      c.io.wrData.poke(123.U)
      c.io.wr_en.poke(true.B)


      c.clock.step(1)
      c.io.wr_en.poke(false.B)
      c.clock.step(1)
      c.io.out1.expect(123.U)
      c.io.out2.expect(0.U)
      
    }
  }
}

class Lab08Ex04Test extends FreeSpec with ChiselScalatestTester {
  "Lab08Ex04Test" in {
    test(new memory_assignment()) { c =>
      c.io.requestor(0).valid.poke(false.B)
      c.io.requestor(1).valid.poke(false.B)
      c.io.requestor(0).bits.poke(0.U)
      c.io.requestor(1).bits.poke(0.U)
      c.io.wr_en.poke(false.B)
      
      c.clock.step(1)


      c.io.requestor(0).bits.poke(122.U)
      c.io.requestor(1).bits.poke(123.U)
      c.io.requestor(2).bits.poke(132.U)
      c.io.requestor(3).bits.poke(152.U)
      c.io.requestor(0).valid.poke(true.B)
      c.io.requestor(1).valid.poke(true.B)
      c.io.requestor(2).valid.poke(true.B)
      c.io.requestor(3).valid.poke(true.B)
      c.io.Writeaddr.poke(5.U)
      c.io.Readaddr.poke(10.U)
      // c.io.wr_en.poke(true.B)
      c.clock.step(1)
      c.io.requestor(0).valid.poke(true.B)
      c.io.wr_en.poke(true.B)
      c.clock.step(1)


      c.io.Readaddr.poke(5.U)
      c.clock.step(1)
      c.io.memory_out(0).expect(122.U)
    }
  }
}

