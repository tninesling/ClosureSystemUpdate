package object equivalences {

  type ClosedSet = Set[String]

  implicit object MaxSizeMinAlphaOrder extends Ordering[ClosedSet] {
    /**
     * We default to the empty set as max (since it should always be the
     * representative of its class), then we compare the size of the sets
     * so nonbinary equivalences will be represented. After this, ties
     * are broken on the length of the minimum element and then alphabetical
     * (using only alphabetical means 14 will be used before 2, this is just
     * a preference for single character column names)
     */
    def compare(x: ClosedSet, y: ClosedSet) = {
      if (x.isEmpty && y.isEmpty)
        0
      else if (x.isEmpty)
        1
      else if (y.isEmpty)
        -1
      else {
        var compareResult = x.size compare y.size

        if (compareResult == 0) {
          compareResult = y.min.size compare x.min.size

          if (compareResult == 0) {
            compareResult = y.min compare x.min
          }
        }
        compareResult
      }
    }
  }

}
