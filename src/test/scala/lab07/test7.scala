package lab07

import chisel3._
import org.scalatest._
import chiseltest._

class Lab07Ex01Test extends FreeSpec with ChiselScalatestTester {
  "Lab07Ex01Test" in {
    test(new ArbiterWithQueues(UInt(8.W))) { c =>
      c.io.in0.valid.poke(false.B)
      c.io.in1.valid.poke(false.B)
      c.io.in0.bits.poke(0.U)
      c.io.in1.bits.poke(0.U)
      c.clock.step(1)


      c.io.in0.valid.poke(true.B)
      c.io.in0.bits.poke(1.U)
      c.clock.step(1)
      c.io.in0.valid.poke(true.B)
      c.io.in0.bits.poke(2.U)
      c.clock.step(1)
      c.io.in0.valid.poke(false.B)
      c.io.in0.bits.poke(0.U)

      c.io.in1.valid.poke(true.B)
      c.io.in1.bits.poke(3.U)
      c.clock.step(1)
      c.io.in1.valid.poke(true.B)
      c.io.in1.bits.poke(4.U)
      c.clock.step(1)
      c.io.in1.valid.poke(false.B)
      c.io.in1.bits.poke(0.U)

      c.io.out.ready.poke(true.B)
      c.clock.step(1)
    //   c.io.out.bits.expect(1.U)
      c.clock.step(1)
    //   c.io.out.bits.expect(2.U)
      c.clock.step(1)
    //   c.io.out.bits.expect(3.U)
      c.clock.step(1)
    //   c.io.out.bits.expect(4.U)
    }
  }
}


class Lab07Ex03Test extends FreeSpec with ChiselScalatestTester {
  "Lab07Ex03Test" in {
    test(new Manchester_Encoding()) { c =>
      c.io.start.poke(true.B)
      c.clock.step(1)
      c.io.start.poke(true.B)
      c.io.in.poke(1.U)
      c.clock.step(1)
      c.io.start.poke(true.B)
      c.io.in.poke(1.U)
      c.clock.step(1)
      
    }
  }
}