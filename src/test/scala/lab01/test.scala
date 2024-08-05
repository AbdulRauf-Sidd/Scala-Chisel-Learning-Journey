package lab01

import chisel3._
import org.scalatest._
import chiseltest._

class Lab01Ex01Test extends FreeSpec with ChiselScalatestTester{
    "Lab#01 Exercise#01 Test" in{
        test(new Counter(2.U)){ob1 =>
        ob1.clock.step(4)
        ob1.io.result.expect(1.B)}
    }

}

class Lab01Ex02Test extends FreeSpec with ChiselScalatestTester{
    "Lab#01 Exercise#03 Test" in{
        test(new Counter2(2.U)){ob1 =>
        ob1.clock.step(5)
        ob1.io.result.expect(0.S)}
    }

}


class Lab01Ex03Test extends FreeSpec with ChiselScalatestTester{
    "Lab#01 Exercise#03 Test" in{
        test(new Counter3(4.U, 2.U)){ob1 =>
        ob1.clock.step(5)
        ob1.io.result.expect(2.S)}
    }

}