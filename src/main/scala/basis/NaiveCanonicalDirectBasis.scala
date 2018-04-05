package basis

class NaiveCanonicalDirectBasis extends CanonicalDirectBasis {

  override def update(newSet: Set[String]) = {
    val newImplications =
      for {
        implication <- brokenImplications(newSet)
        extension <- baseSet.diff(newSet | implication.conclusion)
      } yield Implication(implication.premise + extension, implication.conclusion)
      
    basis = unbrokenImplications(newSet) | newImplications

    removeWeakImplications()
  }
}
