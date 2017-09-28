package basis

object LaRochelleExample {
  def main(args: Array[String]) = {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val cdb = t.buildCdBasis()
    println(cdb.toString())
  }
}
