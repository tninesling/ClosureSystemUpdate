package basis

import scala.io.Source

class Table {
  var header: List[String] = Nil
  var columns: List[List[Int]] = Nil
  var rows: List[List[Int]] = Nil

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

    family.foreach {closedSet =>
      cdBasis.update(closedSet)
    }

    cdBasis
  }

  def buildNcdBasis(): NaiveCanonicalDirectBasis = {
    val family = mooreFamily()
    buildNcdBasis(family)
  }

  def buildNcdBasis(family: Set[Set[String]]): NaiveCanonicalDirectBasis = {
    val baseSet = family.flatten

    val cdBasis = new NaiveCanonicalDirectBasis
    cdBasis.baseSet = baseSet
    cdBasis.basis = baseSet.map(baseElement => Implication(Set[String](), Set(baseElement)))

    cdBasis.buildSectors()

    family.foreach(closedSet => cdBasis.update(closedSet))

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
    val baseSet = (0 to header.size - 1).toSet
    val powSet = baseSet.subsets.toSet

    powSet.map(s => closure(s))
  }

  /** Returns a reduced version of the table. To reduce the table we perform the following:
   *  1) Remove columns which are all zeros or all ones
   *  2) Remove columns whose closures are not unique
   *  3) If closure(y) = closure(closure(y)\y), remove y
   */
  def reduce(): Table = {
    val nonConstantIndexes = nonConstant()
    val nonConstantTable = select(nonConstantIndexes)

    val uniqueSingletonClosuresIndexes = nonConstantTable.uniqueSingletonClosures()
    val uniqueSingletonClosuresTable = nonConstantTable.select(uniqueSingletonClosuresIndexes)

    val uniqueClosuresIndexes = uniqueSingletonClosuresTable.uniqueClosures()
    val uniqueClosuresTable = uniqueSingletonClosuresTable.select(uniqueClosuresIndexes)

    uniqueClosuresTable
  }

  // Returns indexes of non-constant columns
  def nonConstant(): List[Int] = {
    val filteredCols = columns.zipWithIndex filter { tuple: (List[Int], Int) =>
      val colSum = tuple._1.foldLeft(0)((x,y) => x + y)
      (colSum > 0) && (colSum < tuple._1.size)
    }
    filteredCols.map(_._2)
  }

  // Returns indexes of columns with unique closures
  def uniqueSingletonClosures(index: Int = 0,
                              existingClosures: Set[Set[Int]] = Set[Set[Int]]()
                              ): List[Int] = {
    if (index >= columns.size)
      Nil
    else {
      val currentClosure = closure(Set(index))
      if (existingClosures.contains(currentClosure))
        uniqueSingletonClosures(index + 1, existingClosures)
      else
        index :: uniqueSingletonClosures(index + 1, existingClosures + currentClosure)
    }
  }

  // Removes column y if closure(y) == closure(closure(y)/y)
  def uniqueClosures(index: Int = 0): List[Int] = {
    if (index >= columns.size )
      Nil
    else {
      val y = Set(index)
      val Y = closure(y)

      if (y.subsetOf(closure(Y &~ y)))
        uniqueClosures(index + 1)
      else
        index :: uniqueClosures(index + 1)
    }
  }

  def closure(A: Set[Int]): Set[Int] =
    rowSupport(columnSupport(A.toList)).toSet

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
        .filter {
          case (x,i) => columnNames.contains(x)
        }.map(_._2)

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

    premiseBools zip conclusionBools forall { case (x,y) => if (x) y else true }
  }

  def allRowsEqual1(cols: List[List[Int]]): List[Boolean] =
    cols.map(_.map(_ == 1))
      .foldLeft(this.rows.map(x => true))((x,y) => x.zip(y).map { case (x,y) => x && y })

}
