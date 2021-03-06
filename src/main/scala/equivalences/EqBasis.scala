package equivalences

import basis.Basis
import basis.DBasis
import basis.Implication
import basis.NaiveCanonicalDirectBasis
import scala.collection.mutable.TreeSet
import syntax.eqimplication._

class EqBasis(var reducedBasis: Basis = new NaiveCanonicalDirectBasis()) {
  var basis = Set.empty[EqImplication]
  var equivalences = Set.empty[EquivalenceClass]
  var closedSets = Set.empty[ClosedSet]

  def update(s: ClosedSet) = {
    closedSets = closedSets + s
    handleEquivalences(s)

    reducedBasis.basis = mapToReducedBasis(basis)
    reducedBasis.update(s)

    basis = basis.filter(x =>
      x.isBinary() &&
      x.holdsOn(s)
    ).union(mapToEquivalenceBasis(reducedBasis.basis))

    reducedBasis.basis = mapToReducedBasis(basis) // clean up unnecessary implications already covered in Sigma_{eq}
  }

  /**
   * Handles the broken equivalences before an update.
   * The order is important, bottom element change must come after equivalences are updated
   */
  def handleEquivalences(s: ClosedSet) = {
    effectEqBasisChange(s)
    effectEquivalenceChange(s)
    reducedBasis.baseSet = baseSet
  }

  /**
   * Adds to the basis implications which were previously encapsulated
   * by equivalences
   */
  def effectEqBasisChange(cs: ClosedSet) = {
    basis = basis.flatMap(_.expand(cs))
    /* temporarily like this until I can figure out the D-basis-specific expansion
    basis = reducedBasis match {
      case db: DBasis =>
        basis.flatMap(_.dbasisExpand(cs))
      case _ =>
        basis.flatMap(_.expand(cs))
    }
    */

    equivalences.foreach{ eqc =>
      basis = basis | newTransitiveImplications(cs)(eqc)
    }
  }

  def newTransitiveImplications(cs: ClosedSet)(eqc: EquivalenceClass) = {
    val splitEq = eqc.partition(cs)
    for {
      x <- splitEq
      y <- splitEq - x
      p <- upSet(x)
      c <- downSet(y)
    } yield p --> c
  }

  def upSet(eqc: EquivalenceClass): Set[EquivalenceClass] =
    basis.withFilter(_.isBinary)
      .withFilter(_.conclusion.equals(Set(eqc)))
      .flatMap(_.premise)
      .union(Set(eqc))

  def downSet(eqc: EquivalenceClass): Set[EquivalenceClass] =
    basis.withFilter(_.isBinary)
      .withFilter(_.premise.equals(Set(eqc)))
      .flatMap(_.conclusion)
      .union(Set(eqc))

  /**
   * Splits equivalences into classes which hold on the new
   * closed set
   */
  def effectEquivalenceChange(cs: ClosedSet) = {
    equivalences = equivalences.flatMap(_.partition(cs))
    //  .map(_.filter(_.size < 2))
    //checkForNewNonbinaryEquivalences(cs)
  }

  def baseSet = equivalences.flatMap(_.representative).flatten

  def addNewImplicationsToReducedBasis() =
    basis.flatMap(_.toImplication)
      .flatMap(_.unitImplications)
      .foreach(reducedBasis.addImplication)

  def mapToReducedBasis(equivBasis: Set[EqImplication]): Set[Implication] =
    equivBasis.flatMap(_.toImplication)
      .flatMap(_.unitImplications)

  def mapToEquivalenceBasis(rBasis: Set[Implication]): Set[EqImplication] = {
    rBasis.map(imp => EqImplication(
      imp.premise.map(x => equivalenceClass(x)),
      imp.conclusion.map(x => equivalenceClass(x))
    ))
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
  /*def checkForNewBottomElement() = {
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
  }*/

  def checkForNewNonbinaryEquivalences(cs: ClosedSet) = {
    var intermediateBasis = basis
    equivalences.filterNot(_.contains(Set.empty[String])).foreach{ x =>
      // check the closure using only implications that hold
      val cl_x = holdingClosure(cs)(Set(x))

      // if phi(x) = phi(phi(x)\x), then x <-> phi(x)\x
      if (closure(cl_x - x).contains(x)) { // regular closure gives us all implications we need
        val equivalentClosedSet = (cl_x - x).flatMap(_.representative).flatten
        intermediateBasis = intermediateBasis.map(_.replace(x, x <=> equivalentClosedSet))
        x.add(equivalentClosedSet)
      }
    }
    basis = intermediateBasis
  }

  def closure(eqs: Set[EquivalenceClass]): Set[EquivalenceClass] =
    basis.withFilter(_.premise.subsetOf(eqs))
      .flatMap(_.conclusion)
      .union(eqs)
      .filterNot(_.contains(Set.empty[String]))

  /**
   * Computes the closure of a set only using the implications which hold on the
   * set cs. Used after we add needed broken implications but they haven't been
   * removed by the body-building process yet
   */
  def holdingClosure(cs: ClosedSet)(eqs: Set[EquivalenceClass]): Set[EquivalenceClass] =
    basis.withFilter(_.holdsOn(cs))
      .withFilter(_.premise.subsetOf(eqs))
      .flatMap(_.conclusion)
      .union(eqs)
      .filterNot(_.contains(Set.empty[String]))

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
