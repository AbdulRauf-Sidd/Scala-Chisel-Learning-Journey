file://<WORKSPACE>/src/test/scala/pipelined/test_pipelined.scala
### java.lang.IndexOutOfBoundsException: 0

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala3-library_3/3.3.3/scala3-library_3-3.3.3.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.12/scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 2683
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
      
      // Case 1: No hazards, branch control is active
      dut.io.ctrl_branch.poke(true.B)
      dut.io.id_rdsel.poke(1.U)
      dut.io.ex_rdsel.poke(3.U)
      dut.io.mem_rdsel.poke(3.U)
      dut.io.rs1_in.poke(1.U)
      dut.io.rs2_in.poke(3.U)
      

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(2.U)
      dut.io.sta.expect(false.B)

      // Case 2: ALU Hazard, branch control is active
      dut.io.id_rdsel.poke(2.U)
      dut.io.ex_rdsel.poke(2.U)
      dut.io.rs1_in.poke(2.U)
      dut.io.rs2_in.poke(2.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(1.U)

      // Case 3: MEM Hazard, branch control is active
      dut.io.id_rdsel.poke(4.U)
      dut.io.ex_rdsel.poke(3.U)
      dut.io.mem_rdsel.poke(4.U)
      dut.io.rs1_in.poke(4.U)
      dut.io.rs2_in.poke(4.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(1.U)
      dut.io.fwd2_out.expect(1.U)

      // Case 4: EX stage memory read
      dut.io.ctrl_branch.poke(true.B)
      dut.io.ex_memRead.poke(true.B)
      dut.io.ex_rdsel.poke(5.U)
      dut.io.rs1_in.poke(5.U)
      dut.io.rs2_in.poke(5.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(4.U)
      dut.io.fwd2_out.expect(4.U)

      //Case 5: MEM stage memory read
      dut.io.ctrl_branch.poke(true.B)
      dut.io.ex_memRead.poke(false.B)
      dut.io.mem_memRead.poke(true.B)
      dut.io.mem_rdsel.poke(6.U)
      dut.io.rs1_in.poke(6.U)
      dut.io.rs2_in.poke(6.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(5.U)
      dut.io.fwd2_out.expect(5.U)

      // Case 6: No branch control, forwarding for rs1 only
      dut.io.ctrl_branch.poke(false.B)
      dut.io.id_rdsel.poke(7.U)
      dut.io.ex_rdsel.poke(8.U)
      dut.io.mem_rdsel.poke(9.U)
      dut.io.rs1_in.poke(7.U)
      dut.io.rs2_in.poke(10.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(8.U)
      dut.io.fwd2_out.expect(0.U)

      // Case 7: No branch control, forwarding for rs2 only
      dut.io.rs1_in.poke(10.U)
      dut.io.rs2_in.poke(8.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(0.U)
      dut.io.fwd2_out.expect(0.U)

      // Case 8: No branch control, forwarding for rs1 from MEM stage
      dut.io.rs1_in.poke(9.U)
      dut.io.rs2_in.poke(10.U)

      dut.clock.step(1)
      dut.io.fwd1_out.expect(10.U)
      dut.io.fwd2_out.expect(0.U)



      //stall case

      dut.io.ctrl_branch.poke(@@)
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