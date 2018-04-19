package equivalences

import basis.DBasis
import basis.Table
import cats.Monoid
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax.equivalenceclass._

class EqBasisBuildingSpec extends FlatSpec with Matchers {

  val testTable = new Table
  testTable.fromFile("./src/test/data/basisbuildingexample/Original10x22.csv")

  "The target DBasis" should "be correctly generated for the first row" in {
    val generatedBasis = testTable.takeNRows(1).buildEqBasis(new DBasis())

    val firstEquivClass = testTable.rowToClosedSet(testTable.rows.head)
      .foldLeft(Monoid[EquivalenceClass].empty)(_ <=> _)

    val secondEquivClass = testTable.header
      .toSet
      .diff(testTable.rowToClosedSet(testTable.rows.head))
      .foldLeft(Monoid[EquivalenceClass].empty)(_ <=> _)
    println(generatedBasis.equivalences)
    generatedBasis.equivalences should equal (Set(firstEquivClass, secondEquivClass))
    generatedBasis.reducedBasis.isEmpty should be (true)
    generatedBasis.bottomElement should be (firstEquivClass)
  }
  it should "be correctly generated for the first two rows" in {
    val generatedBasis = testTable.takeNRows(2).buildEqBasis(new DBasis())

    val firstEquivClass = "1" <=> "6" <=> "10" <=> "11" <=> "16" <=> "18" <=> "20" <=> "22"
    val secondEquivClass = "2" <=> "3" <=> "9" <=> "15"
    val thirdEquivClass = "4" <=> "5" <=> "7" <=> "8" <=> "12" <=> "19" <=> Set("2", "13")
    val fourthEquivClass = "13" <=> "14" <=> "17" <=> "21"

    println(testTable.takeNRows(2).toCsv)
    println(generatedBasis.reducedBasis.closure(Set("4")))
    println(generatedBasis.reducedBasis.closure(Set("2", "13")))
    println(generatedBasis.basis)
    println(generatedBasis.reducedBasis.basis)

    generatedBasis.equivalences should equal (Set(firstEquivClass, secondEquivClass, thirdEquivClass, fourthEquivClass))
    generatedBasis.reducedBasis.isEmpty should be (true)
    generatedBasis.bottomElement should be (firstEquivClass)
  }
  it should "be correctly generated for the first three rows" in {
    val generatedBasis = testTable.takeNRows(3).buildEqBasis(new DBasis())

    generatedBasis.reducedBasis.isEmpty should be (true)
  }
  it should "be correctly generated for the first four rows" in {
    val generatedBasis = testTable.takeNRows(4).buildEqBasis(new DBasis())

    generatedBasis.reducedBasis.isEmpty should be (true)
  }
  it should "be correctly generated for the first five rows" in {
    val generatedBasis = testTable.takeNRows(5).buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/5.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first six rows" in {
    val generatedBasis = testTable.takeNRows(6).buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/6.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first seven rows" in {
    val generatedBasis = testTable.takeNRows(7).buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/7.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first eight rows" in {
    val generatedBasis = testTable.takeNRows(8).buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/8.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first nine rows" in {
    val generatedBasis = testTable.takeNRows(9).buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/9.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for all ten rows" in {
    val generatedBasis = testTable.buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/10.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }

}
