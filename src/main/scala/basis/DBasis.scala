package basis

class DBasis extends Basis {
  def binary = this.basis.filter(imp => ((imp.premise.size == 1) && (imp.conclusion.size == 1)))
  def nonBinary = this.basis &~ binary

  def brokenImplications(newSet: Set[String]) =
      this.basis.filter(imp => imp.premise.subsetOf(newSet) && !imp.conclusion.subsetOf(newSet))

  def unbrokenImplications(newSet: Set[String]) = this.basis &~ brokenImplications(newSet)

  // Step 1 in D-basis update section of paper

  def targets(newSet: Set[String]): Set[String] =
    (binary & brokenImplications(newSet)).flatMap(_.conclusion)

  // Step 2 in D-basis update section of paper

  /** For sets x and y greaterThanOrEqualTo(x, y) is True if x -> y is in Sigma^b (binary) */
  def greaterThanOrEqualTo(x: Set[String], y: Set[String]) = binary contains Implication(x, y)

  def greaterThanOrEqualTo(x: Set[String], y: Set[String], newSet: Set[String]) =
    (binary & unbrokenImplications(newSet)) contains Implication(x, y)

  def min(x: Set[String], y: Set[String]): Set[String] =
    if (greaterThanOrEqualTo(x, y))
      y
    else
      x

  def A_x(x: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val allPossible =
      binary.map(_.premise)
        .filter(_.subsetOf(newSet))
        .filter(a => greaterThanOrEqualTo(a, x))

    allPossible.filter(a1 =>
      (allPossible &~ Set(a1)).forall(a2 => !(greaterThanOrEqualTo(a1, a2)))
    )
  }

  def Alift(imp: Implication, newSet: Set[String]): Set[Implication] = {
    if (imp.premise.forall(elem => !targets(newSet).contains(elem))) // no element of C is in the target set
      Set(imp)
    else { // some element of C is in the target set
      for {
        removable <- imp.premise & targets(newSet)
        replacement <- A_x(Set(removable), newSet)
      } yield Implication(((imp.premise - removable) | replacement), imp.conclusion)
    }
  }

  // Step 3 of D-basis update section of paper

  // Calculates the ideal of an element in the lattice
  def ideal(a: Set[String]): Set[Set[String]] = ideal(a, Set())

  // Calculates the ideal of an element in the lattice after partial orders are broken by newSet
  def ideal(a: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val children =
      (binary &~ brokenImplications(newSet)).filter(imp => imp.premise.equals(a))
        .map(_.conclusion)

    children.map(child => ideal(child, newSet))
      .foldLeft(children)((x,y) => x | y)
  }

  // If imp1 << imp2, true otherwise false
  def refines(imp1: Implication, imp2: Implication): Boolean = refines(imp1, imp2, Set())

  def refines(imp1: Implication, imp2: Implication, newSet: Set[String]): Boolean =
    if (imp1.conclusion.equals(imp2.conclusion))
      imp1.premise.subsetOf(imp2.premise | ideal(imp2.premise, newSet).flatten)
    else
      false

  def restrictedAlift(imp: Implication, newSet: Set[String]): Set[Implication] = {
    val lifted = Alift(imp, newSet)
    val candidates = nonBinary | lifted
    lifted.filterNot(Y => (candidates - Y).exists(Z => refines(Z, Y, newSet)))
  }

  // Step 4
  def upSet(A: Set[String], newSet: Set[String]): Set[Set[String]] =
    A map (a =>
      this.baseSet filter (x =>
        greaterThanOrEqualTo(Set(x), Set(a), newSet)
      )
    )

  def minimalElems(A: Set[Set[String]], newSet: Set[String]) =
    A filterNot (a =>
      (A - a) exists (x => greaterThanOrEqualTo(a, x, newSet))
    )

  def update(newSet: Set[String]) = {
    val broken = brokenImplications(newSet)
    val unbroken = unbrokenImplications(newSet)
    val lifted = broken flatMap (imp => restrictedAlift(imp, newSet))

    // Perform body building on lifted broken implications
    val updated = lifted flatMap {imp =>
      val extensions = this.baseSet &~ (newSet | imp.conclusion | minimalElems(upSet(imp.conclusion, newSet), newSet).flatten)
      extensions map (ext =>
        Implication(imp.premise + ext, imp.conclusion)
      )
    }

    val unrefinedCandidates = updated | unbroken

    // Filter out implications which have refinements
    val refined = unrefinedCandidates filterNot {imp1 =>
      (unrefinedCandidates - imp1) exists {imp2 =>
        refines(imp2, imp1, newSet)
      }
    }

    this.basis = refined
  }
}
