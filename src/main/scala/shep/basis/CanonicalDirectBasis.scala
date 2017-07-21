package shep.basis

import shep._

import scala.collection.mutable.HashMap

trait CanonicalDirectBasis extends Basis {
  var sectors: HashMap[Set[String], Sector] = HashMap[Set[String], Sector]()

  def toNaiveCdb(): NaiveCanonicalDirectBasis = {
    val ncdb = new ClosureSystem with NaiveCanonicalDirectBasis
    ncdb.baseSet = this.baseSet
    ncdb.basis = this.basis
    ncdb
  }

  def toWildCdb(): WildCanonicalDirectBasis = {
    val wcdb = new ClosureSystem with WildCanonicalDirectBasis
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

  abstract override def fromFile(fileLocation: String) = {
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
      case None => new Sector(d)
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

  case class Sector(rightSide: Set[String], implications: Set[SectorImplication] = Set()) {
    def +(that: SectorImplication) =
      Sector(rightSide, implications + that)

    def update(closedSet: Set[String]): Sector = {
      // The implications which are broken have left sides which are subsets
      // of the new closed set and a right side not in the closed set
      val brokenImplications =
        implications.filter(_.leftSide.subsetOf(closedSet))

      val validImplications =
        implications &~ brokenImplications // &~ is set difference

      val extensions =
        brokenImplications.flatMap(implication =>
          validExtensions(implication, closedSet)
        )

      val newSector = Sector(rightSide, validImplications | extensions)
      newSector.enrich()
    }

    def validExtensions(implication: SectorImplication, closedSet: Set[String]): Set[SectorImplication] = {
      val validElements = baseSet &~ (closedSet | rightSide | implication.skewedDifferences)

      validElements.map(element =>
        SectorImplication(implication.leftSide + element, Set())
      )
    }

    // Enriches the data structure by finding the skewed differences for each SectorImplication
    def enrich(): Sector = {
      val enrichedImplications = implications.map { implication =>
        val skewedDifferences =
          implications.map(_.leftSide &~ implication.leftSide)
            .filter(_.size == 1)
            .flatten

        SectorImplication(implication.leftSide, skewedDifferences)
      }

      Sector(rightSide, enrichedImplications)
    }
  }

  case class SectorImplication(leftSide: Set[String], skewedDifferences: Set[String])
}
