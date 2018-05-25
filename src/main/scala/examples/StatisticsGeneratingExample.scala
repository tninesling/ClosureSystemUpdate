package examples

import basis._

import scala.util.{Try, Success, Failure}

object StatisticsGeneratingExample {
  def main(args: Array[String]) = {
    // Rows, Columns, Total implications, Implications broken, Time taken
    /*
    val stats = (1 to 100).toList.map{x=>// (1 to 50).toList.map { x =>
      // Randomly generate a table and generate the basis
      val t = generateTable()
      val r = t.reduce()
      val cdb = r.buildCdBasis()
      val ncdb = cdb.toNaiveCdb()
      val wcdb = cdb.toWildCdb()
      val db = cdb.toDbasis()

      val origcdb = cdb.basis
      val origdb = db.basis

      // Select some new closed set to be added
      val updateKey = selectUpdateKey(r)

      val basisSize = cdb.basis.size
      val numBroken = cdb.brokenImplications(updateKey).size

      // Update with advanced algorithm
      val cdStart = System.currentTimeMillis()
      cdb.update(updateKey)
      val cdTime = System.currentTimeMillis() - cdStart

      // Update with naive algorithm
      val ncdStart = System.currentTimeMillis()
      ncdb.update(updateKey)
      val ncdTime = System.currentTimeMillis() - ncdStart

      // Update with Wild's algorithm
      val wcdStart = System.currentTimeMillis()
      wcdb.update(updateKey)
      val wcdTime = System.currentTimeMillis() - wcdStart

      val dbStart = System.currentTimeMillis()
      db.update(updateKey)
      val dbTime = System.currentTimeMillis() - dbStart

      val cEqN = Try(assert(cdb.basisEquals(ncdb)))
      val cEqW = Try(assert(cdb.basisEquals(wcdb.unitBasis)))
      val cEqD = Try(assert(db.basisEquals(cdb.toDbasis)))

      (cEqN, cEqW, cEqD) match {
        case (Success(a), Success(b), Success(c)) =>
          List(r.rows.size, r.columns.size, basisSize, numBroken, cdTime, ncdTime, wcdTime, dbTime)
        case (_,_,Failure(e)) => {

          println(s"Dbasis check wrong by ${((cdb.toDbasis.basis &~ db.basis) | (db.basis &~ cdb.toDbasis.basis)).size}")
          t.toCsv
          println(s"Orig CDB: $origcdb")
          println(s"Orig DB: $origdb")
          println(s"Update with $updateKey")
          println(s"CDB: ${cdb.basis}")
          println(s"DB: ${db.basis}")
          println("Difference:")
          println(s"CDB-DB: ${cdb.toDbasis.basis &~ db.basis}")
          println(s"DB-CDB: ${db.basis &~ cdb.toDbasis.basis}")
          System.exit(1)
          Nil
        }
        case _ => {
          println("Computed basis was not equal")
          Nil
        }
      }
    }

    printCsv(stats.toList)
    */
  }

  // Generates a Table object of random size less or equal to 15 x 15
  def generateTable(): Table = {
    val numRows = generateRandomBetween(5, 10)
    val numCols = generateRandomBetween(5, 10)

    val t = new Table()

    // Add header
    t.header = (1 to numCols).toList.map(_.toString)

    // Add rows
    (1 to numRows).foreach { x =>
      t.rows = randomRow(numCols) :: t.rows
    }

    // Assign columns as transpose of rows
    t.columns = t.transpose(t.rows)
    return t
  }

  def generateRandomBetween(min: Int, max: Int) =
    math.ceil(math.random * (max - min)).toInt + min

  def randomRow(rowLength: Int): List[Int] =
    if (rowLength == 1)
      List(generateRandomBinary())
    else generateRandomBinary() :: randomRow(rowLength - 1)

  def generateRandomBinary(): Int =
    math.rint(math.random).toInt

  def selectUpdateKey(t: Table) =
    randomSubset(t.header)

  def randomSubset(ls: List[String]): Set[String] =
    ls match {
      case Nil => Set[String]()
      case x::tail => randomSubset(tail) | maybe(x)
    }

  def maybe(x: String) =
    if (math.random > 0.5)
      Set[String]()
    else
      Set(x)

  def printCsv(stats: List[List[_]]) =
    stats.map(_.mkString(",")).foreach(println)
}
