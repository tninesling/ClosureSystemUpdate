package equivalences

import cats.Monoid
import scala.collection.mutable.TreeSet
import scala.collection.SortedSet

import syntax._

case class EquivalenceClass(elements: TreeSet[ClosedSet]) {

  def add(a: ClosedSet) = elements += a

  def addAll(sa: Set[ClosedSet]) = elements ++= sa

  def contains(a: ClosedSet): Boolean = elements.contains(a)

  def contains(a: String): Boolean = contains(Set(a))

  def diff(eq: EquivalenceClass) = EquivalenceClass(elements.diff(eq.elements))

  def isEmpty() = elements.isEmpty

  def filter(p: ClosedSet => Boolean) = EquivalenceClass(elements.filter(p))

  def filterNot(p: ClosedSet => Boolean) = filter(x => !p(x))

  def newBinaryImplications(s: ClosedSet): Set[EqImplication] = {
    val splitEquiv = partition(s)
    for {
      p <- splitEquiv
      c <- splitEquiv - p
    } yield p --> c
  }

  def partition(a: ClosedSet): Set[EquivalenceClass] = {
    Set(filter(_.subsetOf(a)), filterNot(_.subsetOf(a)))
      .filter(_.elements.exists(_.size == 1)) // keep nonempty equivalence classes with a join-irreducible
  }

  def remove(a: ClosedSet) = elements -= a

  def representative: Option[ClosedSet] =
    if (elements.nonEmpty)
      Some(elements.max)
    else
      None

  def singletons() = elements.filter(_.size == 1)

  def subsetOf(that: EquivalenceClass) = elements.subsetOf(that.elements)

  override def toString(): String =
    s"[${elements.to[SortedSet].map(_.mkString(" ")).mkString(",")}]"

}

object EquivalenceClass {
  implicit val eqClassMonoid = new Monoid[EquivalenceClass] {
    def combine(e1: EquivalenceClass, e2: EquivalenceClass) =
      EquivalenceClass(e1.elements | e2.elements)

    def empty = EquivalenceClass(TreeSet.empty[ClosedSet])
  }
}
