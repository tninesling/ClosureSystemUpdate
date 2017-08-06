package basis

class NaiveCanonicalDirectBasis extends CanonicalDirectBasis {

  override def update(closedSet: Set[String]) = {
    val unbroken =
      basis.filter(implication =>
        !implication.premise.subsetOf(closedSet) ||
        implication.conclusion.subsetOf(closedSet)
      )

    val broken = basis &~ unbroken

    val newImplications =
      broken.flatMap(implication =>
        (baseSet &~ (closedSet | implication.conclusion)).map(extension =>
          Implication(implication.premise + extension, implication.conclusion)
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
          !(basisImp.premise.subsetOf(implication.premise) &&
          basisImp.conclusion.equals(implication.conclusion))
        )
      )
  }
}
