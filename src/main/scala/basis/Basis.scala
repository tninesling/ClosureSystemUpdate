package basis

import scala.collection.SortedSet
import scala.io.Source

case class Implication(premise: Set[String], conclusion: Set[String])

trait Basis {
  var baseSet: Set[String] = Set()
  var basis: Set[Implication] = Set()

  def basisEquals(other: Basis): Boolean =
    this.basis.subsetOf(other.basis) && other.basis.subsetOf(this.basis)

  def basisEquals(other: Set[Implication]): Boolean =
    this.basis.subsetOf(other) && other.subsetOf(this.basis)

  def closure(A: Set[String]) =
    A | basis.filter(_.premise.subsetOf(A))
      .map(_.conclusion)
      .flatten

  // Modifies the basis to include the closed set in its Moore family
  def update(closedSet: Set[String])

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

  ////////// Update to handle sets which have more than one element ////////
  def parseSet(setString: String) =
    setString.split(" ").toSet

  def generateBaseSet() = {
    baseSet = basis.map(imp => imp.premise | imp.conclusion).flatten
  }

  // Generates the Moore Family defined by the Basis
  def mooreFamily(): Set[Set[String]] = {
    var closedSets = Set(Set[String]())
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
        closedSets = closedSets + Y.to[Set]
    }

    closedSets
  }

  override def toString(): String = {
    implicationsToString(basis.toList)
  }

  def implicationsToString(basis: List[Implication]): String = {
    basis match {
      case Nil => ""
      case imp::Nil => implicationToString(imp)
      case imp::tail => s"${implicationToString(imp)}, ${implicationsToString(tail)}"
    }
  }

  def implicationToString(imp: Implication): String = {
    s"${listToString(imp.premise.toList)} -> ${listToString(imp.conclusion.toList)}"
  }

  def listToString(ls: List[String]): String = {
    ls match {
      case Nil => ""
      case x::Nil => x
      case x::tail => x + " " + listToString(tail)
    }
  }

  def brokenBy(strSet: Set[String]) =
    basis.filter(imp => imp.premise.subsetOf(strSet) && !imp.conclusion.subsetOf(strSet))
}
