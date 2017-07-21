package tests

import shep._
import shep.basis._
import shep.table._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class TableSpec extends FlatSpec with Matchers {
  val testTable = new Table
  val columns = List(List(1,1,1), List(0,1,0), List(0,1,1), List(0,1,0))
  testTable.header = List("a", "b", "c", "d")
  testTable.columns = columns
  testTable.rows = testTable.transpose(columns)

  "The nonConstant function" should "return 3 non-constant columns" in {
    testTable.nonConstant().size shouldBe 3
  }

  "The uniqueSingletonClosures function" should "return 3 columns with unique singleton closures" in {
    testTable.uniqueSingletonClosures().size shouldBe 3
  }

  "The closure function" should "return column 0 for column 0" in {
    testTable.closure(Set(0)) should be (Set(0))
  }
  it should "return columns 0 and 2 for column 2" in {
    testTable.closure(Set(2)) should be (Set(0,2))
  }

  "The transpose of an m x n table" should "have dimensions n x m" in {
    val m = columns.head.size
    val n = columns.size

    val t = testTable.transpose(columns)
    t.head.size shouldBe n
    t.size shouldBe m
  }
  it should "return an empty list for an empty table" in {
    testTable.transpose(List[List[Int]]()) shouldBe (List[List[Int]]())
  }

  "The support function" should "return all 3 rows for test column 0" in {
    testTable.support(columns, List(0)) should be (List(0,1,2))
  }
  it should "return row index 1 for test columns 1 and 3" in {
    testTable.support(columns, List(1)) should be (List(1))
    testTable.support(columns, List(3)) should be (List(1))
    testTable.support(columns, List(1,3)) should be (List(1))
  }
  it should "return row index 1 for all test columns together" in {
    testTable.support(columns, List(0,1,2,3)) should be (List(1))
  }

  "The select function" should "return two columns" in {
    testTable.select(columns, List(1,2)).size shouldBe 2
  }

  "The reduce function" should "alter the table to reduced form of 2 columns" in {
    val reducedTestTable = testTable.reduce()
    reducedTestTable.columns.size shouldBe 2
  }

  "The table" should "have the correct column labels" in {
    val t = new Table
    t.fromFile("./src/test/data/example2/table.csv")
    t.header should equal (List("b", "a1", "a2", "c1", "c2", "u", "v"))
  }
  it should "have the correct reduced column labels" in {
    val t = new Table
    t.fromFile("./src/test/data/example2/table.csv")
    val r = t.reduce()
    r.header should equal (List("b", "a1", "a2", "c1", "c2"))
  }

  "The Moore family" should "be correctly generated" in {
    val t = new Table
    t.fromFile("./src/test/data/example2/table.csv")
    val r = t.reduce()
    val family = Set(Set(), Set("c1"), Set("c2"), Set("c1", "c2"),
                     Set("a1", "c1"), Set("a2", "c2"), Set("b", "c1", "c2"),
                     Set("b", "c1", "c2", "a1", "a2"))
    r.mooreFamily() should equal (family)
  }

  "The buildCdBasis function" should "have the correct Moore family" in {
    val t = new Table
    t.fromFile("./src/test/data/example2/table.csv")
    val r = t.reduce()
    val family = r.mooreFamily()

    val generatedBasis = r.buildCdBasis()
    val newFamily = generatedBasis.mooreFamily()

    newFamily should equal (family)
  }
  it should "produce the correct basis" in {
    val t = new Table
    t.fromFile("./src/test/data/example2/table.csv")
    val r = t.reduce()
    val generatedBasis = r.buildCdBasis()

    val correctBasis = new ClosureSystem with CanonicalDirectBasis
    correctBasis.fromFile("./src/test/data/example2/basis.txt")

    generatedBasis.basis should equal(correctBasis.basis)
  }
}
