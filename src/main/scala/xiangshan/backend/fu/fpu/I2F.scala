package xiangshan.backend.fu.fpu
import chisel3._
import chisel3.util._
import xiangshan.backend.fu.{FuConfig, FuncUnit, HasPipelineReg}
import org.chipsalliance.cde.config.Parameters
import xiangshan.{FPUCtrlSignals, HasXSParameter, XSModule}
import yunsuan.scalar.INT2FP

class I2FCVT(cfg: FuConfig)(implicit p: Parameters) extends FuncUnit(cfg) with HasPipelineReg {
  override def latency: Int = cfg.latency.latencyVal.get
  val i2f = Module(new INT2FP(latency,XLEN))
  i2f.io.src := io.in.bits.data.src(0)
  i2f.io.opType := io.in.bits.ctrl.fuOpType
  i2f.io.rm := io.frm.get
  i2f.io.wflags := io.in.bits.ctrl.fpu.getOrElse(0.U.asTypeOf(new FPUCtrlSignals)).wflags
  i2f.io.rmInst := io.in.bits.ctrl.fpu.getOrElse(0.U.asTypeOf(new FPUCtrlSignals)).rm
  i2f.regEnables <> VecInit((1 to latency) map (i => regEnable(i)))

  io.out.bits.res.data := i2f.io.result
  io.out.bits.res.fflags.get := i2f.io.fflags
}


