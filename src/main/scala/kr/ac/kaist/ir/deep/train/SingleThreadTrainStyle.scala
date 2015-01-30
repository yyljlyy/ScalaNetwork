package kr.ac.kaist.ir.deep.train

import kr.ac.kaist.ir.deep.fn.{WeightSeqOp, WeightUpdater}
import kr.ac.kaist.ir.deep.network.Network

/**
 * __Trainer__ : Stochastic-Style, Single-Threaded
 *
 * @param net __Network__ to be trained
 * @param algorithm Weight __update algorithm__ to be applied
 * @param param __Training criteria__ (default: [[kr.ac.kaist.ir.deep.train.SimpleTrainingCriteria]])
 */
class SingleThreadTrainStyle[IN, OUT](protected[train] override val net: Network,
                                      protected[train] override val algorithm: WeightUpdater,
                                      protected[train] override val param: TrainingCriteria = SimpleTrainingCriteria())
  extends TrainStyle[IN, OUT] {

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
   * @param make Set of input operations
   */
  override protected[train] def batch(make: ManipulationType[IN, OUT]): Unit = {
    val seq = trainingSet(param.miniBatch).par.map {
      pair ⇒ (make corrupted pair._1) → pair._2
    }.seq
    make roundTrip(net, seq)
  }
}