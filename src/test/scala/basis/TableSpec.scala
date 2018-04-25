package basis

import equivalences._
import equivalences.syntax.equivalenceclass._

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.collection.mutable.TreeSet

class TableSpec extends FlatSpec with Matchers {
  val testTable = new Table
  val columns = List(List(1,1,1), List(0,1,0), List(0,1,1), List(0,1,0))
  testTable.header = List("a", "b", "c", "d")
  testTable.columns = columns
  testTable.rows = testTable.transpose(columns)
  testTable.equivalences = testTable.header.map(x => EquivalenceClass(TreeSet(Set(x)))).toSet + EquivalenceClass(TreeSet(Set.empty[String]))

  "The addBinaryEquivalence function" should "add the equivalence a <=> emptyset" in {
    val t = new Table
    val columns = List(List(1,1,1), List(0,1,0), List(0,1,1), List(0,1,0))
    t.header = List("a", "b", "c", "d")
    t.columns = columns
    t.rows = t.transpose(columns)
    t.equivalences = t.header.map(_.eqClass).toSet + Set.empty[String].eqClass

    t.addBinaryEquivalence(Set.empty[String], Set("a"))
    t.equivalences should equal (Set("a" <=> Set.empty[String], "b".eqClass, "c".eqClass, "d".eqClass))
  }

  "The nonConstant function" should "return 3 non-constant columns" in {
    testTable.nonConstant.columns.size shouldBe 3
  }
  it should "track a as the bottom element" in {
    testTable.nonConstant.equivalences should contain ("a" <=> Set.empty[String])
  }
  it should "track 6 as the bottom element" in {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    t.nonConstant.equivalences should contain ("6" <=> Set.empty[String])
  }

  "The uniqueSingletonClosures function" should "return 3 columns with unique singleton closures" in {
    testTable.uniqueSingletonClosures.columns.size shouldBe 3
  }
  it should "return a table with the equivalence 7 <-> 8" in {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val eqs: Set[EquivalenceClass] =
      t.header.toSet
        .withFilter(x => !Set("6", "7", "8").contains(x))
        .map(x => EquivalenceClass(TreeSet(Set(x))))

    t.nonConstant.uniqueSingletonClosures.equivalences should equal (eqs + ("7" <=> "8") + ("6" <=> Set.empty[String]))
  }

  "The closure function" should "return column 0 for column 0" in {
    testTable.closure(List(0)) should be (Set(0))
  }
  it should "return columns 0 and 2 for column 2" in {
    testTable.closure(List(2)) should be (Set(0,2))
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
  it should "track a as bottom element and b <=> d" in {
    val r = testTable.reduce()
    r.equivalences should equal (Set(
      EquivalenceClass(TreeSet(Set.empty[String], Set("a"))),
      EquivalenceClass(TreeSet(Set("b"), Set("d"))),
      EquivalenceClass(TreeSet(Set("c")))
    ))

    //r.bottomElement should equal (Set("a"))
  }
  it should "track 6 as bottom element and 7 <=> 8" in {
    val t = new Table
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val r = t.reduce()

    val eqs: Set[EquivalenceClass] =
      t.header.toSet
        .withFilter(x => !Set("6", "7", "8").contains(x))
        .map(_.eqClass)
        //.map(x => EquivalenceClass(TreeSet(Set(x))))

    r.equivalences should equal (eqs + ("7" <=> "8") + ("6" <=> Set.empty[String]))//(eqs + EquivalenceClass(TreeSet(Set("7"), Set("8"))))
    //r.bottomElement should equal (Set("6"))
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
    r.mooreFamily().toSet should equal (family)
  }

}
