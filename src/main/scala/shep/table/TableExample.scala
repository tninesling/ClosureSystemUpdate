package shep.table

import shep._
import shep.basis._

object TableExample {
  def main(args: Array[String]) = {
    val t = new Table
    /*t.fromFile("./data/largeExample/table.csv")

    val r = t.reduce()

    r.printFamily()*/
    val family = Set(Set[String](), Set("1"), Set("2"), Set("6"), Set("1","2","3"),
                     Set("1","2","3","4"), Set("1","2","3","5"), Set("1","2","3","4","5","6"))

    val basis1 = t.buildCdBasis(family)
    println(basis1.toString())

    val basis2 = t.buildCdBasis(family - Set("1","2","3"))
    println(basis2.toString())

    val diffBasis = new ClosureSystem with CanonicalDirectBasis
    diffBasis.basis = basis1.basis &~ basis2.basis
    println(diffBasis.toString())
  }
}
