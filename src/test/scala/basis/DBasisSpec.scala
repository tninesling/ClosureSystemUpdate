package basis

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DBasisSpec extends FlatSpec with Matchers {

  "The set of targets" should "be {x,y}" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2")
    testBasis.basis = Set(
      Implication(Set("a1"), Set("x")),
      Implication(Set("a2"), Set("y")),
      Implication(Set("x", "y"), Set("d"))
    )
    val newSet = Set("a1", "a2")

    testBasis.targets(newSet) should equal (Set("x", "y"))
  }
  it should "be 4" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )
    val newSet = Set("5")

    testBasis.targets(newSet) should equal (Set("4"))
  }

  "The A-lift" should "produce {a1,y}->d and {x,a2}->d" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2")
    testBasis.basis = Set(
      Implication(Set("a1"), Set("x")),
      Implication(Set("a2"), Set("y")),
      Implication(Set("x", "y"), Set("d"))
    )
    val newSet = Set("a1", "a2")

    testBasis.Alift(Implication(Set("x", "y"), Set("d")), newSet) should equal (Set(Implication(Set("a1", "y"), Set("d")), Implication(Set("x", "a2"), Set("d"))))
  }
  it should "be empty" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )
    val newSet = Set("5")

    testBasis.Alift(Implication(Set("2", "5"), Set("1")), newSet) should equal (Set(Implication(Set("2", "5"), Set("1"))))
  }

  // Example 4.3
  "The set difference of ideals" should "be a2" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2", "a3")
    testBasis.basis = Set(
      Implication(Set("a1"), Set("x")),
      Implication(Set("x", "y"), Set("d")),
      Implication(Set("a3"), Set("y")),
      Implication(Set("a3"), Set("a2")),
      Implication(Set("x", "a2"), Set("d"))
    )
    val newSet = Set("a1", "a2", "a3")

    val a3DownA = testBasis.ideal(Set("a3"), newSet)
    val yDown = testBasis.ideal(Set("y"))

    (a3DownA &~ yDown) should equal (Set(Set("a2")))
  }

  "The refinement check" should "determine 14->3 refines 15->3" in {
    val db = new DBasis()
    db.baseSet = Set("1", "2", "3", "4", "5")
    db.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )

    db.refines(Implication(Set("1", "4"), Set("3")), Implication(Set("1", "5"), Set("3"))) should be (true)
  }
  it should "give the right ideal" in {
    val db = new DBasis()
    db.baseSet = Set("1", "2", "3", "4", "5")
    db.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )

    db.ideal(Set("5")) should equal (Set(Set("4")))
  }

  "The update method" should "add implications 15 -> 4, 25 -> 4, and 35 -> 4" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )
    val newSet = Set("5")

    testBasis.update(newSet)

    val updatedBasis = Set(
      Implication(Set("1", "5"), Set("4")),
      Implication(Set("2", "5"), Set("4")),
      Implication(Set("3", "5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )

    testBasis.basis should equal (updatedBasis)
  }
  it should "remove 23->4 and 123->5" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("1", "2", "3", "4", "5")
    testBasis.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )
    val newSet = Set("1", "2", "3")

    testBasis.update(newSet)

    val updatedBasis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1"))
    )

    testBasis.basis should equal (updatedBasis)
  }

  "The conversion" should "derive the D-basis from the CDB" in {
    val cdb = new CanonicalDirectBasis()
    cdb.fromFile("./src/test/data/example1/basis.txt")
    cdb.baseSet = Set("1","2","3","4","5")

    val db = cdb.toDbasis

    val expectedDbasis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )

    db.basis should equal (expectedDbasis)
  }
  it should "derive the CDB from the D-basis" in {
    val cdb = new CanonicalDirectBasis()
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val db = new DBasis()
    db.baseSet = Set("1", "2", "3", "4", "5")
    db.basis = Set(
      Implication(Set("5"), Set("4")),
      Implication(Set("2", "3"), Set("4")),
      Implication(Set("2", "4"), Set("3")),
      Implication(Set("3", "4"), Set("2")),
      Implication(Set("1", "4"), Set("2")),
      Implication(Set("1", "4"), Set("3")),
      Implication(Set("1", "4"), Set("5")),
      Implication(Set("2", "5"), Set("1")),
      Implication(Set("3", "5"), Set("1")),
      Implication(Set("1", "2", "3"), Set("5"))
    )

    db.toCdb.basis should equal (cdb.basis)
  }
}
