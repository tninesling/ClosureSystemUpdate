package basis

object LaRochelleExample {
  def main(args: Array[String]) = {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val r = t.reduce()
    println("Table reduced")

    val family = r.mooreFamily()
    println("Moore family generated")

    val cdb = r.buildCdBasis(family)
    println(cdb.toString())
  }
}
