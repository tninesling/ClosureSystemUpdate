package equivalences

import basis.Implication
import scala.collection.mutable.TreeSet

case class EqImplication(premise: Set[EquivalenceClass], conclusion: Set[EquivalenceClass]) {
  def holdsOn(s: ClosedSet): Boolean =
    conclusion.flatMap(_.elements).flatten.subsetOf(s) ||
    !premise.flatMap(_.elements).flatten.subsetOf(s)

  def isBinary(): Boolean = {
    val singleEquivalences =
      premise.size == 1 && conclusion.size == 1

    val singleRepresentatives  =
      premise.flatMap(_.representative).flatten.size == 1 &&
      conclusion.flatMap(_.representative).flatten.size == 1

    singleEquivalences && singleRepresentatives
  }

  def expand(s: ClosedSet): Set[EqImplication] =
    for {
      prem <- product(premise.map(_.partition(s)))
      conc <- product(conclusion.map(_.partition(s)))
    } yield EqImplication(prem, conc)

  def addToPremise(eq: EquivalenceClass) =
    EqImplication(premise + eq, conclusion)

  def addToConclusion(eq: EquivalenceClass) =
    EqImplication(premise, conclusion + eq)

  def toImplication(): Option[Implication] = {
    val asImp = Implication(
      premise.flatMap(_.representative).flatten,
      conclusion.flatMap(_.representative).flatten
    )
    val validImp = Implication(asImp.premise, asImp.conclusion &~ asImp.premise)

    if (validImp.conclusion.nonEmpty)
      Some(validImp)
    else
      None
  }

  override def toString(): String = {
    val p = premise//.flatMap(_.representative).flatten
    val c = conclusion//.flatMap(_.representative).flatten

    s"${p.mkString(" ")} -> ${c.mkString(" ")}"
  }

  /** For Sets A, B return {{a,b}: a in A, b in B} */
  def product[T](operands: Set[Set[T]]): Set[Set[T]] =
    product[T](Set(Set.empty[T]), operands)

  def product[T](acc: Set[Set[T]], operands: Set[Set[T]]): Set[Set[T]] = {
    if (operands.isEmpty)
      acc
    else {
      val newAcc = for {
        s <- acc
        x <- operands.head
      } yield s + x

      product(newAcc, operands.tail)
    }
  }

}

object EqImplication {
  def implies(s1: String, s2: String) =
    EqImplication(
      Set(EquivalenceClass(TreeSet(Set(s1)))),
      Set(EquivalenceClass(TreeSet(Set(s2))))
    )

  def implies(eq1: EquivalenceClass, eq2: EquivalenceClass) =
    EqImplication(Set(eq1), Set(eq2))
}
