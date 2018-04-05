package object equivalences {

  type ClosedSet = Set[String]

  implicit object MaxSizeMinAlphaOrder extends Ordering[Set[String]] {
    // compare size, break ties with alphabetic ordering
    def compare(x: ClosedSet, y: ClosedSet) = {
      var compareResult = x.size compare y.size
      if (compareResult == 0) {
        compareResult = y.min compare x.min
      }
      compareResult
    }
  }

}
