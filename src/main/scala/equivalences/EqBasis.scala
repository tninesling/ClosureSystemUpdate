package equivalences

import basis.Basis
import basis.Implication
import basis.NaiveCanonicalDirectBasis
import cats.Monoid
import scala.collection.mutable.TreeSet

import EquivalenceClass._
import syntax._

class EqBasis(var reducedBasis: Basis = new NaiveCanonicalDirectBasis()) {
  var basis = Set.empty[EqImplication]
  //var reducedBasis: Basis = new NaiveCanonicalDirectBasis
  var equivalences = Set.empty[EquivalenceClass]
  var closedSets = Set.empty[ClosedSet]
  var bottomElement = Monoid[EquivalenceClass].empty


  def update(s: ClosedSet) = {
    closedSets = closedSets + s
    handleEquivalences(s)
    addNewImplicationsToReducedBasis()
    reducedBasis.update(s)
    basis = mapToEquivalenceBasis(reducedBasis.basis)
    postUpdateCleanup()
  }

  /**
   * Handles the broken equivalences before an update.
   * The order is important, bottom element change must come after equivalences are updated
   */
  def handleEquivalences(s: ClosedSet) = {
    effectEqBasisChange(s)
    effectEquivalenceChange(s)
    effectBottomElementChange(s)
  }

  /**
   * Adds to the basis implications which were previously encapsulated
   * by equivalences
   */
  def effectEqBasisChange(s: ClosedSet) = {
    basis = basis | equivalences.flatMap(_.newBinaryImplications(s))
    basis = basis.flatMap(_.expand(s))
  }

  /**
   * Splits equivalences into classes which hold on the new
   * closed set
   */
  def effectEquivalenceChange(s: ClosedSet) = {
    equivalences = equivalences.flatMap(_.partition(s))
  }

  /**
   * If the bottom element is not in the new set, then it is no longer a unique
   * lowest element
   */
  def effectBottomElementChange(s: ClosedSet) = {
    if (!bottomElement.isEmpty) {
      val newPremises = baseSet.filterNot(x => bottomElement.contains(x)).map(equivalenceClass)
      basis = basis | newPremises.map(p => p --> bottomElement.filterNot(_.subsetOf(s)))
      bottomElement = bottomElement.filter(_.subsetOf(s))
    }
  }

  def baseSet = equivalences.diff(Set(bottomElement)).flatMap(_.representative).flatten

  def addNewImplicationsToReducedBasis() =
    basis.flatMap(_.toImplication)
      .flatMap(_.unitImplications)
      .foreach(reducedBasis.addImplication)

  def mapToReducedBasis(equivBasis: Set[EqImplication]): Set[Implication] =
    equivBasis.flatMap(_.toImplication).flatMap(_.unitImplications)

  def mapToEquivalenceBasis(rBasis: Set[Implication]): Set[EqImplication] =
    rBasis.map(imp => EqImplication(
      imp.premise.map(x => equivalenceClass(x)),
      imp.conclusion.map(x => equivalenceClass(x))
    ))

  def postUpdateCleanup() = {
    checkForMissingImplicationsWithJoinReducibles()
    checkForNewBottomElement()
    checkForNewNonbinaryEquivalences()
  }

  /**
   * When we map from the reduced basis back to the equivalence basis, we may
   * not add implications where the conclusion is a join-reducible element, even
   * if this element is the representative of its equivalence class.
   */
  def checkForMissingImplicationsWithJoinReducibles() = {
    basis.map(_.premise).foreach{prem =>
      equivalences.foreach{equiv =>
        val inClosure = equiv.representative.map(
          _.map(x => EquivalenceClass(TreeSet(Set(x)))).subsetOf(closure(prem))
        ).getOrElse(false)

        if (inClosure) {
          basis = basis + (prem --> Set(equiv))
        }
      }
    }
  }

  /**
   * If every other base element implies some element x,
   * remove those implications and set x as the bottom element
   */
  def checkForNewBottomElement() = {
    baseSet.foreach{ x =>
      val allImplyX = for {
          premEquiv <- equivalences if (!premEquiv.contains(x))
          concEquiv <- equivalences if (concEquiv.contains(x))
        } yield premEquiv --> concEquiv

      if (allImplyX.nonEmpty && allImplyX.subsetOf(basis)) {
        basis = basis &~ allImplyX
        bottomElement = equivalenceClass(x)
      }
    }
  }

  def checkForNewNonbinaryEquivalences() = {
    baseSet.map(equivalenceClass).foreach{ x =>
      val cl_x = closure(Set(x))

      // if phi(x) = phi(phi(x)\x), then x <-> phi(x)\x
      if (cl_x.equals(closure(cl_x - x))) {
        val equivalentClosedSet = (cl_x - x).flatMap(_.representative).flatten

        basis = basis.map{ imp =>
          if (imp.premise.contains(x)) {
            imp.addToPremise(equivalenceClass(equivalentClosedSet))
          } else if (imp.conclusion.contains(x)) {
            imp.addToConclusion(equivalenceClass(equivalentClosedSet))
          } else {
            imp
          }
        }

        x.add(equivalentClosedSet)
      }
    }
  }

  def closure(eqs: Set[EquivalenceClass]): Set[EquivalenceClass] =
    basis.filter(_.premise.subsetOf(eqs))
      .flatMap(_.conclusion)
      .union(eqs)


  def brokenEqImplications(newSet: ClosedSet) =
    basis.filterNot(_.holdsOn(newSet))

  def unbrokenEqImplications(newSet: ClosedSet) =
    basis.filter(_.holdsOn(newSet))

  def equivalenceClass(s: ClosedSet): EquivalenceClass =
    equivalences.filter(_.contains(s))
      .headOption
      .getOrElse{
        val newEq = EquivalenceClass(TreeSet(s))
        if (s.size == 1) { // if we have a new join-irreducible, add it to the equivalences so it shows up in base set
          equivalences = equivalences + newEq
        }
        newEq
      }

  def equivalenceClass(s: String): EquivalenceClass = equivalenceClass(Set(s))

  def removeWeakEqImplications() = {
    basis = basis.filterNot(imp1 =>
      (basis - imp1).exists(imp2 =>
        imp2.conclusion.equals(imp1.conclusion) &&
        imp2.premise.subsetOf(imp1.premise)
      )
    )
  }

}
