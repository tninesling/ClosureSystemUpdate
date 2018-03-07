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
}
