package tests

import shep._
import shep.basis._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NaiveCanonicalDirectBasisSpec extends FlatSpec with Matchers {

  "The Naive Canonical Direct basis" should "produced the same updated basis as the CD basis" in {
    val cdb = new ClosureSystem with CanonicalDirectBasis
    cdb.fromFile("./src/test/data/example1/basis.txt")

    val ncdb = new ClosureSystem with NaiveCanonicalDirectBasis
    ncdb.fromFile("./src/test/data/example1/basis.txt")

    // Basis should be equal before update
    cdb.basis should equal (ncdb.basis)

    cdb.update(Set("1","2","3"))
    ncdb.update(Set("1","2","3"))

    // Basis should still be equal after update
    cdb.basis should equal (ncdb.basis)
  }
}
