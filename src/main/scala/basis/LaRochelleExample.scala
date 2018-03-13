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

    println(s"Equivalences: ${db.equivalences.mkString(", ")}")
    println("Basis:")

    val condensed = cdb.basis//condense(cdb.basis)
    condensed.foreach(x => println(x.toString))

    /*
    val binaryCheck = target.binary &~ db.binary
    println(s"binMissing: ${binaryCheck.size}")

    val refineCheck = db.filterRefinements(target.binary, extra, missing)
    println(s"Refine check: ${refineCheck.size}")
    */
  }

  def condense(B: Set[Implication]) = {
    B.groupBy(_.premise)
      .map(_._2.foldLeft(
        Implication(Set.empty[String], Set.empty[String]))((x,y) =>
        Implication(x.premise | y.premise, x.conclusion | y.conclusion)
      )
    )
  }
}
