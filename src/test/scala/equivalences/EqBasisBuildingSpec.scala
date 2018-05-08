package equivalences

import basis.DBasis
import basis.NaiveCanonicalDirectBasis
import basis.Table
import cats.Monoid
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax.equivalenceclass._
import syntax.eqimplication._

class EqBasisBuildingSpec extends FlatSpec with Matchers {

  val testTable = new Table
  testTable.fromFile("./src/test/data/basisbuildingexample/Original10x22.csv")

  "The target Canonical Direct Basis" should "be correctly generated for the first row" in {
    val generatedBasis = testTable.takeNRows(1).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(1)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first two rows" in {
    val generatedBasis = testTable.takeNRows(2).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(2)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first three rows" in {
    val generatedBasis = testTable.takeNRows(3).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(3)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first four rows" in {
    val generatedBasis = testTable.takeNRows(4).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(4)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first five rows" in {
    val generatedBasis = testTable.takeNRows(5).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(5)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first six rows" in {
    val generatedBasis = testTable.takeNRows(6).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(6)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first seven rows" in {
    val generatedBasis = testTable.takeNRows(7).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(7)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first eight rows" in {
    val generatedBasis = testTable.takeNRows(8).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(8)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first nine rows" in {
    val generatedBasis = testTable.takeNRows(9).buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.takeNRows(9)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for all ten rows" in {
    val generatedBasis = testTable.buildEqBasis(new NaiveCanonicalDirectBasis())

    val targetBasis = testTable.nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }

  "The target DBasis" should "be correctly generated for the first row" in {
    val generatedBasis = testTable.takeNRows(1).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(1)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first two rows" in {
    val generatedBasis = testTable.takeNRows(2).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(2)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first three rows" in {
    val generatedBasis = testTable.takeNRows(3).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(3)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first four rows" in {
    val generatedBasis = testTable.takeNRows(4).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(4)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first five rows" in {
    val generatedBasis = testTable.takeNRows(5).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(5)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first six rows" in {
    val generatedBasis = testTable.takeNRows(6).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(6)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first seven rows" in {
    val generatedBasis = testTable.takeNRows(7).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(7)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first eight rows" in {
    val generatedBasis = testTable.takeNRows(8).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(8)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for the first nine rows" in {
    val generatedBasis = testTable.takeNRows(9).buildEqBasis(new DBasis())

    val targetBasis = testTable.takeNRows(9)
      .nonConstant
      .uniqueSingletonClosures
      .buildNaiveCanonicalDirectBasis()
      .toDbasis()

    generatedBasis.reducedBasis should equal (targetBasis)
  }
  it should "be correctly generated for all ten rows" in {
    // This equivalence check is fine since the standard and reduced systems are the same
    val generatedBasis = testTable.buildEqBasis(new DBasis())

    val targetBasis = new DBasis
    targetBasis.fromHypergraphDualizationFile("./src/test/data/basisbuildingexample/10.txt")

    generatedBasis.reducedBasis should equal (targetBasis)
  }

}
