package lab07

import chisel3._
import chisel3.util._

class SimpleArbiter[T <: Data](gen: T) extends Module {
  val io = IO(new Bundle {
    val in0 = Flipped(Decoupled(gen))
    val in1 = Flipped(Decoupled(gen))
    val out = Decoupled(gen)
  })

  val arbiter = Module(new Arbiter(gen, 2))

  arbiter.io.in(0) <> io.in0
  arbiter.io.in(1) <> io.in1
  io.out <> arbiter.io.out
}


class ArbiterWithQueues[T <: Data](gen: T) extends Module {
  val io = IO(new Bundle {
    val in0 = Flipped(Decoupled(gen))
    val in1 = Flipped(Decoupled(gen))
    val out = Decoupled(gen)
  })

  val queue0 = Module(new Queue(gen, 4))
  val queue1 = Module(new Queue(gen, 4))
  val arbiter = Module(new SimpleArbiter(gen))

  queue0.io.enq <> io.in0
  queue1.io.enq <> io.in1

  arbiter.io.in0 <> queue0.io.deq
  arbiter.io.in1 <> queue1.io.deq

  io.out <> arbiter.io.out
}

import chisel3._
import chisel3.util._

class FSM extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(2.W))
    val out = Output(Bool())
  })

  // 00 = f1, 01 = f2, 10 = r1, 11 = r2

  val s0 :: s1 :: s2 :: s3 :: s4 :: s5 :: Nil = Enum(6)
  val state = RegInit(s0)

  

  switch(state) {
    is(s0) {
      when(io.in === 0.U) {
        state := s1
      }.elsewhen(io.in === 1.U) {
        state := s5
      }
      io.out := 0.U
    }
    is(s1) {
      when(io.in === 0.U) {
        state := s3
      }.elsewhen(io.in === 2.U) {
        state := s0
      }
      io.out := 0.U
    }
    is(s2) {
      when(io.in === 0.U) {
        state := s3
      }.elsewhen (io.in === 2.U) {
        state := s1
      }
      io.out := 3.U
    }
    is(s3) {
      state := s0
      io.out := 0.U
    }
    is(s4) {
      when(io.in === 1.U) {
        state := s3
      }.elsewhen (io.in === 3.U) {
        state := s5
      }
      io.out := 7.U
    }
    is(s5) {
      when(io.in === 2.U) {
        state := s4
      }.elsewhen (io.in === 3.U) {
        state := s0
      }
      io.out := 0.U
    }
  }
}


class Manchester_Encoding extends Module {
    val io = IO (new Bundle {
        val in = Input(UInt(1.W))
        val start = Input(Bool())
        val out = Output(UInt(8.W))
        val flag = Output(UInt(1.W))
    })

    val store = RegInit(0.U(8.W))
    val s0 :: s1 :: Nil = Enum(2)
    val state = RegInit(s0)
    val counter = RegInit(0.U(3.W))

    //s0 = last low to high
    //s1 = last high to low

    when (io.start) {
        switch(state) {
            is(s0) {
                when (io.in === 1.U) {
                    state := s1
                    // store := (store << 1).asUInt 
                    store := Cat(store(6, 0), 0.U(1.W)).asUInt
                    // store := store & "b11111110".U 
                }.otherwise {
                    store := Cat(store(6, 0), 1.U(1.W)).asUInt
                    // store := store & "b11111111".U 
                }
            }
            is (s1) {
                when (io.in === 1.U) {
                    state := s0
                    store := Cat(store(6, 0), 1.U(1.W)).asUInt 
                }.otherwise {
                    store := Cat(store(6, 0), 0.U(1.W)).asUInt 
                }
            }
        }
        counter := counter + 1.U
    }

    io.flag := counter === 0.U
    io.out := store
}
