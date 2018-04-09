package equivalences

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.collection.mutable.TreeSet
import syntax._

class EqImplicationSpec extends FlatSpec with Matchers {

  "The implies syntax method" should "produce a valid implication" in {
    "a" implies "b" should equal (EqImplication(
      Set(EquivalenceClass(TreeSet(Set("a")))),
      Set(EquivalenceClass(TreeSet(Set("b"))))
    ))
  }

  "The expansions method for DBasis updates" should "skip the old implication" in {
    val imp = Set("x".eqClass, "y" <=> "z") --> "d"
    imp.dbasisExpand(Set("z")) should equal (Set(
      Set("x".eqClass, "z".eqClass) --> "d"
    ))
  }

}
