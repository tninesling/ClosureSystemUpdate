package shep.logging

import shep.basis._

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait LoggingBasis extends Basis {

  val logger = LoggerFactory.getLogger(classOf[LoggingBasis])

  abstract override def update(closedSet: Set[String]) = {
    val affectedSets = basis.filter(imp =>
      imp._1.subsetOf(closedSet) &&
      !imp._2.subsetOf(closedSet)
    )

    val affectedSize = affectedSets.size
    val basisSize = basis.size

    val startMillis = System.currentTimeMillis()
    val result = super.update(closedSet)
    val time = System.currentTimeMillis() - startMillis

    logger.info(s"Updated with set ${closedSet.toString()}")
    logger.info(s"${affectedSize} of ${basisSize} implications broken")
    logger.info(s"Update took ${time} milliseconds")

    result
  }
}
