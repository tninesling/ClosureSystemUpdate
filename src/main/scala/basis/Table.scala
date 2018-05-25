package basis

import equivalences._
import equivalences.EquivalenceClass._
import equivalences.syntax.equivalenceclass._
import syntax.implication._

import cats.Monoid
import cats.syntax.semigroup._
import scala.io.Source
//import scala.collection.mutable.TreeSet

class Table {
  var header = List.empty[String]
  var columns = List.empty[List[Int]]
  var rows = List.empty[List[Int]]
  var equivalences = Set.empty[EquivalenceClass]
  //var bottomElement = Set.empty[String]
  var previousTable: Option[Table] = None


  /** Returns a reduced version of the table. To reduce the table we perform the following:
   *  1) Remove columns which are all zeros or all ones
   *  2) Remove columns whose closures are not unique
   *  3) If closure(y) = closure(closure(y)\y), remove y
   */
  def reduce(): Table = {
    val reducedTable = nonConstant
      .uniqueSingletonClosures
      .uniqueClosures

    reducedTable.previousTable = Some(this)
    reducedTable
  }

  // Returns indexes of non-constant columns
  def nonConstant(): Table = {
    val colSums = columns.map(col => col.tail.foldLeft(col.head)(_ + _))
    var nonConstantCols = List.empty[Int]

    colSums.zipWithIndex.foreach { case (colSum, ind) =>
      if (colSum == rows.size) {
        // If col x is all ones, we add y -> x for all y
        val colHeader = Set(header(ind))
        addBinaryEquivalence(Set.empty[String], colHeader)//addBottomElement(colHeader)
      } else {
        nonConstantCols = nonConstantCols :+ ind
      }
    }

    val nonConstantTable = select(nonConstantCols)
    nonConstantTable.equivalences = equivalences
    //nonConstantTable.bottomElement = bottomElement
    nonConstantTable
  }

  /*def addBottomElement(s: ClosedSet) = {
    if (bottomElement.nonEmpty) {
      equivalences.foreach{ equiv =>
        if (equiv.contains(bottomElement)) {
          equiv.add(s)
          bottomElement = equiv.representative.getOrElse(s)
        } else {
          equiv.remove(s)
        }
      }
    } else {
      bottomElement = s
      equivalences = equivalences.flatMap(_.partition(s))
    }
  }*/

  // Returns indexes of columns with unique closures
  def uniqueSingletonClosures(): Table = {
    val indices = (0 to header.size - 1).toList
    val closures = indices.map(x => closure(List(x)))

    val uniqueIndices = indices.filterNot { i =>
      indices.filter(_ < i).exists { j =>
        val isDuplicate = closures(i).equals(closures(j))
        if (isDuplicate) {
          val hi = header(i)
          val hj = header(j)
          addBinaryEquivalence(Set(hi), Set(hj))
        }
        isDuplicate
      }
    }

    val uniqueClosuresTable = select(uniqueIndices)
    uniqueClosuresTable.equivalences = equivalences
    //uniqueClosuresTable.bottomElement = bottomElement
    uniqueClosuresTable
  }

  def addBinaryEquivalence(s1: ClosedSet, s2: ClosedSet) = {
    val newEqClass = equivalences.filter(eqc =>
      eqc.contains(s1) || eqc.contains(s2)
    ).foldLeft(Monoid[EquivalenceClass].empty)(_ |+| _)

    val unaffected = equivalences.filterNot(eqc =>
      eqc.contains(s1) || eqc.contains(s2)
    )

    equivalences = unaffected + newEqClass
  }

  // Removes column y if closure(y) == closure(closure(y)/y)
  def uniqueClosures(): Table = {
    val indices = (0 to header.size - 1).toList.filterNot { i =>
      val y = Set(i)
      val closureOfThis = closure(y.toList)
      val closureOfThose = closure((closureOfThis &~ y).toList)

      val reduceable = closureOfThis.equals(closureOfThose)
      if (reduceable) {
        val h1 = Set(header(i))
        val h2 = (closureOfThis &~ y).map(x => header(x))
        addNonbinaryEquivalence(h1, h2)
      }
      reduceable
    }

    val uniqueTable = select(indices)
    uniqueTable.equivalences = equivalences
    //uniqueTable.bottomElement = bottomElement
    uniqueTable
  }

  // Adds cs to the equivalence class of s
  def addNonbinaryEquivalence(s1: ClosedSet, s2: ClosedSet) =
    equivalences.withFilter(_.contains(s1)).foreach(_.add(s2))

  def buildCanonicalDirectBasis() = buildBasis(new CanonicalDirectBasis())

  def buildDBasis() = buildBasis(new DBasis())

  def buildNaiveCanonicalDirectBasis() = buildBasis(new NaiveCanonicalDirectBasis())

  def buildLoggingDBasis() = buildBasis(new DBasis with LoggingBasis)

  def buildBasis[T <: Basis](basis: T): T = {
    basis.baseSet = header.toSet
    basis.basis = basis.baseSet.map(x => Set.empty[String] --> x)

    rows.foreach(row =>
      basis.update(rowToClosedSet(row))
    )

    basis
  }

  def buildEqBasis(reducedBasis: Basis): EqBasis = {
    val eqb = new EqBasis(reducedBasis)
    val base = header.toSet
    val allEq: EquivalenceClass =
      base.foldLeft(Monoid[EquivalenceClass].empty)(_ <=> _) <=> Set.empty[String]

    eqb.equivalences = Set(allEq)
    //eqb.bottomElement = allEq

    rows.foreach(row =>
      eqb.update(rowToClosedSet(row))
    )

    eqb
  }

  def buildEqDBasis(): DBasis = {
    val db = new DBasis with EquivalenceHandling
    // start with set of single equivalence class containing all elements and empty set
    db.equivalences = Set(
      header.foldLeft(Set.empty[String].eqClass)(_ <=> _)
    )

    rows.map(rowToClosedSet)
      .foreach(db.update)

    db
  }

  def buildEqCdb(): NaiveCanonicalDirectBasis = {
    val cdb = new NaiveCanonicalDirectBasis with EquivalenceHandling
    cdb.equivalences = Set(header.foldLeft(Set.empty[String].eqClass)(_ <=> _))

    rows.map(rowToClosedSet).foreach(cdb.update)
    cdb
  }

  def rowToClosedSet(row: List[Int]): Set[String] =
    row.zip(header)
      .filter(_._1 == 1)
      .map(_._2)
      .toSet

  def printFamily() = {
    val family = mooreFamily()
    val txt = "[" + family.map(s => s"[${s.mkString(", ")}]").mkString(", ") + "]"
    println(txt)
  }

  def mooreFamily(): Iterator[ClosedSet] =
    header.toSet.subsets.map(closure)

  def closure(cs: ClosedSet): ClosedSet = {
    val indices =
      header.zipWithIndex
        .filter(x => cs.contains(x._1))
        .map(_._2)

    val closureIndices = closure(indices)
    closureIndices.map(i => header(i))
  }

  def closure(indexList: List[Int]): Set[Int] =
    rowSupport(columnSupport(indexList)).toSet

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

    select(indexes.toList)
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
    equivalences = header.map(_.eqClass).toSet + Set.empty[String].eqClass
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

  def takeNRows(n: Int): Table = {
    val t = new Table
    t.header = header
    t.rows = rows.take(n)
    t.columns = transpose(t.rows)
    t
  }

  override def toString(): String = {
    val ls = header.mkString(", ") :: rows.map(_.mkString(", "))
    ls.mkString("\n")
  }

  def toCsv() = {
    println(header.mkString(", "))
    rows.foreach(row => println(row.mkString(", ")))
  }

  def apply(index: Int): Table = {
    val newTable = new Table()
    newTable.header = List(header(index))
    newTable.columns = List(columns(index))
    newTable.rows = transpose(newTable.columns)

    newTable
  }

  def prependTo(that: Table): Table = {
    val newTable = new Table()
    newTable.header = header:::that.header
    newTable.columns = columns:::that.columns
    newTable.rows = transpose(newTable.columns)

    newTable
  }

  def holds(imp: EqImplication): Boolean = {
    val premiseCols = select(imp.premise.flatMap(_.representative).flatten)
    val conclusionCols = select(imp.conclusion.flatMap(_.representative).flatten)

    val premiseBools = allRowsEqual1(premiseCols.columns)
    val conclusionBools = allRowsEqual1(conclusionCols.columns)

    premiseBools.zip(conclusionBools).forall { case (x,y) => if (x) y else true }
  }

  def allRowsEqual1(cols: List[List[Int]]): List[Boolean] =
    cols.map(_.map(_ == 1))
      .foldLeft(rows.map(x => true))((x,y) => x.zip(y).map { case (x,y) => x && y })

  def isEmpty() = rows.isEmpty && columns.isEmpty

  def nonEmpty() = !isEmpty()
}

object Table {
  def fromFile(s: String) = {
    val t = new Table()
    t.fromFile(s)
    t
  }
}
