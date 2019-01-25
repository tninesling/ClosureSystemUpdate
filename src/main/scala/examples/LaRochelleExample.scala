package examples

import basis._

object LaRochelleExample {
  def main(args: Array[String]) = {
    val updateSet = Set("6", "7", "8", "17")
    val db = new DBasis
    db.fromFile("./src/test/data/example3/FormattedTargetDbasis.txt")

    //println("With 10:")
    //db.basis.filter(_.conclusion.contains("10")).foreach(println)

    val dbStart = System.currentTimeMillis()
    db.update(updateSet)
    val dbTime = System.currentTimeMillis() - dbStart

    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example3/FormattedLaRochelleCdBasis.txt")

    //println("With 10:")
    //db.basis.filter(_.conclusion.contains("10")).foreach(println)

    val cdbStart = System.currentTimeMillis()
    cdb.update(updateSet)
    val cdbTime = System.currentTimeMillis() - cdbStart

    val target = new DBasis
    target.fromFile("./src/test/data/example4/BasisExtendedBy6_7_8_17.txt")

    val extra = db.basis &~ target.basis
    val missing = target.basis &~ db.basis

    println(s"Extra: $extra")
    println(s"Missing: $missing")
    println(s"Dbasis Time: $dbTime ms")
    println(s"Canonical Direct Time: $cdbTime ms")
  }
}
