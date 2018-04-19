package equivalences

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.collection.mutable.TreeSet

class EquivalenceClassSpec extends FlatSpec with Matchers {

  EquivalenceClass(TreeSet(Set("a"), Set("b"), Set("c"))).representative should be (Some(Set("a")))

  EquivalenceClass(TreeSet(Set("a", "b"), Set("c"))).representative should be (Some(Set("a", "b")))

  EquivalenceClass(TreeSet(Set("13"), Set("2"))).representative should be (Some(Set("2")))

  "The element b" should "be added to the equivalence class" in {
    val e = EquivalenceClass(TreeSet(Set("a")))
    e.add(Set("b"))
    e should equal (EquivalenceClass(TreeSet(Set("a"), Set("b"))))
  }

  "The elements b and c" should "be added to the equivalence class" in {
    val e = EquivalenceClass(TreeSet(Set("a")))
    e.addAll(Set(Set("b"), Set("c")))
    e should equal (EquivalenceClass(TreeSet(Set("a"), Set("b"), Set("c"))))
  }

  "The equivalence class of a" should "contain a" in {
    val e = EquivalenceClass(TreeSet(Set("a")))
    e.contains(Set("a")) should be (true)
  }

  EquivalenceClass(TreeSet.empty[ClosedSet]).isEmpty should be (true)

  "The partition of [a,b] with new set a" should "be {[a], [b]}" in {
    val e = EquivalenceClass(TreeSet(Set("a"), Set("b")))
    e.partition(Set("a")) should equal (Set(
      EquivalenceClass(TreeSet(Set("a"))),
      EquivalenceClass(TreeSet(Set("b")))
    ))
  }

}
