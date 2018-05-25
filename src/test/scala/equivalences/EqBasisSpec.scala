package equivalences

import basis.DBasis
import cats.syntax.semigroup._
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import syntax.eqimplication._
import syntax.equivalenceclass._
import EquivalenceClass._

class EqBasisSpec extends FlatSpec with Matchers {

  "The closure of {[1],[2]}" should "be {[1],[2],[3]}" in {
    val testBasis = new EqBasis(new DBasis())
    testBasis.equivalences = Set("1".eqClass, "2".eqClass, "3".eqClass)
    testBasis.basis = Set("1" --> "3", "2" --> "3")

    testBasis.closure(Set("1".eqClass, "2".eqClass)) should equal (Set("1".eqClass, "2".eqClass, "3".eqClass))
  }
  it should "be {[1],[2],[4]}" in {
    val testBasis = new EqBasis(new DBasis())
    testBasis.equivalences = Set("1", "2", "3", "4").map(_.eqClass)
    testBasis.basis = Set("1" --> "2", "2" --> "1", "1" --> "4", "3" --> "4", "4" --> "3")

    testBasis.holdingClosure(Set("2", "4"))(Set("1".eqClass, "2".eqClass)) should equal (Set("1".eqClass, "2".eqClass, "4".eqClass))
  }

  "The effectEqBasisChange method" should "properly add the binary implications" in {
    val testBasis = new EqBasis()
    testBasis.equivalences = Set("a" <=> "b" <=> "c" <=> "d")
    testBasis.effectEqBasisChange(Set("c", "d"))
    testBasis.basis should equal (Set(
      Set("a" <=> "b") --> Set("c" <=> "d"),
      Set("c" <=> "d") --> Set("a" <=> "b")
    ))
  }

  "The handleEquivalences function" should "add the correct equivalences with the nonbinary equivalence" in {
    val firstEquivClass = "1" <=> "6" <=> "10" <=> "11" <=> "16" <=> "18" <=> "20" <=> "22" <=> Set.empty[String]
    val secondEquivClass = "2" <=> "3" <=> "9" <=> "15"
    val thirdEquivClass = "4" <=> "5" <=> "7" <=> "8" <=> "12" <=> "19"
    val fourthEquivClass = "13" <=> "14" <=> "17" <=> "21"

    val testBasis = new EqBasis(new DBasis())
    testBasis.equivalences = Set(firstEquivClass |+| fourthEquivClass, secondEquivClass |+| thirdEquivClass)
    testBasis.basis = Set((secondEquivClass |+| thirdEquivClass) --> (firstEquivClass |+| fourthEquivClass))

    testBasis.handleEquivalences(Set("1", "2", "3", "6", "9", "10", "11", "15", "16", "18", "20", "22"))

    testBasis.equivalences should equal (Set(firstEquivClass, secondEquivClass, thirdEquivClass, fourthEquivClass))
  }

  "The equivalence classes" should "be equal" in {
    val first = "1" <=> "2" <=> "5"
    val second = "2" <=> "5" <=> "1"
    val third = "5" <=> "2" <=> "1"

    first should equal (second)
    first should equal (third)
    second should equal (third)
  }

}
