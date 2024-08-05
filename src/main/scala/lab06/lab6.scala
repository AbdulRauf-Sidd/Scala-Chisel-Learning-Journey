package lab06

import chisel3._
import chisel3.util._


class shift_register ( val init : Int = 1) extends Module {
    val io = IO(new Bundle {
        val serialin = Input (Bool())
        val paralellin = Input(UInt(4.W))
        val serialen = Input(Bool())
        val out = Output ( UInt (4. W ) )
    })
    val state = RegInit(init.U(4.W))

    when (io.serialen === true.B) {
        val nextState = ( state << 1) | io.serialin
        state := nextState
    }.otherwise {
        state := state.bitSet(0.U, io.paralellin(0))
        state := state.bitSet(1.U, io.paralellin(1))
        state := state.bitSet(2.U, io.paralellin(2))
        state := state.bitSet(3.U, io.paralellin(3))
    }

    io.out := state
}


class counter ( val max : Int , val min : Int = 0) extends Module {
    val io = IO ( new Bundle {
        val out = Output ( UInt ( log2Ceil ( max ) . W ) )
    })
    
    val counter = RegInit ( min . U ( log2Ceil ( max ) . W ) )
    val count_buffer = Mux ( (isPow2( max) && (min==0)).B, counter +1. U , Mux (counter ===
    max .U , min .U , counter +1. U ))
}


class twoHotTimer extends Module {
    val io = IO (new Bundle {
        val din = Input(UInt(8.W))
        val reload = Input(Bool())
        val count = Output(UInt(8.W))
        val out = Output(Bool())
    })

    val timer_count = RegInit(255.U(8.W))
    val done = Wire(Bool())
    val reg = RegInit(false.B)
    val next = WireInit(0.U(8.W))


    when (io.reload) {
        next := io.din
    }.elsewhen (!done) {
        next := timer_count - 1.U
    }

    timer_count := next
    done := timer_count === 0.U

    when (done || reg) {
        when (reg) {
            reg := false.B
        }.otherwise {
            reg := true.B
        }
        io.out := true.B
    }.otherwise {
        io.out := false.B
    }

    io.count := timer_count
}
