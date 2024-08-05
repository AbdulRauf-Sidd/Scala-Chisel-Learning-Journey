package lab03

import chisel3._
import chisel3.util._
import ALUOP._


object ALUOP {
// ALU Operations , may expand / modify in future
    val ALU_ADD = 0. U (4. W )
    val ALU_SUB = 1. U (4. W )
    val ALU_AND = 2. U (4. W )
    val ALU_OR = 3. U (4. W )
    val ALU_XOR = 4. U (4. W )
    val ALU_SLT = 5. U (4. W )
    val ALU_SLL = 6. U (4. W )
    val ALU_SLTU = 7. U (4. W )
    val ALU_SRL = 8. U (4. W )
    val ALU_SRA = 9. U (4. W )
    val ALU_COPY_A = 10. U (4. W )
    val ALU_COPY_B = 11. U (4. W )
    val ALU_XXX = 15.U(4. W )
}

trait Config {
// word length configuration parameter
    val WLEN = 32
// ALU operation control signal width
    val ALUOP_SIG_LEN = 4
}

class EncoderIO extends Bundle {
    val in = Input ( UInt (4.W ) )
    val out = Output ( UInt (2.W ) )
}

class Encoder4to2 extends Module {
    val io = IO ( new EncoderIO )
    io.out := 0.U
    switch (io.in) {
        is ("b0001".U) {
            io.out := "b00".U
        }
        is ("b0010".U) {
            io.out := "b01".U
        }
        is ("b0100".U) {
            io.out := "b10".U
        }
        is ("b1000".U) {
            io.out := "b11".U
        }
    }
}


class ALUIO extends Bundle with Config {
    val in_A = Input ( UInt ( WLEN . W ) )
    val in_B = Input ( UInt ( WLEN . W ) )
    val alu_Op = Input ( UInt ( ALUOP_SIG_LEN . W ) )
    val out = Output ( UInt ( WLEN . W ) )
    val sum = Output ( UInt ( WLEN . W ) )
    }

class ALU extends Module with Config {
    val io = IO ( new ALUIO )
    val sum = io . in_A + Mux ( io . alu_Op (0) , -io.in_B , io . in_B )
    val cmp = Mux ( io . in_A ( WLEN -1) === io . in_B ( WLEN -1) , sum ( WLEN -1) ,
    Mux ( io . alu_Op (1) , io . in_B ( WLEN -1) , io . in_A ( WLEN -1) ) )
    val shamt = io . in_B (4 ,0) . asUInt
    val shin = Mux ( io . alu_Op (3) , io . in_A , Reverse ( io . in_A ) )
    val shiftr = (Cat ( io . alu_Op (0) && shin ( WLEN -1) , shin ) . asSInt >> shamt ) (WLEN -1 , 0)
    val shiftl = Reverse ( shiftr )
    var out = 0.U
    switch (io.alu_Op) {
        is (ALU_ADD) {
            out = sum
        }
        // is (ALU_SUB) {
        //     out = sum
        // }
        // is (ALU_SRA) {
        //     out = shiftr
        // }
        // is (ALU_SRL) {
        //     out = shiftr
        // }
        // is (ALU_SLT) {
        //     out = cmp
        // }
        // is (ALU_SLTU) {
        //     out = cmp
        // }
        // is (ALU_SLL) {
        //     out = shiftl
        // }
        // is (ALU_AND) {
        //     out = io.in_A & io.in_B
        // }
        // is (ALU_OR) {
        //     out = io.in_A | io.in_B
        // }
        // is (ALU_XOR) {
        //     out = io.in_A ^ io.in_B
        // }
        // is (ALU_COPY_A) {
        //     out = io.in_A
        // }
        // is (ALU_COPY_B) {
        //     out = io.in_B
        // }
    }
        
    io.out := out
    io.sum := sum
}


class LM_IO_Interface_BranchControl extends Bundle {
  val fnct3 = Input(UInt(3.W))
  val branch = Input(Bool())
  val arg_x = Input(UInt(32.W))
  val arg_y = Input(UInt(32.W))
  val br_taken = Output(Bool())
}

class BranchControl extends Module {
  val io = IO(new LM_IO_Interface_BranchControl)


  io.br_taken := false.B

  when(io.branch) {
    switch(io.fnct3) {
      is("b000".U) { 
        io.br_taken := io.arg_x === io.arg_y
      }  
      is("b001".U) { io.br_taken := io.arg_x =/= io.arg_y } 
      is("b100".U) { io.br_taken := io.arg_x.asSInt < io.arg_y.asSInt }  
      is("b101".U) { io.br_taken := io.arg_x.asSInt >= io.arg_y.asSInt } 
      is("b110".U) { io.br_taken := io.arg_x < io.arg_y }  
      is("b111".U) { io.br_taken := io.arg_x >= io.arg_y } 
    }
  }
}

class LM_IO_Interface_ImmdValGen extends Bundle {
    val instr = Input ( UInt (32. W ) )
    val immd_se = Output ( UInt (32. W ) )
}

class ImmdValGen extends Module {
    val io = IO ( new LM_IO_Interface_ImmdValGen )
    // Start coding here
    val imm = io.instr(11, 0)
    when (imm(11) === 1.B) {
        io.immd_se := Cat("b11111111111111111111".U, imm)
    }.otherwise {
        io.immd_se := Cat("b00000000000000000000".U, imm)
    }
    // End your code here
}

class LM_IO_Interface_decoder_with_valid extends Bundle {
    val in = Input ( UInt (2. W ) )
    val out = Valid ( Output ( UInt (4. W ) ) )
}

class DecoderIO extends Bundle {
  val in = Input(UInt(2.W))
  val out = Valid(UInt(4.W))
}

class Decoder2to4 extends Module {
  val io = IO(new DecoderIO)

  val decode = Wire(UInt(4.W))
  decode := 0.U

  switch(io.in) {
    is(0.U) {
      decode := "b0001".U
    }
    is(1.U) {
      decode := "b0010".U
    }
    is(2.U) {
      decode := "b0100".U
    }
    is(3.U) {
      decode := "b1000".U
    }
  }

    io.out.bits := decode
    io.out.valid := true.B
}
