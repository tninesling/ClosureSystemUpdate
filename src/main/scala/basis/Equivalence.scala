package basis

sealed trait Equivalence {
  def isBinary: Boolean
  def holdsOn(s: Set[String]): Boolean
  def expand(baseSet: Set[String]): Set[Implication]

  override def equals(other: Any): Boolean = {
    (this, other) match {
      case (AllEquivalence(x), AllEquivalence(y)) =>
        x.equals(y)
      case (BinaryEquivalence(x,y), BinaryEquivalence(w,z)) =>
        (x.equals(w) && y.equals(z)) || (x.equals(z) && y.equals(w))
      case (NonbinaryEquivalence(x,y), NonbinaryEquivalence(w,z)) =>
        (x.equals(w) && y.equals(z)) || (x.equals(z) && y.equals(w))
      case _ =>
        false
    }
  }
}

final case class AllEquivalence(y: Set[String]) extends Equivalence {
  override def isBinary = true

  override def holdsOn(s: Set[String]) = false

  override def expand(baseSet: Set[String]) =
    for {
      left <- baseSet &~ y
      right <- y
    } yield Implication(Set(left), Set(right))
}

final case class BinaryEquivalence(x: Set[String], y: Set[String]) extends Equivalence {
  override def isBinary = true

  override def holdsOn(s: Set[String]): Boolean =
    x.union(y).subsetOf(s) || x.union(y).intersect(s).isEmpty

  override def expand(baseSet: Set[String]) =
    Set(Implication(x,y), Implication(y,x))
}

final case class NonbinaryEquivalence(x: Set[String], y: Set[String]) extends Equivalence {
  override def isBinary = false

  override def holdsOn(s: Set[String]): Boolean =
    x.union(y).subsetOf(s) || x.union(y).intersect(s).isEmpty

  override def expand(baseSet: Set[String]) = {
    val implications = Set(Implication(x,y), Implication(y,x))
    val broken = implications.filterNot(_.holdsOn(baseSet))
    val expandedBroken = for {
      imp <- broken
      left <- imp.premise
      right <- imp.conclusion
    } yield Implication(Set(left), Set(right))

    implications.diff(broken).union(expandedBroken)
  }
}
