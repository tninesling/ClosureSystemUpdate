package basis

import scala.collection.SortedSet

case class Implication(premise: Set[String], conclusion: Set[String]) {
  def holdsOn(s: Set[String]): Boolean =
    conclusion.subsetOf(s) || !premise.subsetOf(s)

  def isBinary(): Boolean =
    (premise.size == 1) && (conclusion.size == 1)

  override def toString(): String =
    s"${premise.to[SortedSet].mkString(" ")} -> ${conclusion.to[SortedSet].mkString(" ")}"
}
