package lab03

import chisel3._
import org.scalatest._
import chiseltest._
import ALUOP._

class Lab03Ex01Test extends FreeSpec with ChiselScalatestTester{
    "Lab#03 Exercise#01 Test" in{
        test(new Encoder4to2){ob1 =>
        ob1.io.in.poke("b0001".U)
        ob1.io.out.expect("b00".U)
        }
    }

    "Lab#03 Exercise#01 Test2" in{
        test(new Encoder4to2){ob1 =>
        ob1.io.in.poke("b0100".U)
        ob1.io.out.expect("b10".U)
        }
    }
}



class ALUTester extends FreeSpec with ChiselScalatestTester {
//   "ALU Test" in {
//     test(c) { alu =>
//       def testALUOperation(inA: Int, inB: Int, aluOp: UInt, expectedOut: Int, expectedSum: Int): Unit = {
//         alu.io.in_A.poke(inA.U)
//         alu.io.in_B.poke(inB.U)
//         alu.io.alu_Op.poke(aluOp)
//         alu.clock.step(1)
//         alu.io.out.expect(expectedOut.U)
//         alu.io.sum.expect(expectedSum.U)
//       }

    "Lab#03 Exercise#02 Test" in{
        test(new ALU){ob1 =>
        ob1.io.in_A.poke(10.U)
        ob1.io.in_B.poke(8.U)
        ob1.io.alu_Op.poke(ALU_ADD)
        ob1.clock.step(10)
        ob1.io.out.expect(17.U)
        }
    }
}

class Lab03Ex03Test extends FreeSpec with ChiselScalatestTester{
    "Lab#03 Exercise#03 Test" in{
        test(new BranchControl){ob1 =>
        ob1.io.fnct3.poke(0.U)
        ob1.io.branch.poke(true.B)
        ob1.io.arg_x.poke(15.U)
        ob1.io.arg_y.poke(15.U)
        ob1.io.br_taken.expect(true.B)
        }
    }
}

class Lab03Ex04Test extends FreeSpec with ChiselScalatestTester{
    "Lab#03 Exercise#04 Test" in{
        test(new ImmdValGen){ob1 =>
        ob1.io.instr.poke("b11111111111111111111000000000001".U)
        ob1.io.immd_se.expect("b00000000000000000000000000000001".U)
        }
    }
}

class Lab03Ex05Test extends FreeSpec with ChiselScalatestTester{
  "Lab#03 Exercise#05 Test" in {
    test(new Decoder2to4) { ob1 =>
      ob1.io.in.poke("b00".U)
      ob1.io.out.bits.expect("b0001".U)
      ob1.io.out.valid.expect(true.B)
    }
  }
}