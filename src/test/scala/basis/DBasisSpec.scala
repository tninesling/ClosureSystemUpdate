package basis

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax._

class DBasisSpec extends FlatSpec with Matchers {

  "The set of targets" should "be {x,y}" in {
    val testBasis = new DBasis
    val binary = Set("a1" --> "x", "a2" --> "y")
    val newSet = Set("a1", "a2")

    testBasis.targets(binary, newSet) should equal (Set("x", "y"))
  }
  it should "be 4" in {
    val testBasis = new DBasis
    val binary = Set("5" --> "4")
    val newSet = Set("5")

    testBasis.targets(binary, newSet) should equal (Set("4"))
  }

  "The value of minStrictUpSet" should "be {a}" in {
    val testBasis = new DBasis
    val binary = Set("a" --> "x", "a'" --> "y")

    testBasis.minStrictUpSet(binary, Set("a", "a'"), Set("x")) should be (Set("a"))
  }

  "The value of A_y" should "be {a'}" in {
    val testBasis = new DBasis
    val binary = Set("a" --> "x", "a'" --> "y")

    testBasis.minStrictUpSet(binary, Set("a", "a'"), Set("y")) should be (Set("a'"))
  }

  "The A-lift" should "produce a1 y -> d, and x a2 -> d" in {
    val testBasis = new DBasis
    val binary = Set("a1" --> "x", "a2" --> "y")

    val newSet = Set("a1", "a2")
    val targets = testBasis.targets(binary, newSet)
    val lifted = testBasis.lift(targets, binary, Set("a1", "a2", "a_y"), Implication(Set("x", "y"), Set("d")))
    val correctLifted = Set(Implication(Set("a1", "y"), Set("d")), Implication(Set("x", "a2"), Set("d")))

    lifted should equal (correctLifted)
  }
  it should "be empty" in {
    val testBasis = new DBasis
    val binary = Set("5" --> "4")
    val newSet = Set("5")
    val targets = testBasis.targets(binary, newSet)

    testBasis.lift(targets, binary, newSet, Set("2", "5") --> "1") should equal (Set(Set("2", "5") --> "1"))
  }

  // Example 4.3
  "The set difference of ideals" should "be a2" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a", "a'", "a_y")
    testBasis.basis = Set(
      "a" --> "x", "a_y" --> "y", "a_y" --> "a'",
      Set("x", "y") --> "d", Set("x", "a'") --> "d"
    )

    val newSet = Set("a", "a'", "a_y")
    val unbrokenBinary = testBasis.binary & testBasis.unbrokenImplications(newSet)

    val ideal1 = testBasis.ideal(unbrokenBinary, Set("a_y"))
    val ideal2 = testBasis.ideal(unbrokenBinary, Set("y"))

    (ideal1 &~ ideal2) should equal (Set("a_y", "a'"))
  }

  "The refinement check" should "determine 14->3 refines 15->3" in {
    val db = new DBasis()
    val binary = Set("5" --> "4")

    db.refines(binary, Set("1", "4") --> "3", Set("1", "5") --> "3") should be (true)
  }
  it should "give the right ideal" in {
    val db = new DBasis()
    val binary = Set("5" --> "4")

    db.ideal(binary, Set("5")) should equal (Set("4", "5"))
  }

  "The update method" should "add implications 15 -> 4, 25 -> 4, and 35 -> 4" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      "5" --> "4", Set("2", "3") --> "4", Set("2", "4") --> "3",
      Set("3", "4") --> "2", Set("1", "4") --> "2", Set("1", "4") --> "3",
      Set("1", "4") --> "5", Set("2", "5") --> "1", Set("3", "5") --> "1",
      Set("1", "2", "3") --> "5"
    )
    val newSet = Set("5")

    testBasis.update(newSet)

    val updatedBasis = Set(
      Set("1", "5") --> "4", Set("2", "5") --> "4", Set("3", "5") --> "4",
      Set("2", "3") --> "4", Set("2", "4") --> "3", Set("3", "4") --> "2",
      Set("1", "4") --> "2", Set("1", "4") --> "3", Set("1", "4") --> "5",
      Set("2", "5") --> "1", Set("3", "5") --> "1", Set("1", "5") --> "2",
      Set("3", "5") --> "2", Set("1", "5") --> "3", Set("2", "5") --> "3",
      Set("1", "2", "3") --> "5"
    )

    testBasis.basis should equal (updatedBasis)
  }
  it should "remove 23->4 and 123->5" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      "5" --> "4", Set("2", "3") --> "4", Set("2", "4") --> "3",
      Set("3", "4") --> "2", Set("1", "4") --> "2", Set("1", "4") --> "3",
      Set("1", "4") --> "5", Set("2", "5") --> "1", Set("3", "5") --> "1",
      Set("1", "2", "3") --> "5"
    )
    val newSet = Set("1", "2", "3")

    testBasis.update(newSet)

    val updatedBasis = Set(
      "5" --> "4",
      Set("2", "4") --> "3", Set("3", "4") --> "2", Set("1", "4") --> "2",
      Set("1", "4") --> "3", Set("1", "4") --> "5", Set("2", "5") --> "1",
      Set("3", "5") --> "1"
    )

    testBasis.basis should equal (updatedBasis)
  }

  "The conversion" should "derive the D-basis from the CDB" in {
    val cdb = new CanonicalDirectBasis()
    cdb.fromFile("./src/test/data/example1/basis.txt")
    cdb.baseSet = Set("1","2","3","4","5")

    val db = cdb.toDbasis

    val expectedDbasis = Set(
      "5" --> "4", Set("2", "3") --> "4", Set("2", "4") --> "3",
      Set("3", "4") --> "2", Set("1", "4") --> "2", Set("1", "4") --> "3",
      Set("1", "4") --> "5", Set("2", "5") --> "1", Set("3", "5") --> "1",
      Set("1", "2", "3") --> "5"
    )

    db.basis should equal (expectedDbasis)
  }
  it should "derive the CDB from the D-basis" in {
    val cdb = new CanonicalDirectBasis()
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val db = new DBasis()
    db.baseSet = Set("1", "2", "3", "4", "5")
    db.basis = Set(
      "5" --> "4", Set("2", "3") --> "4", Set("2", "4") --> "3",
      Set("3", "4") --> "2", Set("1", "4") --> "2", Set("1", "4") --> "3",
      Set("1", "4") --> "5", Set("2", "5") --> "1", Set("3", "5") --> "1",
      Set("1", "2", "3") --> "5"
    )

    db.toCdb.basis should equal (cdb.basis)
  }

  "Edge case 1" should "be compared correctly" in {
    val columns = List(List(0,0,0,0,1), List(1,1,1,1,0), List(1,0,1,1,0), List(0,1,1,1,0), List(0,1,1,0,0))
    val header = List("1", "2", "3", "4", "5")
    val updateSet = Set("1", "2")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 2" should "be compared correctly" in {
    val columns = List(List(0,0,1,0,0), List(0,0,1,1,1), List(1,1,0,0,0), List(1,0,1,1,1), List(0,1,1,1,0))
    val header = List("1", "2", "3", "4", "5")
    val updateSet = Set("2", "3", "4")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 3" should "be compared correctly" in {
    val columns = List(List(1,0,1,0,1), List(1,1,1,1,1), List(0,1,0,1,0), List(1,0,0,0,0), List(0,0,1,0,0))
    val header = List("1", "2", "3", "4", "5")
    val updateSet = Set("5")
    checkTable(columns, header, updateSet) should be (true)
  }


  "Edge case 4" should "be compared correctly" in {
    val columns = List(List(1,0,0,0,0,1,0), List(1,1,0,0,1,0,1), List(0,1,0,1,0,0,1), List(1,0,0,0,0,0,1), List(0,0,0,1,0,0,1), List(1,0,0,0,1,0,0))
    val header = List("1", "2", "3", "4", "5", "6")
    val updateSet = Set("1", "2", "4", "5")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 5" should "be compared correctly" in {
    val columns = List(List(1,0,0,0,0,0,0), List(0,1,1,0,1,1,0), List(1,1,0,0,1,0,0), List(0,0,0,0,1,0,1), List(1,1,0,1,1,1,0), List(1,1,1,1,0,0,0), List(0,0,1,0,1,0,0))
    val header = List("1", "2", "3", "4", "5", "6", "7")
    val updateSet = Set("1", "2", "3", "4", "5")
    checkTable(columns, header, updateSet) should be (true)
  }


  "Edge case 6" should "be compared correctly" in {
    val columns = List(List(1,0,0,1,0,0), List(0,1,1,1,1,0), List(0,1,0,1,0,0), List(0,1,1,1,0,1), List(1,1,0,0,0,1), List(1,0,0,0,0,1))
    val header = List("1", "2", "3", "4", "5", "6")
    val updateSet = Set("1", "3", "5")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 7" should "be compared correctly" in {
    val columns = List(List(0,1,1,0,1,1,1), List(0,1,0,1,1,1,0), List(0,1,0,1,0,1,1), List(1,0,0,0,1,0,0), List(1,0,0,1,1,0,1), List(0,0,0,0,0,1,1), List(0,0,1,0,0,0,1))
    val header = List("1", "2", "3", "4", "5", "6", "7")
    val updateSet = Set("3", "5", "6", "7")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 8" should "be compared correctly" in {
    val columns = List(List(1,1,1,1,1,0,1), List(0,0,0,0,1,1,0), List(1,0,0,1,1,1,1), List(1,1,0,0,0,1,0), List(0,1,0,0,0,0,1), List(0,1,1,1,0,0,1))
    val header = List("1", "2", "3", "4", "5", "6")
    val updateSet = Set("5")
    checkTable(columns, header, updateSet) should be (true)
  }

  "Edge case 9" should "be compared correctly" in {
    val columns = List(List(0,0,1,1,1,1,0), List(1,1,1,1,1,1,0), List(1,0,1,1,0,1,0), List(0,0,0,1,0,1,0), List(1,0,0,0,1,0,1), List(0,1,0,0,0,0,1), List(1,0,1,1,1,1,0))
    val header = List("1", "2", "3", "4", "5", "6", "7")
    val updateSet = Set("2", "4", "5", "6")
    checkTable(columns, header, updateSet) should be (true)
  }

  "The 10x22 example extended by {6,7,8,17}" should "be updated correctly" in {
    val db = new DBasis
    db.fromFile("./src/test/data/example4/OriginalBasis.txt")
    db.update(Set("6", "7", "8", "17"))

    val target = new DBasis
    target.fromFile("./src/test/data/example4/BasisExtendedBy6_7_8_17.txt")

    db should equal (target)
  }

  "The 10x22 example extended by {6,7,8,13,14,15,19}" should "be updated correctly" in {
    val db = new DBasis
    db.fromFile("./src/test/data/example4/OriginalBasis.txt")
    db.update(Set("6", "7", "8", "13", "14", "15", "19"))

    val target = new DBasis
    target.fromFile("./src/test/data/example4/BasisExtendedBy6_7_8_13_14_15_19.txt")

    db should equal (target)
  }

  def checkTable(columns: List[List[Int]], header: List[String], updateSet: Set[String]): Boolean = {
    val t = new Table
    t.columns = columns
    t.header = header
    t.rows = t.transpose(t.columns)
    val r = t.reduce

    val cdb = r.buildCanonicalDirectBasis()
    val db = cdb.toDbasis()

    cdb.update(updateSet)
    db.update(updateSet)

    return cdb.toDbasis.basisEquals(db)
  }

}
