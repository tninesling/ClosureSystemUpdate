package basis

object LaRochelleExample {
  def main(args: Array[String]) = {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val r = t.reduce()

    val time1 = System.currentTimeMillis()
    val db = r.buildDBasis()
    val timeDif = System.currentTimeMillis() - time1

    println(s"Size: ${db.basis.size}")
    println(s"Computed in: ${timeDif} ms\n")

    val target = new DBasis
    target.fromFile("./src/test/data/example3/FormattedTargetDbasis.txt")

    assert(db.basisEquals(target))

  }
}
