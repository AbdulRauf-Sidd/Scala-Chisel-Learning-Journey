package lab02

import chisel3._
import org.scalatest._
import chiseltest._

class Lab02Ex01Test extends FreeSpec with ChiselScalatestTester{
    "Lab#02 Exercise#01 Test" in{
        test(new Mux_2to1){ob1 =>
        ob1.io.in_A.poke(42.U)
        ob1.io.in_B.poke(84.U)
        ob1.io.select.poke(false.B)
        ob1.clock.step(1)
        ob1.io.out.expect(42.U)
        }
    }

    "Lab#02 Exercise#01 Test2" in{
        test(new Mux_2to1){ob1 =>
        ob1.io.in_A.poke(42.U)
        ob1.io.in_B.poke(84.U)
        ob1.io.select.poke(true.B)
        ob1.clock.step(1)
        ob1.io.out.expect(84.U)
        }
    }
}

class Lab02Ex02Test extends FreeSpec with ChiselScalatestTester{
    "Lab#02 Exercise#02 Test" in{
        test(new MuxLookupNested){ob1 =>
            ob1.io.in0.poke(1.B)
            ob1.io.in1.poke(0.B)
            ob1.io.in2.poke(1.B)
            ob1.io.in3.poke(1.B)
            ob1.io.in4.poke(1.B)
            ob1.io.in5.poke(0.B)
            ob1.io.in6.poke(1.B)
            ob1.io.in7.poke(1.B)
            ob1.io.sel.poke(5.U)
            ob1.io.out.expect(1.B)
        }
    }
}

class Lab02Ex03Test extends FreeSpec with ChiselScalatestTester{
    "Lab#02 Exercise#03 Test" in{
        test(new mux_onehot_4to2){ob1 =>
            ob1.io.sel.poke(4.U)
            ob1.io.out.expect(2.U)
        }
    }
}

class Lab02Ex04Test extends FreeSpec with ChiselScalatestTester{
    "Lab#02 Exercise#04 Test" in{
        test(new Mux_5to1){ob1 =>
            ob1.io.s0.poke(0.B)
            ob1.io.s1.poke(1.B)
            ob1.io.s2.poke(0.B)
            ob1.io.out.expect(16.U)
        }
    }
}

class Lab02Ex05Test extends FreeSpec with ChiselScalatestTester {
  "Barrel Shift Test" in {
    test(new barrel_shift) { c =>
      // Test logical shift left by 1 (sel = 1, shift_type = 0)
      c.io.in(0).poke(false.B)
      c.io.in(1).poke(false.B)
      c.io.in(2).poke(false.B)
      c.io.in(3).poke(true.B)
      c.io.sel(0).poke(1.B)
      c.io.sel(1).poke(1.B)
      c.io.shift_type.poke(true.B)
      c.clock.step(100)
      c.io.out(0).expect(true.B)
      c.io.out(1).expect(false.B)
      c.io.out(2).expect(false.B)
      c.io.out(3).expect(false.B)
    }
  }
}


