package single_cycle

import chisel3._
import org.scalatest._
import chiseltest._

class Control_Unit_Test extends FreeSpec with ChiselScalatestTester {
    "Control Unit Test" in{
        test(new Control_Unit){ob1 =>
        ob1.io.in.poke("b1100011".U)
        ob1.clock.step(1)

        ob1.io.memWrite.expect(false.B)
        ob1.io.branch.expect(true.B)
        ob1.io.memRead.expect(false.B)
        ob1.io.regWrite.expect(false.B)
        ob1.io.memtoReg.expect(false.B)
        ob1.io.aluOperation.expect(2.U)
        ob1.io.operand_A_sel.expect(0.U)
        ob1.io.operand_B_sel.expect(false.B)
        ob1.io.extend_sel.expect(0.U)
        ob1.io.next_PC_sel.expect(1.U)
        }
    }
}


class ALU_Test extends FreeSpec with ChiselScalatestTester {
    "ALU Test" in{
        test(new ALU){ob1 =>
        ob1.io.in1.poke(1.S)
        ob1.io.in2.poke(-4.S)
        ob1.io.alu_control.poke(2.U)
        ob1.clock.step(1)

        ob1.io.output.expect(1.S)
        ob1.io.branch.expect(false.B)
        }
    }
}


class ALU_Control_Test extends FreeSpec with ChiselScalatestTester {
    "ALU Control Test" in{
        test(new ALUControl){ob1 =>
        ob1.io.ALUOp.poke(4.U)
        ob1.io.funct3.poke(2.U)
        ob1.io.funct7.poke(0.U)
        ob1.clock.step(1)

        ob1.io.outputSignal.expect(0.U)
        }
    }
}



class RegisterFileTest extends FreeSpec with ChiselScalatestTester {
  "Register File Test" in {
    test(new RegisterFile) { c =>
      c.io.regwrite.poke(true.B)
      c.io.writereg.poke(5.U)
      c.io.writedata.poke(42.S)
      c.io.clear.poke(false.B)
      c.clock.step(1)

      c.io.read1.poke(5.U)
      c.io.read2.poke(5.U)
      c.io.readdata1.expect(42.S)
      c.io.readdata2.expect(42.S)

      c.io.clear.poke(true.B)
      c.clock.step(1)

      c.io.read1.poke(5.U)
      c.io.readdata1.expect(0.S)
      c.io.read2.poke(0.U)
      c.io.readdata2.expect(0.S)
    }
  }
}



class RAMTest extends FreeSpec with ChiselScalatestTester {
  "RAM Test" in {
    test(new RAM) { c =>

      c.io.Addr.poke(42.U)
      c.io.wrData.poke(123.S)
      c.io.wr_en.poke(true.B)
      c.io.rd_en.poke(false.B)
      c.clock.step(1)

      c.io.wr_en.poke(false.B)
      c.io.rd_en.poke(true.B)
      c.clock.step(1)
      c.io.out.expect(123.S)


      c.io.Addr.poke(42.U)
      c.io.wrData.poke(-456.S)
      c.io.wr_en.poke(true.B)
      c.io.rd_en.poke(false.B)
      c.clock.step(1)


      c.io.wr_en.poke(false.B)
      c.io.rd_en.poke(true.B)
      c.clock.step(1)
      c.io.out.expect(-456.S)

      c.io.Addr.poke(0.U)
      c.io.wr_en.poke(false.B)
      c.io.rd_en.poke(true.B)
      c.clock.step(1)
      c.io.out.expect(0.S)

      c.io.Addr.poke(1.U)
      c.io.wr_en.poke(false.B)
      c.io.rd_en.poke(true.B)
      c.clock.step(1)
      c.io.out.expect(0.S)
    }
  }
}

class Imm_Gen_Test extends FreeSpec with ChiselScalatestTester {
  "Imm_Gen_Test" in {
    test(new Immediate_Generation){c =>
      c.io.inst.poke("b00000000000100000010000000100011".U)
      c.io.SType.expect(0.S)
      c.io.IType.expect(1.S)
    }
  }
}

class Single_Cycle_Test extends FreeSpec with ChiselScalatestTester {
    "Single Cycle Test" in{
        test(new Processor){ob1 =>
        // ob1.RAM.memory(0).poke(16.S)
        // ob1.io.reset.poke(1.B)

        ob1.clock.step(1)
        // ob1.io.reset.poke(0.B)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        //ob1.pc.io.out.expect(4.U)
        // ob1.io.funct3.poke(1.U)
        // ob1.io.funct7.poke(0.U)
        // ob1.clock.step(1)
        //0010011, 000

        // ob1.io.outputSignal.expect(1.U)
        }
    }
}