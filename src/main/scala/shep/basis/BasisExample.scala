package shep.basis

import shep._
import shep.logging.LoggingBasis

object BasisExample {
  def main(args: Array[String]) = {

    val cs1 = new ClosureSystem with NaiveCanonicalDirectBasis with LoggingBasis
    val cs2 = new ClosureSystem with CanonicalDirectBasis with LoggingBasis

    cs1.fromFile("./data/largeExample/basis.txt")
    cs2.fromFile("./data/largeExample/basis.txt")

    //val origFamily = cs2.mooreFamily()
    //println(s"Original Moore family: ${origFamily.toString()}")

    cs1.update(Set("4", "17"))
    cs2.update(Set("4", "17"))

    //val newFamily = cs2.mooreFamily()
    //println(s"Updated Moore family: ${newFamily.toString()}")

    //println(s"New closed sets: ${(newFamily &~ origFamily).toString()}")

  }
}
