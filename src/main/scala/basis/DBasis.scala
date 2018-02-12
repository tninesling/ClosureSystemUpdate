package basis

class DBasis extends Basis {

  // Step 1 in D-basis update section of paper

  def targets(newSet: Set[String]): Set[String] = {
    val first = (binary & brokenImplications(newSet)).flatMap(_.conclusion) &~ newSet
    val second = binary.flatMap(_.conclusion) & newSet
    first | second
  }
    //binary.filter(imp => imp.premise.subsetOf(newSet) || imp.conclusion.subsetOf(newSet))
    //  .flatMap(imp => imp.premise | imp.conclusion)

  // Step 2 in D-basis update section of paper

  /** For sets x and y greaterThanOrEqualTo(x, y) is True if x -> y is in Sigma^b (binary) */
  def greaterThanOrEqualTo(x: Set[String], y: Set[String]) = binary contains Implication(x, y)

  def greaterThanOrEqualTo(x: Set[String], y: Set[String], newSet: Set[String]) =
    (binary & unbrokenImplications(newSet)) contains Implication(x, y)

  def A_x(x: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val allPossible = binary.filter(imp => x.subsetOf(imp.conclusion)).map(_.premise)

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
        replacement <- A_x(Set(removable), newSet) - imp.conclusion
      } yield Implication(((imp.premise - removable) | replacement), imp.conclusion)
    }
  }

  // Step 3 of D-basis update section of paper

  // Calculates the ideal of an element in the lattice
  def ideal(a: Set[String]): Set[String] = ideal(a, Set())

  // Calculates the ideal of an element in the lattice after partial orders are broken by newSet
  // This implementation is dependent on the assumption of binary transitivity
  def ideal(a: Set[String], newSet: Set[String]): Set[String] =
    a | (binary &~ brokenImplications(newSet))
          .filter(_.premise.subsetOf(a))
          .flatMap(_.conclusion)

  /*
  def ideal(a: Set[String], newSet: Set[String]): Set[String] = {
    val children =
      (binary &~ brokenImplications(newSet))
        .filter(_.premise.subsetOf(a))
        .flatMap(_.conclusion)

    if ((children &~ a).isEmpty) a else ideal(a | children, newSet)
  }
  */
  // If imp1 << imp2, true otherwise false
  def refines(imp1: Implication, imp2: Implication): Boolean = refines(imp1, imp2, Set())

  def refines(imp1: Implication, imp2: Implication, newSet: Set[String]): Boolean =
    if (imp1.conclusion.equals(imp2.conclusion))
      imp1.premise.subsetOf(imp2.premise | ideal(imp2.premise, newSet))
    else
      false

  def restrictedAlift(imp: Implication, newSet: Set[String]): Set[Implication] = {
    val lifted = Alift(imp, newSet)
    val candidates = nonBinary | lifted
    lifted.filterNot(Y => (candidates - Y).exists(Z => refines(Z, Y, newSet)))
  }

  // Step 4

  // This implementation is dependent on the assumption of binary transitivity in the basis
  def upSet(A: Set[String], newSet: Set[String]): Set[Set[String]] =
    (binary & unbrokenImplications(newSet))
      .filter(_.conclusion.subsetOf(A))
      .map(_.premise)

  def update(newSet: Set[String]) = {
    val broken = brokenImplications(newSet)
    val unbroken = unbrokenImplications(newSet)

    val liftable = nonBinary.filterNot(imp =>
      (imp.premise & targets(newSet)).isEmpty
    )
    val lifted = liftable flatMap (imp => Alift(imp, newSet))


    val brokenLifted = lifted.filter(imp => imp.premise.subsetOf(newSet) && !imp.conclusion.subsetOf(newSet))
    val unbrokenLifted = lifted &~ brokenLifted

    // Perform body building on lifted broken implications
    val updated = (broken | brokenLifted) flatMap {imp =>
      val extensions = this.baseSet &~ (newSet | imp.conclusion | upSet(imp.conclusion | imp.premise, newSet).flatten)
      extensions map (ext =>
        Implication(imp.premise + ext, imp.conclusion)
      )
    }

    val unrefinedCandidates = updated | unbroken | unbrokenLifted

    // Split candidates into binary and nonbinary since only the nonbinary should
    // be filtered due to refinements; want to keep transitivities in binary
    val binaryCandidates = unrefinedCandidates.filter(_.premise.size == 1)
    val nonBinaryCandidates = unrefinedCandidates &~ binaryCandidates

    // Filter out implications which have refinements
    val refined = nonBinaryCandidates filterNot {imp1 =>
      (unrefinedCandidates - imp1) exists {imp2 =>
        refines(imp2, imp1, newSet)
      }
    }

    this.basis = binaryCandidates | refined

  }

  /* This is not entirely correct; produces incorrect CDB for some examples */
  def toCdb(): CanonicalDirectBasis = {
    val cdb = new CanonicalDirectBasis
    cdb.baseSet = this.baseSet

    // Replace elements that are conclusion of binary imp with the premise of that imp
    // This generates the elements of the CDB that would have refinements in the D-basis
    val unrefined = binary flatMap {binImp =>
      nonBinary filter {nbImp =>
        binImp.conclusion.subsetOf(nbImp.premise) &&
        !binImp.premise.subsetOf(nbImp.conclusion) // keep if the conclusion of the binary imp is in the premise
      } map {nbImp =>
        val newPrem = (nbImp.premise &~ binImp.conclusion) | binImp.premise
        Implication(newPrem, nbImp.conclusion)
      }
    }

    cdb.basis = this.basis | unrefined
    cdb.basis =
      cdb.basis.filter(implication =>
        (cdb.basis - implication).forall(basisImp =>
          !(basisImp.premise.subsetOf(implication.premise) &&
          basisImp.conclusion.equals(implication.conclusion))
        )
      )
    cdb
  }
}
