package basis

object LaRochelleExample {
  def main(args: Array[String]) = {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val r = t.reduce()

    val time1 = System.currentTimeMillis()
    val cdb = r.buildCdBasis()
    val timeDif = System.currentTimeMillis() - time1

    println(s"Size: ${cdb.basis.size}")
    println(s"Computed in: ${timeDif} ms\n")
    val db = cdb.toDbasis()

    val target = new DBasis
    target.fromFile("./src/test/data/example3/FormattedTargetDbasis.txt")
    val dBasisEquivs = Set(Implication(Set(""), Set("6")), Implication(Set("7"), Set("8")), Implication(Set("8"), Set("7")))

    println(s"Equivs: ${db.equivalences}")

    val missing = target.basis &~ db.basis
    println(s"Missing: ${missing.size}")
    val extra = db.basis &~ target.basis
    println(s"Extra: ${extra.size}")

    val allHold = extra.filterNot(x => r.holds(x))
    println(allHold.size)
    /*
    val binaryCheck = target.binary &~ db.binary
    println(s"binMissing: ${binaryCheck.size}")

    val refineCheck = db.filterRefinements(target.binary, extra, missing)
    println(s"Refine check: ${refineCheck.size}")
    */
  }
}
