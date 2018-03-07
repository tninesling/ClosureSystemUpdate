package basis

import scala.collection.SortedSet
import scala.io.Source

trait Basis {
  var baseSet = Set.empty[String]
  var basis = Set.empty[Implication]
  var equivalences = Set.empty[Implication]
  var closedSets = Set.empty[Set[String]]

  // Handler for equivalences in the table which need to be evaluated before update
  def handleEquivalences(newSet: Set[String]) = {
    handleBinaryEquivalences(newSet)
    handleNonbinaryEquivalences(newSet)

    // Remove the affected equivalences from our tracked equivalences
    // since they no longer hold on the table
    this.equivalences = this.equivalences &~ this.affectedEquivalences(newSet)
  }

  /**
   * Equivalences x -> y for all x must be removed; these are stored with empty string
   * in premise. All other binary equivalences must be handled independently by
   * subclasses
   */
  def handleBinaryEquivalences(newSet: Set[String]) = {
    val equivs = this.equivalences.filter(_.premise.equals(Set("")))
    val newImps = for {
      eq <- equivs
      prem <- this.baseSet &~ eq.conclusion
    } yield Implication(Set(prem), eq.conclusion)
    
    this.baseSet = this.baseSet | newImps.flatMap(_.conclusion) // breaking x -> y is equivalent to adding y back to table
    this.basis = this.basis | newImps
    this.equivalences = this.equivalences &~ equivs
  }

  def handleNonbinaryEquivalences(newSet: Set[String]) = {
    val nonbinaryEquivs =
      this.affectedEquivalences(newSet).filter(x =>
        (x.premise.size > 1) || (x.conclusion.size > 1)
      )

    val goodEquivs = nonbinaryEquivs.filter(_.holdsOn(newSet))
    val badEquivs = nonbinaryEquivs.filterNot(_.holdsOn(newSet))

    val expandedEquivs = for {
      imp <- badEquivs
      x <- imp.premise
      y <- imp.conclusion
    } yield Implication(Set(x), Set(y))

    this.basis = this.basis | goodEquivs | expandedEquivs
  }

  def affectedEquivalences(newSet: Set[String]) =
    this.equivalences.filter(x =>
      x.premise
        .union(x.conclusion)
        .intersect(newSet)
        .nonEmpty
    )

  // Modifies the basis to include the new closed set in its Moore family
  def update(newSet: Set[String]) = {
    this.closedSets = this.closedSets + newSet
    this.handleEquivalences(newSet)
  }

  def unbrokenImplications(newSet: Set[String]) =
    this.basis.filter(_.holdsOn(newSet))

  def brokenImplications(newSet: Set[String]) =
    this.basis &~ unbrokenImplications(newSet)

  def binary = this.basis.filter(_.isBinary)
  def nonBinary = this.basis &~ binary

  def basisEquals(other: Basis): Boolean =
    this.basis.subsetOf(other.basis) && other.basis.subsetOf(this.basis)

  def basisEquals(other: Set[Implication]): Boolean =
    this.basis.subsetOf(other) && other.subsetOf(this.basis)

  def closure(A: Set[String]) =
    A | basis.filter(_.premise.subsetOf(A))
      .map(_.conclusion)
      .flatten

  /** Reads the basis for a closure
   *  system. Expects a file formatted as the following:
   *  [12->3, 4->5, 43->2]
   *  Individual implications are separated by commas,
   *  and an implication is left->right
   *
   *  @param fileLocation - the name of the file containing the CD basis
   */
  def fromFile(fileLocation: String) = {
    val line = Source.fromFile(fileLocation).getLines.next
    val implicationStrings = line.substring(1, line.length - 1).split(", ") // Remove brackets

    implicationStrings.foreach{implication =>
      basis = basis + parseImplication(implication)
    }

    generateBaseSet()
  }

  // Parses an implication string "12->3" as the tuple (Set("1","2"), Set("3"))
  def parseImplication(impString: String): Implication = {
    val split = impString.split("->")
    val left = parseSet(split(0).trim())
    val right = parseSet(split(1).trim())

    Implication(left, right)
  }

  def parseSet(setString: String) =
    setString.split(" ").toSet

  def generateBaseSet() = {
    baseSet = basis.map(imp => imp.premise | imp.conclusion).flatten
  }

  // Generates the Moore Family defined by the Basis
  def mooreFamily(): Set[Set[String]] = {
    var family = Set(Set[String]())
    val powerSet = baseSet.subsets.toList

    powerSet foreach { subset =>
      var Y = subset.to[SortedSet]
      var previousY = Set[String]()
      while (previousY.size < Y.size) {
        previousY = Y.to[Set]
        Y = Y | basis.filter(imp => imp.premise.subsetOf(Y))
                     .flatMap(_.conclusion)
      }

      if (Y.size > 0)
        family = family + Y.to[Set]
    }

    family
  }

  /** For Sets A, B return {{a,b}: a in A, b in B} */
  def product[T](operands: Set[Set[T]]): Set[Set[T]] =
    product[T](Set(Set.empty[T]), operands)

  def product[T](acc: Set[Set[T]], operands: Set[Set[T]]): Set[Set[T]] = {
    if (operands.isEmpty)
      acc
    else {
      val newAcc = for {
        s <- acc
        x <- operands.head
      } yield s + x

      product(newAcc, operands.tail)
    }
  }

  def copy(other: Basis) = {
    this.baseSet = other.baseSet
    this.equivalences = other.equivalences
    this.basis = other.basis
    this.closedSets = other.closedSets
  }

  override def toString(): String =
    basis.mkString(", ")
}
