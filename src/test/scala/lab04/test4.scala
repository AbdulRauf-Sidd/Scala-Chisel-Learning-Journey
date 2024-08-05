package lab04

import chisel3._
import org.scalatest._
import chiseltest._
import ALUOP._

class Lab04Ex02Test extends FreeSpec with ChiselScalatestTester{
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

class Lab04Ex03Test extends FreeSpec with ChiselScalatestTester{
    "Lab#03 Exercise#04 Test" in{
        test(new ImmdValGen){ob1 =>
        ob1.io.instr.poke("b11111111111111111111000000000001".U)
        ob1.io.immd_se.expect("b00000000000000000000000000000001".U)
        }
    }
}

class Lab04Ex04Test extends FreeSpec with ChiselScalatestTester {
    "Lab#04 Exercise#04 Test" in{
        test(new ALU){ob1 =>
        ob1.io.in_A.poke(4.U)
        ob1.io.in_B.poke(2.U)
        ob1.io.alu_Op.poke(ALU_SRL)
        ob1.clock.step(5)
        ob1.io.out.expect(20.U)
        }
    }
}

