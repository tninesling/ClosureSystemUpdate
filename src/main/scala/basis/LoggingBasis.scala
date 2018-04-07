package basis

import org.slf4j.LoggerFactory

trait LoggingBasis extends Basis {

  val logger = LoggerFactory.getLogger(classOf[LoggingBasis])

  abstract override def update(closedSet: Set[String]) = {
    val currentBasis = basis

    val startMillis = System.currentTimeMillis()
    super.update(closedSet)
    val time = System.currentTimeMillis() - startMillis

    logger.info(s"Updated with set ${closedSet.toString()}")
    logger.info(s"Added implications: ${basis &~ currentBasis}")
    logger.info(s"Update took ${time} milliseconds")
  }
}
