package basis

class NaiveCanonicalDirectBasis extends CanonicalDirectBasis {

  override def update(closedSet: Set[String]) = {
    val newImplications =
      brokenImplications(closedSet).flatMap(implication =>
        (baseSet &~ (closedSet | implication.conclusion)).map(extension =>
          Implication(implication.premise + extension, implication.conclusion)
        )
      )

    basis = unbrokenImplications(closedSet) | newImplications

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
