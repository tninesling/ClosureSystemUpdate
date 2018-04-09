package equivalences

import basis.CanonicalDirectBasis
import basis.DBasis
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax._

class EqBasisSpec extends FlatSpec with Matchers {

  "The effectEqBasisChange method" should "properly add the binary implications" in {
    val testBasis = new EqBasis()
    testBasis.equivalences = Set("a" <=> "b" <=> "c" <=> "d")
    testBasis.effectEqBasisChange(Set("c", "d"))
    testBasis.basis should equal (Set(
      Set("a" <=> "b") --> Set("c" <=> "d"),
      Set("c" <=> "d") --> Set("a" <=> "b")
    ))
  }
  it should "add the new nonbinary implications to the Canonical Direct Basis" in {
    val testBasis = new EqBasis(new CanonicalDirectBasis())
    testBasis.equivalences = Set("a".eqClass, "a'".eqClass, "d".eqClass, "x".eqClass, "y" <=> "z")
    testBasis.basis = Set(
      "a" --> "x", "a'" --> ("y" <=> "z"),
      Set("a".eqClass, "a'".eqClass) --> "d", Set("a".eqClass, "y" <=> "z") --> "d",
      Set("x".eqClass, "a'".eqClass) --> "d", Set("x".eqClass, "y" <=> "z") --> "d"
    )
    testBasis.effectEqBasisChange(Set("z"))

    testBasis.basis should contain allOf (
      "a" --> "x", "a'" --> "y", "a'" --> "z", "y" --> "z",
      Set("a".eqClass, "a'".eqClass) --> "d", Set("a".eqClass, "y".eqClass) --> "d",
      Set("a".eqClass, "z".eqClass) --> "d", Set("x".eqClass, "a'".eqClass) --> "d",
      Set("x".eqClass, "y".eqClass) --> "d", Set("x".eqClass, "z".eqClass) --> "d"
    )
  }
  it should "add the new nonbinary implications to the DBasis" in {
    val testBasis = new EqBasis(new DBasis())
    testBasis.equivalences = Set("a".eqClass, "a'".eqClass, "d".eqClass, "x".eqClass, "y" <=> "z")
    testBasis.basis = Set(
      "a" --> "x", "a'" --> ("y" <=> "z"),
      Set("x".eqClass, "y" <=> "z") --> "d"
    )
    testBasis.effectEqBasisChange(Set("z"))

    testBasis.basis should contain allOf (
      "a" --> "x", "a'" --> "y", "a'" --> "z", "y" --> "z",
      Set("x".eqClass, "z".eqClass) --> "d"
    )
    testBasis.basis should not contain (Set("x".eqClass, "y".eqClass) --> "d")
  }

}
