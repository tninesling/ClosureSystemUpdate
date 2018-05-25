package examples

import basis._

object LaRochelleExample {
  def main(args: Array[String]) = {
    val db = new DBasis
    db.fromFile("./src/test/data/example4/FormattedOriginal.txt")

    //println("With 10:")
    //db.basis.filter(_.conclusion.contains("10")).foreach(println)

    db.update(Set("6", "7", "8", "17"))

    val target = new DBasis
    target.fromFile("./src/test/data/example4/FormattedExtendedBy6_7_8_17.txt")

    val extra = db.basis &~ target.basis
    val missing = target.basis &~ db.basis

    println(s"Extra: $extra")
    println(s"Missing: $missing")
  }
}
