package basis

case class Sector(
    parent: CanonicalDirectBasis,
    rightSide: Set[String],
    implications: Set[SectorImplication] = Set()) {

  def +(that: SectorImplication) =
    Sector(parent, rightSide, implications + that)

  def update(closedSet: Set[String]): Sector = {
    // The implications which are broken have left sides which are subsets
    // of the new closed set and a right side not in the closed set
    val brokenImplications = implications.filter(_.leftSide.subsetOf(closedSet))
    val validImplications = implications &~ brokenImplications

    val extensions =
      brokenImplications.flatMap(implication =>
        validExtensions(implication, closedSet)
      )

    val newSector = Sector(parent, rightSide, validImplications | extensions)
    newSector.enrich()
  }

  def validExtensions(implication: SectorImplication, closedSet: Set[String]): Set[SectorImplication] = {
    val validElements = parent.baseSet &~ (closedSet | rightSide | implication.skewedDifferences)

    validElements.map(element =>
      SectorImplication(implication.leftSide + element, Set())
    )
  }

  // Enriches the data structure by finding the skewed differences for each SectorImplication
  def enrich(): Sector = {
    val enrichedImplications = implications.map(imp =>
      SectorImplication(imp.leftSide, findSkewedDifferences(imp))
    )

    Sector(parent, rightSide, enrichedImplications)
  }

  def findSkewedDifferences(imp: SectorImplication) =
    implications.map(_.leftSide &~ imp.leftSide)
      .filter(_.size == 1)
      .flatten
}

case class SectorImplication(leftSide: Set[String], skewedDifferences: Set[String])
