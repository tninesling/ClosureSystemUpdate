package basis

class DBasis extends Basis {
  def binary = this.basis.filter(imp => ((imp._1.size == 1) && (imp._2.size == 1)))
  def nonBinary = this.basis &~ binary

  def brokenImplications(newSet: Set[String]) =
      this.basis.filter(imp => imp._1.subsetOf(newSet) && !imp._2.subsetOf(newSet))

  def unbrokenImplications(newSet: Set[String]) = this.basis &~ brokenImplications(newSet)

  // Step 1 in D-basis update section of paper

  def targets(newSet: Set[String]): Set[String] =
    (binary & brokenImplications(newSet)).flatMap(_._2)

  // Step 2 in D-basis update section of paper

  /** For sets x and y greaterThanOrEqualTo(x, y) is True if x -> y is in Sigma^b (binary) */
  def greaterThanOrEqualTo(x: Set[String], y: Set[String]): Boolean = binary.contains((x, y))

  def min(x: Set[String], y: Set[String]): Set[String] = {
    if (greaterThanOrEqualTo(x, y))
      y
    else
      x
  }

  def A_x(x: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val allPossible =
      binary.map(_._1)
        .filter(_.subsetOf(newSet))
        .filter(a => greaterThanOrEqualTo(a, x))

    allPossible.filter(a1 =>
      (allPossible &~ Set(a1)).forall(a2 => !(greaterThanOrEqualTo(a1, a2)))
    )
  }

  def Alift(imp: (Set[String], Set[String]), newSet: Set[String]): Set[(Set[String], Set[String])] = {
    if (imp._1.forall(elem => !targets(newSet).contains(elem))) // no element of C is in the target set
      Set(imp)
    else { // some element of C is in the target set
      val C = imp._1
      val removables =
        (C & targets(newSet)).subsets
          .toSet
          .filter(_.size > 0) // Remove Set()

      removables.map(removable =>
        (((C &~ removable) | removable.flatMap(x => A_x(Set(x), newSet).flatten)), imp._2)
      )
    }
  }

  // Step 3 of D-basis update section of paper

  // Calculates the ideal of an element in the lattice
  def ideal(a: Set[String]): Set[Set[String]] =
    ideal(a, Set())

  // Calculates the ideal of an element in the lattice after partial orders are broken by newSet
  def ideal(a: Set[String], newSet: Set[String]): Set[Set[String]] = {
    val children =
      (binary &~ brokenImplications(newSet)).filter(imp => imp._1.equals(a))
        .map(_._2)

    children.map(child => ideal(child, newSet))
      .foldLeft(children)((x,y) => x | y)
  }

  def restrictedAlift(imp: (Set[String], Set[String]), newSet: Set[String]): Set[(Set[String], Set[String])] = {
    val lifted = Alift(imp, newSet) // TO-DO
    lifted
  }

  def update(closedSet: Set[String]) = {}
}
