package lab05
import chisel3._
import org.scalatest._
import chiseltest._

class Lab05Ex01Test extends FreeSpec with ChiselScalatestTester {
    "Lab#05 Exercise#01 Test" in{
        test(new ALU(32)){ob1 =>
        ob1.io.arg_x(10.U)
        ob1.io.arg_y(8.U)
        ob1.io.alu_oper.poke("b0000".U)
        ob1.clock.step(5)
        ob1.io.alu_out.expect(8.U)
        }
    }
}

class Lab05Ex02Test extends FreeSpec with ChiselScalatestTester {
    "Lab#05 Exercise#02 Test" in{
        test(new eMux(UInt(4.W))){ob1 =>
        ob1.io.in1(10)
        ob1.io.in2(8)
        ob1.io.sel(0.B)
        ob1.clock.step(3)
        ob1.io.out.expect(10.U)
        }
    }
}

class Lab05Ex03Test extends FreeSpec with ChiselScalatestTester {
  "Lab05Ex03Test" in {
    test(new Operator(2, UInt(16.W))(_ + _)) { obj =>
      obj.io.in1(0).poke(3.U)
      obj.io.in1(1).poke(7.U)
      obj.io.in2(0).poke(5.U)
      obj.io.in2(1).poke(2.U)
      obj.clock.step(1)
      obj.io.out(0).expect(8.U)
      obj.io.out(1).expect(9.U)
    }
  }
}

class Lab05Ex04Test extends FreeSpec with ChiselScalatestTester {
  "Lab05Ex04Test" in {
    test(new Adder(8)) { obj =>
      obj.io.in0.poke(3.S)
      obj.io.in1.poke(-7.S)
      obj.clock.step(1)
      obj.io.sum.expect(-4.S)
    }
  }
}

// class Lab05Ex05Test extends FreeSpec with ChiselScalatestTester {
//   "Lab05Ex05Test" in {
//     test(new Router(UInt(), 32)) { obj =>
//       // Test case where addr is 10
//       obj.io.in.addr.poke(10.U)
//       obj.io.in.data.poke(123.U)
//       obj.clock.step(1)
//       obj.io.out.expect(123.U)

      
//     }
//   }
// }

