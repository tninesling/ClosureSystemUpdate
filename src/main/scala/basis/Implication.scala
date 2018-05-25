package basis

import scala.collection.SortedSet

case class Implication(premise: Set[String], conclusion: Set[String]) {
  def holdsOn(s: Set[String]): Boolean =
    conclusion.subsetOf(s) || !premise.subsetOf(s)

  def contains(s: Set[String]): Boolean =
    s.subsetOf(premise) || s.subsetOf(conclusion)

  def degenerate() = premise.intersect(conclusion).nonEmpty

  def isBinary(): Boolean =
    (premise.size == 1) && (conclusion.size == 1)

  def replace(x: Set[String], y: Set[String]) = {
    val prem = if (x.subsetOf(premise))
        premise.diff(x).union(y)
      else
        premise

    val conc = if (x.subsetOf(conclusion))
        conclusion.diff(x).union(y)
      else
        conclusion

    Implication(prem, conc)
  }

  def unitImplications(): Set[Implication] =
    conclusion.map(c => Implication(premise, Set(c)))

  override def toString(): String =
    s"${premise.to[SortedSet].mkString(" ")} -> ${conclusion.to[SortedSet].mkString(" ")}"
}
