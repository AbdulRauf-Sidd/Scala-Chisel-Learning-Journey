file://<WORKSPACE>/src/test/scala/pipelined/test_pipelined.scala
### java.lang.IndexOutOfBoundsException: 0

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.3/scala3-library_3-3.3.3.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.12/scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 4398
uri: file://<WORKSPACE>/src/test/scala/pipelined/test_pipelined.scala
text:
```scala
package pipelined

import chisel3._
import org.scalatest._
import chiseltest._

class Branch_Forward_Unit_Test extends FreeSpec with ChiselScalatestTester {
  "Branch_Forward_Unit_Test" in {
    test(new Branch_Forward_Unit) { dut =>
      val inst1 = "b00000000001100001010000010010011".U // rs1 = 1, rs2 = 3

      val inst2 = "b00000000001000010010000010010011".U // rs1 = 2, rs2 = 2

      val inst3 = "b00000000010000100010000010010011".U // rs1 = 4, rs2 = 4

      val inst4 = "b00000000010100101010000010010011".U // rs1 = 5, rs2 = 5

      val inst5 = "b00000000011000110010000010010011".U // rs1 = 6, rs2 = 6

      val inst6 = "b00000000101000111010000010010011".U // rs1 = 7, rs2 = 10

      val inst7 = "b00000000100001010010000010010011".U // rs1 = 10, rs2 = 8

      val inst8 = "b00000000101001001010000010010011".U // rs1 = 9, rs2 = 10

      val inst9 = "b00000000011000100010000010010011".U // rs1 = 4, rs2 = 6
      
      // Case 1: No hazards, branch control is active
      dut.io.ctrl_branch.poke(true.B)
      dut.io.inst_in.poke(inst1)
      dut.io.id_rdsel.poke(1.U)
      dut.io.ex_rdsel.poke(3.U)
      dut.io.mem_rdsel.poke(3.U)
    //   dut.io.rs1_in.poke(1.U)
    //   dut.io.rs2_in.poke(3.U)
      

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(2.U)
    //   dut.io.sta.expect(false.B)

      // Case 2: ALU Hazard, branch control is active
      dut.io.id_rdsel.poke(2.U)
      dut.io.ex_rdsel.poke(2.U)
      dut.io.inst_in.poke(inst2)
    //   dut.io.rs1_in.poke(2.U)
    //   dut.io.rs2_in.poke(2.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(1.U)

      // Case 3: MEM Hazard, branch control is active
      dut.io.id_rdsel.poke(4.U)
      dut.io.ex_rdsel.poke(3.U)
      dut.io.mem_rdsel.poke(4.U)
      dut.io.inst_in.poke(inst3)
    //   dut.io.rs1_in.poke(4.U)
    //   dut.io.rs2_in.poke(4.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(1.U)

      // Case 4: EX stage memory read
      dut.io.ctrl_branch.poke(true.B)
      dut.io.ex_memRead.poke(true.B)
      dut.io.ex_rdsel.poke(5.U)
      dut.io.inst_in.poke(inst4)
    //   dut.io.rs1_in.poke(5.U)
    //   dut.io.rs2_in.poke(5.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(4.U)
      dut.io.fwd2_out.expect(4.U)

      //Case 5: MEM stage memory read
      dut.io.ctrl_branch.poke(true.B)
      dut.io.ex_memRead.poke(false.B)
      dut.io.mem_memRead.poke(true.B)
      dut.io.mem_rdsel.poke(6.U)
      dut.io.inst_in.poke(inst5)
    //   dut.io.rs1_in.poke(6.U)
    //   dut.io.rs2_in.poke(6.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(5.U)
      dut.io.fwd2_out.expect(5.U)

      // Case 6: No branch control, forwarding for rs1 only
      dut.io.ctrl_branch.poke(false.B)
      dut.io.id_rdsel.poke(7.U)
      dut.io.ex_rdsel.poke(8.U)
      dut.io.mem_rdsel.poke(9.U)
      dut.io.inst_in.poke(inst6)
    //   dut.io.rs1_in.poke(7.U)
    //   dut.io.rs2_in.poke(10.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(8.U)
      dut.io.fwd2_out.expect(0.U)

      // Case 7: No branch control, forwarding for rs2 only
    //   dut.io.rs1_in.poke(10.U)
    //   dut.io.rs2_in.poke(8.U)
      dut.io.inst_in.poke(inst7)
      dut.clock.step(1)
      dut.io.fwd1_out.expect(0.U)
      dut.io.fwd2_out.expect(0.U)

      // Case 8: No branch control, forwarding for rs1 from MEM stage
    //   dut.io.rs1_in.poke(9.U)
    //   dut.io.rs2_in.poke(10.U)
      dut.io.inst_in.poke(inst8)
      dut.clock.step(1)
      dut.io.fwd1_out.expect(10.U)
      dut.io.fwd2_out.expect(0.U)


      //stall case
      dut.io.ctrl_branch.poke(true.B)
      dut.io.id_memRead.poke(true.B)
      dut.io.id_rdsel.poke(6.U)
      dut.io.ex_rdsel.poke(3.U)
      dut.io.mem_rdsel.poke(3.U)
      dut.io.inst_in.poke(inst9)
    //   dut.io.rs1_in.poke(4.U)
    //   dut.io.rs2_in.poke(6.U)
      

      dut.clock.step(1)
      dut.io.fwd1_out.expect(0.U)
      dut.io.fwd2_out.expect(0.U)
      dut.io.ctrl_fwd_out.expect(1.B)
      dut.io.inst_fwd_out.expect(1.B)
      dut.io.pc_fwd_out.expect(1.B)

    }
  }
}




class Branch_Logic_Test extends FreeSpec with ChiselScalatestTester {
    "Branch_Logic Test" in{
        test(new Branch_Logic_Unit){ob1 =>
        // ob1.RAM.memory(0).poke(16.S)
        // ob1.io.reset.poke(1.B)
        ob1.rs1_in.poke(@@)
        ob1.clock.step(1)
        // ob1.io.reset.poke(0.B)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
        ob1.clock.step(1)
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


class Forward_Unit_Test extends FreeSpec with ChiselScalatestTester {
  "Forward_Unit_Test" in {
    test(new Forward_Unit) { dut =>

      // Case 1: EX stage forwarding to both rs1 and rs2
      dut.io.ex_regWrite_in.poke(true.B)
      dut.io.ex_rd_sel_in.poke(1.U)
      dut.io.rs1_sel_in.poke(1.U)
      dut.io.rs2_sel_in.poke(1.U)
      dut.io.mem_regWrite_in.poke(false.B)
      dut.io.mem_rd_sel_in.poke(0.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(1.U)
      dut.io.forward_b_out.expect(1.U)

      // Case 2: EX stage forwarding to rs1 only
      dut.io.ex_rd_sel_in.poke(2.U)
      dut.io.rs1_sel_in.poke(2.U)
      dut.io.rs2_sel_in.poke(3.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(1.U)
      dut.io.forward_b_out.expect(0.U)

      // Case 3: EX stage forwarding to rs2 only
      dut.io.ex_rd_sel_in.poke(3.U)
      dut.io.rs1_sel_in.poke(4.U)
      dut.io.rs2_sel_in.poke(3.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(0.U)
      dut.io.forward_b_out.expect(1.U)

      // Case 4: MEM stage forwarding to both rs1 and rs2
      dut.io.ex_regWrite_in.poke(false.B)
      dut.io.mem_regWrite_in.poke(true.B)
      dut.io.mem_rd_sel_in.poke(5.U)
      dut.io.rs1_sel_in.poke(5.U)
      dut.io.rs2_sel_in.poke(5.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(2.U)
      dut.io.forward_b_out.expect(2.U)

      // Case 5: MEM stage forwarding to rs1 only
      dut.io.mem_rd_sel_in.poke(6.U)
      dut.io.rs1_sel_in.poke(6.U)
      dut.io.rs2_sel_in.poke(7.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(2.U)
      dut.io.forward_b_out.expect(0.U)

      // Case 6: MEM stage forwarding to rs2 only
      dut.io.mem_rd_sel_in.poke(7.U)
      dut.io.rs1_sel_in.poke(8.U)
      dut.io.rs2_sel_in.poke(7.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(0.U)
      dut.io.forward_b_out.expect(2.U)

      // Case 7: No forwarding required
      dut.io.ex_regWrite_in.poke(false.B)
      dut.io.mem_regWrite_in.poke(false.B)
      dut.io.rs1_sel_in.poke(9.U)
      dut.io.rs2_sel_in.poke(10.U)

      dut.clock.step(1)
      dut.io.forward_a_out.expect(0.U)
      dut.io.forward_b_out.expect(0.U)

    }
  }
}

class Hazard_Detection_Unit_Test extends FreeSpec with ChiselScalatestTester {
  "Hazard_Detection_Unit_Test" in {
    test(new Hazard_Detection_Unit) { dut =>

      // Define some dummy instructions
      val inst1 = "b00000000001000001010000010010011".U // rs1 = 1, rs2 = 2
      

      // Case 1: No hazard detected
      dut.io.memRead_in.poke(false.B)
      dut.io.inst_in.poke(inst1)
      dut.io.rd_sel_in.poke(15.U) // rd != rs1 or rs2
      dut.io.current_pc_in.poke(10.U)
      dut.io.pc_in.poke(20.U)

      dut.clock.step(1)
      dut.io.ctrl_fwd_out.expect(0.B)
      dut.io.inst_fwd_out.expect(0.B)
      dut.io.pc_fwd_out.expect(0.B)
      dut.io.inst_out.expect(inst1)
      dut.io.pc_out.expect(20.U)
      dut.io.current_pc_out.expect(10.U)

      // Case 2: Hazard detected with rs1
      dut.io.memRead_in.poke(true.B)
      dut.io.rd_sel_in.poke(1.U) // rd == rs1

      dut.clock.step(1)
      dut.io.ctrl_fwd_out.expect(1.B)
      dut.io.inst_fwd_out.expect(1.B)
      dut.io.pc_fwd_out.expect(1.B)

      // Case 3: Hazard detected with rs2
      dut.io.rd_sel_in.poke(2.U) // rd == rs2

      dut.clock.step(1)
      dut.io.ctrl_fwd_out.expect(1.B)
      dut.io.inst_fwd_out.expect(1.B)
      dut.io.pc_fwd_out.expect(1.B)

      
      // Case 5: No hazard after hazard scenario
      dut.io.memRead_in.poke(false.B)
      dut.io.inst_in.poke(inst1)
      dut.io.rd_sel_in.poke(1.U) 

      dut.clock.step(1)
      dut.io.ctrl_fwd_out.expect(0.B)
      dut.io.inst_fwd_out.expect(0.B)
      dut.io.pc_fwd_out.expect(0.B)

    }
  }
}



class Pipelined_Test extends FreeSpec with ChiselScalatestTester {
    "Pipeline Test" in{
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
        ob1.clock.step(1)
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
```



#### Error stacktrace:

```
scala.collection.LinearSeqOps.apply(LinearSeq.scala:131)
	scala.collection.LinearSeqOps.apply$(LinearSeq.scala:128)
	scala.collection.immutable.List.apply(List.scala:79)
	dotty.tools.dotc.util.Signatures$.countParams(Signatures.scala:501)
	dotty.tools.dotc.util.Signatures$.applyCallInfo(Signatures.scala:186)
	dotty.tools.dotc.util.Signatures$.computeSignatureHelp(Signatures.scala:94)
	dotty.tools.dotc.util.Signatures$.signatureHelp(Signatures.scala:63)
	scala.meta.internal.pc.MetalsSignatures$.signatures(MetalsSignatures.scala:17)
	scala.meta.internal.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:51)
	scala.meta.internal.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:435)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: 0