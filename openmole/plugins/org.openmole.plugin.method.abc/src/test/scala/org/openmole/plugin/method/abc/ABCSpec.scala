package org.openmole.plugin.method.abc

import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.random.Well1024a
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.openmole.core.dsl._
import org.openmole.core.workflow.test._
import org.scalatest.{ FlatSpec, Matchers }
import scala.util.Random

class ABCSpec extends FlatSpec with Matchers {

  import org.openmole.core.workflow.test.Stubs._

  val rng = new Random(42)

  val x1 = Val[Double]
  val x2 = Val[Double]
  val o1 = Val[Double]
  val o2 = Val[Double]

  // Gaussian Mixture toy model
  def toyModel(theta: Vector[Double], rng: util.Random): Vector[Double] = {
    val cov1: Array[Array[Double]] = Array(
      Array(1.0 / 2.0, -0.4),
      Array(-0.4, 1.0 / 2.0))
    val cov2: Array[Array[Double]] = Array(
      Array(1 / 100.0, 0.0),
      Array(0.0, 1 / 100.0))
    assert(new LUDecomposition(MatrixUtils.createRealMatrix(cov1)).getDeterminant() != 0)
    assert(new LUDecomposition(MatrixUtils.createRealMatrix(cov2)).getDeterminant() != 0)
    val mixtureWeights = Array(0.5, 0.5)
    val translate = 1
    val mean1 = theta.map { _ - translate }.toArray
    val mean2 = theta.map { _ + translate }.toArray
    val dist = new MixtureMultivariateNormalDistribution(
      mixtureWeights, Array(mean1, mean2), Array(cov1, cov2))
    dist.sample.toVector
  }

  val priors = Prior(
    UniformPrior(x1, -10, 10),
    UniformPrior(x2, -10, 10)
  )

  val observed = Array(
    ABC.Observed(o1, 0.0),
    ABC.Observed(o2, 0.0)
  )

  val testTask = TestTask { context ⇒
    val input = Vector(context(x1), context(x2))
    val Vector(o1Value, o2Value) = toyModel(input, rng)

    context + (o1 -> o1Value) + (o2 -> o2Value)
  } set (
    inputs += (x1, x2),
    outputs += (o1, o2)
  )

  val testTaskDeterministic = TestTask { context ⇒ context + (o1 -> context(x1)) + (o2 -> context(x2)) } set (
    inputs += (x1, x2),
    outputs += (o1, o2)
  )

  "abc map reduce" should "run" in {
    val abc =
      ABC(
        evaluation = testTask,
        prior = priors,
        observed = observed,
        sample = 10,
        generated = 10
      )

    abc run ()
  }

  "abc island" should "run" in {
    val abc =
      IslandABC(
        evaluation = testTask,
        prior = priors,
        observed = observed,
        sample = 10,
        generated = 10,
        parallelism = 10
      )

    abc run ()
  }

  "abc with a deterministic model" should "terminate" in {
    val abc =
      ABC(
        evaluation = testTaskDeterministic,
        prior = priors,
        observed = observed,
        sample = 10,
        generated = 10
      )

    abc run ()
  }
}
