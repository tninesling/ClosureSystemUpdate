package basis

import scala.collection.mutable.HashMap

class CanonicalDirectBasis extends Basis {
  var sectors: HashMap[Set[String], Sector] = HashMap[Set[String], Sector]()

  /**
   *  Adds a new closed set to the Moore family represented by the basis.
   *  The only implications that will be removed are ones with left sides that
   *  are subsets of the closed set and right sides not in the closed set.
   */
  override def update(closedSet: Set[String]) = {
    super.update(closedSet)
    buildSectors()

    val updateKeys = sectors.keySet.filter(key => !key.subsetOf(closedSet))
    updateKeys.foreach{ key =>
      val sector = sectors.get(key)
      sector match {
        case Some(sector) =>
          sectors.put(key, sector.update(closedSet))
        case None => {}
      }
    }

    basis = sectorsToBasis()
  }

  override def handleEquivalence(newSet: Set[String])(e: Equivalence): Unit = {
    e match {
      case BinaryEquivalence(x, y) => {
        val expanded = e.expand(this.baseSet)

        val premOfBroken = newSet.intersect(x | y)
        val concOfBroken = (x | y) &~ newSet

        val replaceable = this.basis.filter(imp => premOfBroken.subsetOf(imp.premise))

        val additionalImps =
          replaceable.map { case Implication(left, right) =>
            Implication(left.diff(premOfBroken).union(concOfBroken), right)
          }

        this.basis = this.basis | additionalImps + Implication(concOfBroken, premOfBroken)
      }
      case AllEquivalence(y) => {
        this.baseSet = this.baseSet | y
        this.basis = this.basis | e.expand(this.baseSet)
      }
      case NonbinaryEquivalence(_,_) => {
        this.basis = this.basis | e.expand(this.baseSet)
      }
    }
  }
/*
  override def handleBinaryEquivalences(newSet: Set[String]) = {
    super.handleBinaryEquivalences(newSet)

    val affectedBinaryEquivs = this.affectedEquivalences(newSet).filter(_.premise.size == 1)
    val binaryUnbrokenEquivs = affectedBinaryEquivs.filter(_.holdsOn(newSet))
    val binaryBrokenEquivs = affectedBinaryEquivs.filterNot(_.holdsOn(newSet))

    // For each broken binary implication x -> y, replace all instances of y with x
    val replacedImps = for {
      replacement <- binaryBrokenEquivs
      replaceable <- this.basis if(replaceable.premise.subsetOf(replacement.premise))
    } yield {
      val newPrem =
        replaceable.premise
          .diff(replacement.premise)
          .union(replaceable.conclusion)

      Implication(newPrem, replaceable.conclusion)
    }

    this.basis = this.basis | binaryUnbrokenEquivs | replacedImps
  }*/

  /**
   *  Removes a meet irreducible element from the corresponding lattice.
   *  This is equivalent to removing a row from the corresponding data table.
   */
  def remove(A: Set[String]) = {
    this.closedSets = this.closedSets - A

    val newImps = for {
      d <- upperCover(A) &~ A
      Y <- minimalTransversals(A)
    } yield Implication(Y, Set(d))

    this.basis = this.basis | newImps
    removeWeakImplications()
  }

  def upperCover(A: Set[String]): Set[String] = {
    val above = this.closedSets.filter(x => A.subsetOf(x)) - A
    above.filterNot(x =>
      (above - x).exists(y =>
        y.subsetOf(x)
      )
    ).headOption // for meet irreducible, the upper cover is unique so head will take only element
     .getOrElse(baseSet)
  }

  /**
   * Gives the set of minimal transversals (w.r.t. subset) on the hypergraph
   * defined by complements of closed subsets of A
   */
  def minimalTransversals(A: Set[String]) = {
    val T = maximalClosedSubsets(A).map(x => A &~ x)
    product(T)
  }

  def maximalClosedSubsets(A: Set[String]) = {
    val below = this.closedSets.filter(_.subsetOf(A)) - A
    below.filterNot(x =>
      (below - x).exists(y =>
        x.subsetOf(y)
      )
    )
  }

  override def fromFile(fileLocation: String) = {
    super.fromFile(fileLocation)
    buildSectors()
  }

  def buildSectors() =
    basis.foreach(addImplication)

  def addImplication(implication: Implication) = {
    val C = implication.premise
    val d = implication.conclusion

    val dSec = sectors.get(d) match {
      case Some(sec) => sec
      case None => new Sector(this, d)
    }

    val E = basis.filter(_.conclusion.equals(d)) // only check implications with right side d
                 .map(_.premise &~ C) // map all of these to set difference with C
                 .filter(_.size == 1) // keep if set difference is a singleton
                 .flatten

    sectors.put(d, dSec + SectorImplication(C, E))
  }

  def sectorsToBasis(): Set[Implication] = {
    val sectorTuples = sectors.toSet

    sectorTuples.map { tuple =>
      tuple._2.implications.map { imp =>
        Implication(imp.leftSide, tuple._1)
      }
    }.flatten
  }

  // Remove implications from the basis which are weaker than existing implications
  def removeWeakImplications() = {
    basis =
      basis.filter(implication =>
        (basis - implication).forall(basisImp =>
          !(basisImp.premise.subsetOf(implication.premise) &&
          basisImp.conclusion.equals(implication.conclusion))
        )
      )
  }

  def toNaiveCdb(): NaiveCanonicalDirectBasis = {
    val ncdb = new NaiveCanonicalDirectBasis
    ncdb.copy(this)
    ncdb
  }

  def toWildCdb(): WildCanonicalDirectBasis = {
    val wcdb = new WildCanonicalDirectBasis
    wcdb.copy(this)

    wcdb.basis =
      this.basis
        .map(_.premise)
        .map(x => Implication(x, x | closure(x)))

    wcdb
  }

  def toDbasis(): DBasis = {
    val db = new DBasis
    db.copy(this)
/*
    val refined = this.nonBinary filterNot {imp1 =>
      (nonBinary - imp1) exists {imp2 =>
        db.refines(binary, imp2, imp1)
      }
    }*/
    val refined = db.filterRefinements(this.binary, this.nonBinary, this.nonBinary)

    db.basis = this.binary | refined
    db
  }

}
