package basis

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class WildCanonicalDirectBasisSpec extends FlatSpec with Matchers {

  "The Wild Canonical Direct basis" should "have an equivalent basis to the CD unit basis" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val wcdb = cdb.toWildCdb()

    cdb.basis should equal (wcdb.unitBasis)
  }
  it should "produce the an updated basis equivalent to the updated CD unit basis" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val ncdb = new NaiveCanonicalDirectBasis
    ncdb.fromFile("./src/test/data/example1/basis.txt")

    val wcdb = new WildCanonicalDirectBasis
    wcdb.fromFile("./src/test/data/example1/basis.txt")

    wcdb.basis =
      wcdb.basis
        .map(_.premise)
        .map(x =>
          Implication(x, wcdb.closure(x))
        )

    cdb.update(Set("1", "2", "3"))
    ncdb.update(Set("1", "2", "3"))
    wcdb.update(Set("1", "2", "3"))

    ncdb.basisEquals(wcdb.unitBasis) shouldBe true
    cdb.basisEquals(wcdb.unitBasis) shouldBe true
  }
  it should "produce the an updated basis equivalent to the updated CD unit basis again" in {
    val cdb = new CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val ncdb = new NaiveCanonicalDirectBasis
    ncdb.fromFile("./src/test/data/example1/basis.txt")

    val wcdb = new WildCanonicalDirectBasis
    wcdb.fromFile("./src/test/data/example1/basis.txt")

    wcdb.basis =
      wcdb.basis
        .map(_.premise)
        .map(x =>
          Implication(x, x | wcdb.closure(x))
        )

    cdb.update(Set("5"))
    ncdb.update(Set("5"))
    wcdb.update(Set("5"))

    ncdb.basis should equal (wcdb.unitBasis)
    cdb.basis should equal (wcdb.unitBasis)
  }

}
