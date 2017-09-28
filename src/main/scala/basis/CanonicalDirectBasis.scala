package basis

import scala.collection.mutable.HashMap

class CanonicalDirectBasis extends Basis {
  var sectors: HashMap[Set[String], Sector] = HashMap[Set[String], Sector]()

  def toNaiveCdb(): NaiveCanonicalDirectBasis = {
    val ncdb = new NaiveCanonicalDirectBasis
    ncdb.baseSet = this.baseSet
    ncdb.basis = this.basis
    ncdb
  }

  def toWildCdb(): WildCanonicalDirectBasis = {
    val wcdb = new WildCanonicalDirectBasis
    wcdb.baseSet = this.baseSet

    wcdb.basis =
      this.basis
        .map(_.premise)
        .map(x => Implication(x, x | closure(x)))

    wcdb
  }

  def toDbasis(): DBasis = {
    val db = new DBasis
    db.baseSet = this.baseSet
    db.basis = this.basis

    val binary = this.basis.filter(imp => ((imp.premise.size == 1) && (imp.conclusion.size == 1)))
    val nonBinary = this.basis &~ binary

    val refined = nonBinary filterNot {imp1 =>
      (nonBinary - imp1) exists {imp2 =>
        db.refines(imp2, imp1)
      }
    }

    db.basis = binary | refined
    db
  }

  /** Adds a new closed set to the Moore family represented by the basis.
   *  The only implications that will be removed are ones with left sides that
   *  are subsets of the closed set and right sides not in the closed set.
   */
  def update(closedSet: Set[String]) = {
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
      case None => new Sector(baseSet, d)
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

}
