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
    println(cdb.toString())
  }
}
