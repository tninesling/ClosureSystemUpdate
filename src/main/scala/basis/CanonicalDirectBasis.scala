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
        .map(_._1)
        .map(x => (x, x | closure(x)))

    wcdb
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

  def addImplication(implication: (Set[String],Set[String])) = {
    val C = implication._1
    val d = implication._2

    val dSec = sectors.get(d) match {
      case Some(sec) => sec
      case None => new Sector(baseSet, d)
    }

    val E = basis.filter(_._2.equals(d)) // only check implications with right side d
                 .map(_._1 &~ C) // map all of these to set difference with C
                 .filter(_.size == 1) // keep if set difference is a singleton
                 .flatten

    sectors.put(d, dSec + SectorImplication(C, E))
  }

  def sectorsToBasis(): Set[(Set[String], Set[String])] = {
    val sectorTuples = sectors.toSet

    sectorTuples.map { tuple =>
      tuple._2.implications.map { imp =>
        (imp.leftSide, tuple._1)
      }
    }.flatten
  }

}
