package kr.ac.kaist.ir.deep.train.style

import kr.ac.kaist.ir.deep.fn.WeightSeqOp
import kr.ac.kaist.ir.deep.fn.alg.WeightUpdater
import kr.ac.kaist.ir.deep.network.Network
import kr.ac.kaist.ir.deep.train._
import kr.ac.kaist.ir.deep.train.op.InputOp

/**
 * __Trainer__ : Stochastic-Style, Single-Threaded
 *
 * @param net __Network__ to be trained
 * @param algorithm Weight __update algorithm__ to be applied
 * @param param __Training criteria__ (default: [[kr.ac.kaist.ir.deep.train.SimpleTrainingCriteria]])
 */
class SingleThreadTrainStyle[IN](protected[train] override val net: Network,
                                 protected[train] override val algorithm: WeightUpdater,
                                 protected[train] override val param: TrainingCriteria = SimpleTrainingCriteria())
  extends TrainStyle[IN] {

  /**
   * Fetch weights
   *
   * @param iter current iteration
   */
  override protected[train] def fetch(iter: Int): Unit = {}

  /**
   * Send update of weights
   *
   * @param iter current iteration
   */
  override protected[train] def update(iter: Int): Unit = {
    net.dW :/= param.miniBatch.toDouble
    net.W -= net.dW
  }

  /**
   * Do mini-batch
   *
   * @param op Set of input operations
   */
  override protected[train] def batch(op: InputOp[IN]): Unit =
    trainingSet(param.miniBatch) foreach {
      pair ⇒ op roundTrip(net, op corrupted pair._1, pair._2)
    }
}
