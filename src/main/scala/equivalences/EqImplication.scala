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

  /**
   * Expands the implication, removing the class eq from
   * the generated implications
   */
  def expandWithout(s: ClosedSet, eq: EquivalenceClass): Set[EqImplication] =
    for {
      prem <- product(premise.map(_.partition(s))).filterNot(_.equals(eq)) if (prem.nonEmpty)
      conc <- product(conclusion.map(_.partition(s))).filterNot(_.equals(eq)) if (conc.nonEmpty)
    } yield EqImplication(prem, conc)


  /**
   * If the basis is a DBasis, we expand binary implications normally, splitting
   * the equivalences, but for nonbinary implications we replace the old implications
   * with the new ones after splitting, since they will be refinements
   */
  def dbasisExpand(s: ClosedSet): Set[EqImplication] =
    if (isBinary())
      expand(s)
    else {
      for {
        prem <- product(premise.map(_.partition(s))) if (prem.exists(_.intersect(s).nonEmpty))
        conc <- product(conclusion.map(_.partition(s)))
      } yield EqImplication(prem, conc)
    }

  def dbasisExpandWithout(s: ClosedSet, eq: EquivalenceClass): Set[EqImplication] =
    if (isBinary())
      expandWithout(s, eq)
    else {
      for {
        prem <- product(premise.map(_.partition(s))).filterNot(_.equals(eq)) if (prem.exists(_.intersect(s).nonEmpty))
        conc <- product(conclusion.map(_.partition(s))).filterNot(_.equals(eq)) if (conc.nonEmpty)
      } yield EqImplication(prem, conc)
    }

  def addToPremise(eq: EquivalenceClass) =
    EqImplication(premise + eq, conclusion)

  def addToConclusion(eq: EquivalenceClass) =
    EqImplication(premise, conclusion + eq)

  // If the premise or conclusion contains existing, we replace it with the replacement class
  def replace(existing: EquivalenceClass, replacement: EquivalenceClass) = {
    val prem =
      if (premise.contains(existing))
        (premise - existing) + replacement
      else
        premise

    val conc =
      if (conclusion.contains(existing))
        (conclusion - existing) + replacement
      else
        conclusion

    EqImplication(prem, conc)
  }


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
  def implies(s1: String, s2: String): EqImplication =
    EqImplication(
      Set(EquivalenceClass(TreeSet(Set(s1)))),
      Set(EquivalenceClass(TreeSet(Set(s2))))
    )

  def implies(eq1: EquivalenceClass, eq2: EquivalenceClass): EqImplication =
    EqImplication(Set(eq1), Set(eq2))

  def implies(s: String, eqc: EquivalenceClass): EqImplication =
    implies(EquivalenceClass(TreeSet(Set(s))), eqc)
}
