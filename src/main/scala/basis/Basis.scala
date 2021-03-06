package basis

import scala.collection.SortedSet
import scala.io.Source

trait Basis {
  var baseSet = Set.empty[String]
  var basis = Set.empty[Implication]
  var closedSets = Set.empty[Set[String]]


  // Modifies the basis to include the new closed set in its Moore family
  def update(newSet: Set[String]) = {
    this.closedSets = this.closedSets + newSet
  }

  def addImplication(imp: Implication): Unit = {
    basis = basis + imp
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

  // Parses an implication string "1 2 -> 3" as the tuple (Set("1","2"), Set("3"))
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

    powerSet.foreach { subset =>
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

  def isEmpty(): Boolean = basis.isEmpty

  def copyValues(other: Basis) = {
    this.baseSet = other.baseSet
    this.basis = other.basis
    this.closedSets = other.closedSets
  }

  override def equals(other: Any): Boolean =
    other match {
      case x: Basis =>
        basis.equals(x.basis)
      case _ =>
        false
    }

  override def toString(): String =
    if (basis.size < 20)
      basis.mkString(", ")
    else
      basis.take(20).mkString(", ") + " ..."
}
