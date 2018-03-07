package basis

import scala.io.Source

class Table {
  var header = List.empty[String]
  var columns = List.empty[List[Int]]
  var rows = List.empty[List[Int]]
  var equivalences = Set.empty[Implication]
  var previousTable: Option[Table] = None


  /** Returns a reduced version of the table. To reduce the table we perform the following:
   *  1) Remove columns which are all zeros or all ones
   *  2) Remove columns whose closures are not unique
   *  3) If closure(y) = closure(closure(y)\y), remove y
   */
  def reduce(): Table = {
    val reducedTable = this.nonConstant
      .uniqueSingletonClosures
      .uniqueClosures

    reducedTable.previousTable = Some(this)
    reducedTable
  }

  // Returns indexes of non-constant columns
  def nonConstant(): Table = {
    val colSums = columns.map(col => col.tail.foldLeft(col.head)(_ + _))
    var nonConstantCols = List.empty[Int]
    var newEquivalences = Set.empty[Implication]

    colSums.zipWithIndex.foreach { case (colSum, ind) =>
      if (colSum == this.rows.size) {
        // If col x is all ones, we add y -> x for all y
        val colHeader = this.header(ind)
        /*val newImps = (this.header.toSet - colHeader).map(prem =>
          Implication(Set(prem), Set(colHeader))
        )*/
        val newImps = Set(Implication(Set(""), Set(colHeader)))
        newEquivalences = newEquivalences | newImps
      } else {
        nonConstantCols = nonConstantCols :+ ind
      }
    }

    val nonConstantTable = this.select(nonConstantCols)
    nonConstantTable.equivalences = this.equivalences | newEquivalences
    nonConstantTable
  }

  // Returns indexes of columns with unique closures
  def uniqueSingletonClosures(): Table = {
    val indices = (0 to this.header.size - 1).toList
    val closures = indices.map(x => closure(Set(x)))
    var newEquivalences = Set.empty[Implication]

    val uniqueIndices = indices.filterNot { i =>
      indices.filter(_ < i).exists { j =>
        val isDuplicate = closures(i).equals(closures(j))
        if (isDuplicate) {
          val hi = this.header(i)
          val hj = this.header(j)
          val newImps = Set(Implication(Set(hi), Set(hj)), Implication(Set(hj), Set(hi)))
          newEquivalences = newEquivalences | newImps
        }
        isDuplicate
      }
    }

    val uniqueClosuresTable = this.select(uniqueIndices)
    uniqueClosuresTable.equivalences = this.equivalences | newEquivalences
    uniqueClosuresTable
  }

  // Removes column y if closure(y) == closure(closure(y)/y)
  def uniqueClosures(): Table = {
    var newEquivalences = Set.empty[Implication]

    val indices = (0 to this.header.size - 1).toList.filterNot { i =>
      val y = Set(i)
      val closureOfThis = closure(y)
      val closureOfThose = closure(closureOfThis &~ y)

      val reduceable = closureOfThis.equals(closureOfThose)
      if (reduceable) {
        val h1 = Set(header(i))
        val h2 = (closureOfThis &~ y).map(x => header(x))

        newEquivalences = newEquivalences | Set(Implication(h1, h2), Implication(h2, h1))
      }
      reduceable
    }

    val uniqueTable = this.select(indices)
    uniqueTable.equivalences = this.equivalences | newEquivalences
    uniqueTable
  }

  def buildDBasis(): DBasis = {
    val family = mooreFamily()
    println(s"Family size: ${family.size}")

    val dBasis = new DBasis
    dBasis.baseSet = family.flatten
    dBasis.basis = dBasis.baseSet.map(baseElement => Implication(Set[String](), Set(baseElement)))

    family foreach { x =>
      dBasis.update(x)
      //println(s"Basis updated with $x")
    }
    dBasis.equivalences = this.equivalences

    dBasis
  }

  def buildCdBasis(): CanonicalDirectBasis = {
    val family = mooreFamily()
    buildCdBasis(family)
  }

  def buildCdBasis(family: Set[Set[String]]): CanonicalDirectBasis = {
    val baseSet = family.flatten

    val cdBasis = new CanonicalDirectBasis
    cdBasis.baseSet = baseSet
    cdBasis.basis = baseSet.map(baseElement => Implication(Set[String](), Set(baseElement)))
    cdBasis.buildSectors()

    family.foreach{ x =>
      //println("updated with " + x)
      cdBasis.update(x)
    }

    cdBasis.equivalences = this.equivalences

    cdBasis
  }

  def buildNcdBasis(): NaiveCanonicalDirectBasis = {
    val family = mooreFamily()//this.previousTable.getOrElse(this).mooreFamily()
    buildNcdBasis(family)
  }

  def buildNcdBasis(family: Set[Set[String]]): NaiveCanonicalDirectBasis = {
    val baseSet = family.flatten

    val cdBasis = new NaiveCanonicalDirectBasis
    cdBasis.baseSet = baseSet
    cdBasis.basis = baseSet.map(baseElement => Implication(Set[String](), Set(baseElement)))
    cdBasis.buildSectors()

    family.foreach(cdBasis.update)

    cdBasis.equivalences = this.equivalences

    cdBasis
  }

  def printFamily() = {
    val family = mooreFamily()
    val txt = "[" + family.map(s => s"[${s.mkString(", ")}]").mkString(", ") + "]"
    println(txt)
  }

  def mooreFamily(): Set[Set[String]] = {
    val setsByIndex = mooreFamilyIndexes()

    setsByIndex.map(indexSet =>
      indexSet.map(index =>
        header(index)
      )
    )
  }

  def mooreFamilyIndexes(): Set[Set[Int]] = {
    var familyIndexes = Set.empty[Set[Int]]
    val baseSet = (0 to header.size - 1).toSet
    val powSet = baseSet.subsets.map(closure).foreach { x =>
      familyIndexes = familyIndexes + x
    }

    familyIndexes
  }

  def closure(indexSet: Set[Int]): Set[Int] =
    rowSupport(columnSupport(indexSet.toList)).toSet

  def columnSupport(indexes: List[Int]): List[Int] =
    support(columns, indexes)

  def rowSupport(indexes: List[Int]): List[Int] =
    support(rows, indexes)

  // Returns a list of indexes where all of the lists in the table are 1
  def support(table: List[List[Int]], indexes: List[Int]): List[Int] = {
    val selectedLists = select(table, indexes)

    if (selectedLists.isEmpty)
      return List[Int]()

    val sumsByIndex = selectedLists.foldLeft(zeroList(selectedLists.head))((A,B) => listSum(A,B))

    sumsByIndex.zipWithIndex
               .filter(_._1 == indexes.size) // select where all lists had a 1
               .map(_._2) // map tuples to the index
  }

  // Returns a copy of the table with only the lists at the provided indexes
  def select(indexes: List[Int]): Table = {
    indexes match {
      case Nil => new Table()
      case index::tail => this(index).prependTo(select(tail))
    }
  }

  def select(columnNames: Set[String]): Table = {
    val indexes =
      header.zipWithIndex
        .filter { case (x,i) => columnNames.contains(x) }
        .map(_._2)

    this.select(indexes.toList)
  }

  def select(table: List[List[Int]], indexes: List[Int]): List[List[Int]] = {
    indexes match {
      case Nil => List[List[Int]]()
      case index::tail => table(index) :: select(table, tail)
    }
  }

  // Sums the two lists by index
  def listSum(A: List[Int], B: List[Int]): List[Int] =
    A.zip(B).map { tuple =>
      tuple._1 + tuple._2
    }

  // Creates a list of zeros the same length as ls
  def zeroList(ls: List[Int]) =
    ls.map(x => 0)

  /** Reads the table from a given file location. Sets both the rows and
   *  columns which will be identical copies of the table.
   *
   *  @param fileLocation the location of the CSV file containing the table
   */
  def fromFile(fileLocation: String) = {
    val csv = readCsv(fileLocation)
    header = csv.head
    rows = csv.tail.map(ls => ls.map(_.toInt))
    columns = transpose(rows)
  }

  def readCsv(fileLocation: String): List[List[String]] = {
    val lines = Source.fromFile(fileLocation).getLines.toList

    lines.map { line =>
      line.split(",").toList
    }
  }

  def transpose(table: List[List[Int]]): List[List[Int]] =
    table match {
      case Nil => Nil
      case _ => {
        // Map each column index to its corresponding column
        (0 to (table.head.size - 1)).map { index =>
          table.map(_(index))
        }.toList
      }
    }

  def toCsv = {
    println(header.mkString(", "))
    rows.foreach(row => println(row.mkString(", ")))
  }

  def apply(index: Int): Table = {
    val newTable = new Table()
    newTable.header = List(this.header(index))
    newTable.columns = List(this.columns(index))
    newTable.rows = transpose(newTable.columns)

    newTable
  }

  def prependTo(that: Table): Table = {
    val newTable = new Table()
    newTable.header = this.header:::that.header
    newTable.columns = this.columns:::that.columns
    newTable.rows = transpose(newTable.columns)

    newTable
  }

  def holds(imp: Implication): Boolean = {
    val premiseCols = this.select(imp.premise)
    val conclusionCols = this.select(imp.conclusion)

    val premiseBools = allRowsEqual1(premiseCols.columns)
    val conclusionBools = allRowsEqual1(conclusionCols.columns)

    premiseBools.zip(conclusionBools).forall { case (x,y) => if (x) y else true }
  }

  def allRowsEqual1(cols: List[List[Int]]): List[Boolean] =
    cols.map(_.map(_ == 1))
      .foldLeft(this.rows.map(x => true))((x,y) => x.zip(y).map { case (x,y) => x && y })

}
