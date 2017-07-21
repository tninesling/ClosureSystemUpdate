package shep.basis

trait WildCanonicalDirectBasis extends NaiveCanonicalDirectBasis {

  override def update(X: Set[String]) = {
    var sigma: Set[(Set[String], Set[String])] = Set()

    // Build extended basis
    basis foreach { implication =>
      // implication is A -> B
      val A = implication._1
      val B = implication._2

      if (!A.subsetOf(X)) {
        sigma = sigma + implication
      } else {
        sigma = sigma + ((A, B & X))
        (baseSet &~ X) foreach { y =>
          sigma = sigma + ((A + y, B))
        }
      }
    }
    basis = sigma

    // Remove unnecessary implications
    sigma foreach { implication => // for each (A -> B)
      var A = implication._1
      var B = implication._2
      var Y = A

      (basis - implication) foreach { otherImplication =>
        val C = otherImplication._1
        val D = otherImplication._2

        if (C.subsetOf(A))
          Y = Y | D
      }

      if (B.subsetOf(Y)) {//&& !baseSet.subsetOf(B)) { // !baseSet.subsetOf(B) added by me, not part of Wild's algorithm
        basis = basis - implication
      } else {
        basis = (basis - implication) + ((A, B | Y))
      }
    }
  }

  def unitBasis = {
    val uBasis =
      for (
        tuple <- this.basis;
        right <- tuple._2 &~ tuple._1
      ) yield ((tuple._1, Set(right)))

    uBasis.filter(implication =>
      (uBasis - implication).forall(basisImp =>
        !(basisImp._1.subsetOf(implication._1) &&
        basisImp._2.equals(implication._2))
      )
    )
  }
}
