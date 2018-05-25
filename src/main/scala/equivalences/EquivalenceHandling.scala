package equivalences

import basis.Basis
import basis.Implication
import basis.syntax.implication._
import cats.Monoid
import cats.instances.option._
import cats.syntax.apply._
import cats.syntax.semigroup._
import scala.io.Source
import syntax.equivalenceclass._

trait EquivalenceHandling extends Basis {
  var equivalences: Set[EquivalenceClass] = Set(Set.empty[String].eqClass)

  override def update(cs: ClosedSet) = {
    handleBrokenEquivalences(cs)
    super.update(cs)
  }

  def convertStandardToReducedSystem() = {
    basis = transitiveClosure(basis | implicationsBetweenPartitions(_.size <= 1))
    equivalences = equivalences.map(_.filter(_.size <= 1))
    baseSet = equivalences.flatMap(_.representative).flatten
  }

  def convertReducedToStandardSystem() = {
    var intermediateBasis = basis
    equivalences.foreach{ eqc =>
      val x = eqc.representative.getOrElse(Set.empty[String])
      val cl_x = closure(x)
      val closureComplement = cl_x &~ x

      // if phi(x) = phi(phi(x)\x), then x <-> phi(x)\x
      if (x.nonEmpty && x.subsetOf(closure(closureComplement))) {
        intermediateBasis = intermediateBasis.map(_.replace(x, closureComplement))
          .filterNot(_.degenerate)
        eqc.add(closureComplement)
      }
    }
    basis = intermediateBasis
    baseSet = equivalences.flatMap(_.representative).flatten
  }

  // we can add implications &~ basis for Dbasis
  def handleBrokenEquivalences(cs: ClosedSet) = {
    val newBinaryImps = implicationsBetweenPartitions(_.subsetOf(cs))
    val newNonbinaryImps = newBinaryImps.filter(_.holdsOn(cs))
      .flatMap(imp => basis.map(_.replace(imp.premise, imp.conclusion)))

    basis = transitiveClosure(basis | newBinaryImps | newNonbinaryImps)
    equivalences = equivalences.flatMap(_.partition(cs))
    baseSet = equivalences.flatMap(_.representative).flatten
  }

  /**
   * Partitions each equivalence class into a class satisying the predicate
   * and a class not satisfying the predicate. Then produces the implications
   * between the two classes
   */
  def implicationsBetweenPartitions(pred: ClosedSet => Boolean) =
    equivalences.flatMap{ eqc =>
      val eqc1 = eqc.filter(pred)
      val eqc2 = eqc.filterNot(pred)

      (eqc1.representative, eqc2.representative).mapN((rep1, rep2) =>
        Set(rep1 --> rep2, rep2 --> rep1)
      )
      .map(_.flatMap(_.unitImplications()))
      .getOrElse(Set.empty[Implication])
    }

  override def fromFile(fileLocation: String) = {
    val lines = Source.fromFile(fileLocation).getLines

    while (lines.hasNext) {
      val line = lines.next
      if (line matches """\d[ \d]*""") {
        equivalences = equivalences | line.split(" ").map(_.trim.eqClass).toSet
      } else if (line.contains("<=>")) {
        addToEquivalences(parseEquivalence(line))
      }
    }

    super.fromFile(fileLocation)
  }

  def parseEquivalence(equivStr: String): EquivalenceClass =
    equivStr.split("<=>")
      .filter(_.isEmpty)
      .map{ closedSetString =>
        val cs: ClosedSet = closedSetString.split(" ").toSet
        cs.eqClass
      }
      .foldLeft(Monoid[EquivalenceClass].empty)(_ |+| _)

  def addToEquivalences(eqc: EquivalenceClass) = {
    val classesWithEqcElements = equivalences.filter(existingEquiv =>
      eqc.elements.exists(cs => existingEquiv.contains(cs))
    )

    val existingEquivalences = equivalences.diff(classesWithEqcElements)
    val newEquivalence = classesWithEqcElements.foldLeft(eqc)(_ |+| _)

    equivalences = existingEquivalences + newEquivalence
  }
}
