package lab06

import chisel3._
import chiseltest._
import org.scalatest.FreeSpec

class Lab06Ex03Test extends FreeSpec with ChiselScalatestTester {
  "twoHotTimer Test" in {
    test(new twoHotTimer) { c =>
      // Test reload functionality
      c.io.din.poke(10.U)
      c.io.reload.poke(true.B)
      c.clock.step(1)
      c.io.reload.poke(false.B)
      c.io.count.expect(10.U)
      c.io.out.expect(false.B)

      c.clock.step(10)
      c.io.count.expect(0.U)
      c.io.out.expect(true.B)
      c.io.reload.poke(true.B)
      c.clock.step(1)
      c.io.reload.poke(false.B)
      c.io.count.expect(10.U)
      c.io.out.expect(true.B)

    }
  }
}