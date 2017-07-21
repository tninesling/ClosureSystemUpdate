package tests

import shep._
import shep.basis._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class CanonicalDirectBasisSpec extends FlatSpec with Matchers {

  "The Canonical Direct basis" should "parse 14 implications from the basis.txt test file" in {
    val cs = new ClosureSystem with CanonicalDirectBasis
    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.basis.size shouldBe 14
  }
  it should "split the basis into 5 sectors" in {
    val cs = new ClosureSystem with CanonicalDirectBasis
    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.sectors.size shouldBe 5
  }
  it should "return the basis with sectorsToBasis" in {
    val cs = new ClosureSystem with CanonicalDirectBasis
    cs.fromFile("./src/test/data/example1/basis.txt")

    cs.sectorsToBasis should equal (cs.basis)
  }
  it should "add 5 to the family when updated with 5" in {
    val cs = new ClosureSystem with CanonicalDirectBasis
    cs.fromFile("./src/test/data/example1/basis.txt")

    cs.baseSet = Set("1","2","3","4","5")
    val prevFamily = cs.mooreFamily
    cs.update(Set("5"))
    cs.mooreFamily should equal (prevFamily + Set("5"))
  }
  it should "add 123 and 23 when updated with 123" in {
    val cs2 = new ClosureSystem with CanonicalDirectBasis

    cs2.fromFile("./src/test/data/example1/basis.txt")
    cs2.baseSet = Set("1","2","3","4","5")
    val prevFamily = cs2.mooreFamily
    cs2.update(Set("1","2","3"))
    cs2.mooreFamily should equal (prevFamily | Set(Set("2","3"), Set("1","2","3")))
  }

  "A Basis" should "produce the correct Moore family" in {
    val cs = new ClosureSystem with CanonicalDirectBasis

    cs.fromFile("./src/test/data/example1/basis.txt")
    cs.baseSet = Set("1","2","3","4","5")

    val family = cs.mooreFamily
    //family.size shouldBe 10
    //family should contain (Set())
    family should contain allOf (Set("1"), Set("2"), Set("3"), Set("4"), Set("1","2"),
                                 Set("1","3"), Set("4","5"), Set("2","3","4"),
                                 Set("1","2","3","4","5"))
  }
}
