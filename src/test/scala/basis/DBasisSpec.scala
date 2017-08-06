package basis

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DBasisSpec extends FlatSpec with Matchers {

  "The set of targets" should "be {x,y}" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2")
    testBasis.basis = Set((Set("a1"), Set("x")), (Set("a2"), Set("y")), (Set("x", "y"), Set("d")))
    val newSet = Set("a1", "a2")

    testBasis.targets(newSet) should equal (Set("x", "y"))
  }

  "The A-lift" should "produce {a1,y}->d, {x,a2}->d, and {a1,a2}->d" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2")
    testBasis.basis = Set((Set("a1"), Set("x")), (Set("a2"), Set("y")), (Set("x", "y"), Set("d")))
    val newSet = Set("a1", "a2")

    testBasis.Alift((Set("x", "y"), Set("d")), newSet) should equal (Set((Set("a1", "y"), Set("d")), (Set("x", "a2"), Set("d")), (Set("a1", "a2"), Set("d"))))
  }

  // Example 4.3
  "The set difference of ideals" should "be a2" in {
    val testBasis = new DBasis
    testBasis.baseSet = Set("x", "y", "d", "a1", "a2", "a3")
    testBasis.basis = Set((Set("a1"), Set("x")), (Set("x", "y"), Set("d")), (Set("a3"), Set("y")), (Set("a3"), Set("a2")), (Set("x", "a2"), Set("d")))
    val newSet = Set("a1", "a2", "a3")

    val a3DownA = testBasis.ideal(Set("a3"), newSet)
    val yDown = testBasis.ideal(Set("y"))

    (a3DownA &~ yDown) should equal (Set(Set("a2")))
  }

}
