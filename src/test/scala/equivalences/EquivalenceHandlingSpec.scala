package equivalences

import basis.CanonicalDirectBasis
import basis.syntax.implication._
import basis.Table
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax.equivalenceclass._

class EquivalenceHandlingSpec extends FlatSpec with Matchers {

  val testTable = new Table
  testTable.fromFile("./src/test/data/basisbuildingexample/Original10x22.csv")

  "The implications between the equivalence partitions" should "be b -> c, c -> b" in {
    val cdb = new CanonicalDirectBasis with EquivalenceHandling
    cdb.baseSet = Set("a", "b")
    cdb.equivalences = Set("a".eqClass, "b" <=> "c")

    cdb.implicationsBetweenPartitions(_.subsetOf(Set("a", "b"))) should equal (Set(
      "b" --> "c", "c" --> "b"
    ))
  }

  "The standard system" should "be converted to the reduced system" in {
    val cdb = new CanonicalDirectBasis with EquivalenceHandling
    cdb.baseSet = Set("1", "2", "3")
    cdb.equivalences = Set("1".eqClass, "2".eqClass, "3".eqClass, "4" <=> Set("1", "2", "3"))

    cdb.convertStandardToReducedSystem()

    cdb.equivalences should equal (Set("1".eqClass, "2".eqClass, "3".eqClass, "4".eqClass))
    cdb.basis should equal (Set("4" --> "1", "4" --> "2", "4" --> "3", Set("1", "2", "3") --> "4"))
  }

  "The reduced system" should "be converted to the standard system" in {
    val cdb = new CanonicalDirectBasis with EquivalenceHandling
    cdb.baseSet = Set("1", "2", "3", "4")
    cdb.equivalences = cdb.baseSet.map(_.eqClass)
    cdb.basis = Set("4" --> "1", "4" --> "2", "4" --> "3", Set("1", "2", "3") --> "4")

    cdb.convertReducedToStandardSystem()

    cdb.equivalences should equal (Set("1".eqClass, "2".eqClass, "3".eqClass, "4" <=> Set("1", "2", "3")))
    cdb.basis.isEmpty should be (true)
  }

  "The target Canonical Direct Basis" should "be correctly generated for the first row" in {
    val generatedBasis = testTable.takeNRows(1).buildEqCdb()

    val targetBasis = testTable.takeNRows(1)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first two rows" in {
    val generatedBasis = testTable.takeNRows(2).buildEqCdb()

    val targetBasis = testTable.takeNRows(2)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    println(s"Old basis: ${targetBasis.basis}")

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first three rows" in {
    val generatedBasis = testTable.takeNRows(3).buildEqCdb()

    val targetBasis = testTable.takeNRows(3)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    println(s"Missing: ${targetBasis.basis &~ generatedBasis.basis}")
    println(s"Extra: ${generatedBasis.basis &~ targetBasis.basis}")

    generatedBasis should equal (targetBasis)
  }

  "The target DBasis" should "be correctly generated for the first row" in {
    val generatedBasis = testTable.takeNRows(1).buildEqDBasis()

    val targetBasis = testTable.takeNRows(1)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first two rows" in {
    val generatedBasis = testTable.takeNRows(2).buildEqDBasis()

    val targetBasis = testTable.takeNRows(2)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first three rows" in {
    val generatedBasis = testTable.takeNRows(3).buildEqDBasis()

    val targetBasis = testTable.takeNRows(3)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    //println(s"Missing: ${targetBasis.basis &~ generatedBasis.basis}")
    //println(s"Extra: ${generatedBasis.basis &~ targetBasis.basis}")

    generatedBasis should equal (targetBasis)
  }/*
  it should "be correctly generated for the first four rows" in {
    val generatedBasis = testTable.takeNRows(4).buildEqDBasis()

    val targetBasis = testTable.takeNRows(4)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first five rows" in {
    val generatedBasis = testTable.takeNRows(5).buildEqDBasis()

    val targetBasis = testTable.takeNRows(5)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first six rows" in {
    val generatedBasis = testTable.takeNRows(6).buildEqDBasis()

    val targetBasis = testTable.takeNRows(6)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.basis should equal (targetBasis)
  }
  it should "be correctly generated for the first seven rows" in {
    val generatedBasis = testTable.takeNRows(7).buildEqDBasis()

    val targetBasis = testTable.takeNRows(7)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.basis should equal (targetBasis)
  }
  it should "be correctly generated for the first eight rows" in {
    val generatedBasis = testTable.takeNRows(8).buildEqDBasis()

    val targetBasis = testTable.takeNRows(8)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.basis should equal (targetBasis)
  }
  it should "be correctly generated for the first nine rows" in {
    val generatedBasis = testTable.takeNRows(9).buildEqDBasis()

    val targetBasis = testTable.takeNRows(9)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.basis should equal (targetBasis)
  }
  it should "be correctly generated for all ten rows" in {
    // This equivalence check is fine since the standard and reduced systems are the same
    val generatedBasis = testTable.buildEqDBasis()

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/10.txt")

    generatedBasis.basis should equal (targetBasis)
  }
*/
}
