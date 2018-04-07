package basis

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax._

class CanonicalDirectBasisSpec extends FlatSpec with Matchers {

  "The Canonical Direct basis" should "parse 14 implications from the basis.txt test file" in {
    val cs = new CanonicalDirectBasis()
    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.basis.size shouldBe 14
  }
  it should "split the basis into 5 sectors" in {
    val cs = new CanonicalDirectBasis()
    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.sectors.size shouldBe 5
  }
  it should "return the basis with sectorsToBasis" in {
    val cs = new CanonicalDirectBasis()
    cs.fromFile("./src/test/data/example1/basis.txt")

    cs.sectorsToBasis should equal (cs.basis)
  }
  it should "add 5 to the family when updated with 5" in {
    val cs = new CanonicalDirectBasis()
    cs.fromFile("./src/test/data/example1/basis.txt")

    cs.baseSet = Set("1","2","3","4","5")
    val prevFamily = cs.mooreFamily
    cs.update(Set("5"))
    cs.mooreFamily should equal (prevFamily + Set("5"))
  }
  it should "add 123 and 23 when updated with 123" in {
    val cs2 = new CanonicalDirectBasis()

    cs2.fromFile("./src/test/data/example1/basis.txt")
    cs2.baseSet = Set("1","2","3","4","5")
    val prevFamily = cs2.mooreFamily
    cs2.update(Set("1","2","3"))
    cs2.mooreFamily should equal (prevFamily | Set(Set("2","3"), Set("1","2","3")))
  }

  "A Basis" should "produce the correct Moore family" in {
    val cs = new CanonicalDirectBasis()

    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.baseSet = Set("1","2","3","4","5")

    val family = cs.mooreFamily
    family should contain allOf (Set("1"), Set("2"), Set("3"), Set("4"), Set("1","2"),
                                 Set("1","3"), Set("4","5"), Set("2","3","4"),
                                 Set("1","2","3","4","5"))
  }
  /* // This example is very long
  "The Canonical Direct Basis" should "reduce to the target Dbasis" in {
    val t = new Table()
    t.fromFile("./src/test/data/example3/larochelle.csv")
    val r = t.reduce()
    val cdb = r.buildCanonicalDirectBasis()
    val db = cdb.toDbasis()

    val target = new DBasis()
    target.fromFile("./src/test/data/example3/FormattedTargetDbasis.txt")

    db.basis should equal (target.basis)
  }
  */

  "The upper cover of 145" should "be 12345" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")
    cdb.closedSets = cdb.mooreFamily()
    val newSet = Set("1", "4", "5")

    cdb.upperCover(newSet) should equal (Set("1", "2", "3", "4", "5"))
  }

  "The maximal subsets of 145" should "be 1 and 45" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")
    cdb.closedSets = cdb.mooreFamily()
    cdb.maximalClosedSubsets(Set("1", "4", "5")) should equal (Set(Set("1"), Set("4", "5")))
  }

  "The set of minimal transversals" should "be {14, 15}" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")
    cdb.closedSets = cdb.mooreFamily()
    cdb.minimalTransversals(Set("1", "4", "5")) should equal (Set(Set("1", "4"), Set("1", "5")))
  }

  "The removal process" should "properly remove the meet irreducible element 145" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")
    val newSet = Set("1", "4", "5")
    cdb.closedSets = cdb.mooreFamily()

    cdb.update(newSet) // add a new closed set
    cdb.remove(newSet) // removing the same set should return the basis to original

    val target = new CanonicalDirectBasis
    target.fromFile("./src/test/data/example1/basis.txt")

    cdb.basis should equal (target.basis)
  }
  it should "properly remove the meet irreducible element m_1 m_2" in {
    val cdb = new CanonicalDirectBasis
    cdb.baseSet = Set("m1", "m2", "x", "d")
    cdb.closedSets = Set(Set.empty[String], Set("x"), Set("m1"), Set("m2"), Set("m1", "m2"), cdb.baseSet)
    cdb.basis = Set("d" --> "x", Set("m1", "m2", "x") --> "d")

    cdb.remove(Set("m1", "m2"))

    val target = Set("d" --> "x", Set("m1", "m2") --> "d", Set("m1", "m2") --> "x")

    cdb.basis should equal (target)
  }

}
