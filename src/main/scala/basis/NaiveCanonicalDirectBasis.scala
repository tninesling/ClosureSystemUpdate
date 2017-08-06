package basis

class NaiveCanonicalDirectBasis extends CanonicalDirectBasis {

  override def update(closedSet: Set[String]) = {
    val unbroken =
      basis.filter(implication =>
        !implication._1.subsetOf(closedSet) ||
        implication._2.subsetOf(closedSet)
      )

    val broken = basis &~ unbroken

    val newImplications =
      broken.flatMap(implication =>
        (baseSet &~ (closedSet | implication._2)).map(extension =>
          (implication._1 + extension, implication._2)
        )
      )

    basis = unbroken | newImplications

    removeWeakImplications()
  }

  // Remove implications from the basis which are weaker than existing implications
  def removeWeakImplications() = {
    basis =
      basis.filter(implication =>
        (basis - implication).forall(basisImp =>
          !(basisImp._1.subsetOf(implication._1) &&
          basisImp._2.equals(implication._2))
        )
      )
  }
}
