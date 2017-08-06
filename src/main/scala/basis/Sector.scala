package basis

case class Sector(baseSet: Set[String], rightSide: Set[String], implications: Set[SectorImplication] = Set()) {
  def +(that: SectorImplication) =
    Sector(baseSet, rightSide, implications + that)

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

    val newSector = Sector(baseSet, rightSide, validImplications | extensions)
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

    Sector(baseSet, rightSide, enrichedImplications)
  }
}
