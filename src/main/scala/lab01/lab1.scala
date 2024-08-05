package lab01
import chisel3._

class Counter(counterBits: UInt) extends Module {
    val io = IO(new Bundle {
        val result = Output(Bool())
    })

    val max = (1.S << counterBits) - 1.S
    val count = RegInit(0.S(16.W))

    when(count === max + 1.S) {
        count := 0.S
    }.otherwise {
        count := count + 1.S
    }

    io.result := count(counterBits)
}

class Counter2(counterBits: UInt) extends Module {
    val io = IO(new Bundle {
        val result = Output(SInt())
    })

    val max = (1.S << counterBits) - 1.S
    val count = RegInit(0.S(16.W))

    when(count(counterBits) === 1.B) {
        count := 0.S
    }.otherwise {
        count := count + 1.S
    }

    io.result := count
}


class Counter3(size: UInt, maxval: UInt) extends Module {
    val io = IO(new Bundle {
        val result = Output(SInt())
    })

    def gen(n: UInt, max: Int) = {
        val counter = RegInit(0.U(n.getWidth.W))

        when(counter === max.asUInt) {
            counter := 0.U
        }.otherwise {
            counter := counter + 1.U
        }
        counter
    }

    val counter1 = gen(size, maxval.asInstanceOf[Int])
    io.result := counter1.asSInt
}