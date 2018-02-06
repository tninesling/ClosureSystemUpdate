package basis

class DBasis extends Basis {

  // Step 1 in D-basis update section of paper

  def targets(newSet: Set[String]): Set[String] =
    //(binary & brokenImplications(newSet)).flatMap(_.conclusion)
    //binary.flatMap(_.conclusion) & newSet
    binary.filter(imp => imp.premise.subsetOf(newSet) || imp.conclusion.subsetOf(newSet))
      .flatMap(imp => imp.premise | imp.conclusion)

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
    /*val allPossible =
      binary.map(_.premise)
        .filter(_.subsetOf(newSet))
        .filter(a => greaterThanOrEqualTo(a, x))*/
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
  def ideal(a: Set[String]): Set[Set[String]] = ideal(a, Set())

  // Calculates the ideal of an element in the lattice after partial orders are broken by newSet
  def ideal(a: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val children =
      (binary &~ brokenImplications(newSet)).filter(imp => imp.premise.subsetOf(a))
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
    (binary & unbrokenImplications(newSet)).filter(imp =>
      imp.conclusion.subsetOf(A)//A.exists(a => imp.conclusion.contains(a))
    ).map(_.premise)
    /*A map (a =>
      this.baseSet filter (x =>
        greaterThanOrEqualTo(Set(x), Set(a), newSet)
      )
    )*/

  def minimalElems(A: Set[Set[String]], newSet: Set[String]) =
    A filterNot (a =>
      (A - a) exists (x => greaterThanOrEqualTo(a, x, newSet))
    )

  def update(newSet: Set[String]) = {
    val broken = brokenImplications(newSet)
    val unbroken = unbrokenImplications(newSet)
    val T = targets(newSet)
    val liftable = nonBinary.filter(imp =>
      T.exists(elem => imp.premise.contains(elem))
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
/*
    println(s"Broken: $broken \n")
    println(s"Unbroken: $unbroken \n")
    println(s"Liftable: $liftable \n")
    println(s"Lifted: $lifted \n")
    println(s"Broken lifted: $brokenLifted \n")
    println(s"Unbroken lifted: $unbrokenLifted \n")
    println(s"Update results: $updated \n")
*/
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
