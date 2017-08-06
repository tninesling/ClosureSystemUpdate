package basis

class WildCanonicalDirectBasis extends NaiveCanonicalDirectBasis {

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

      if (B.subsetOf(Y)) {
        basis = basis - implication
      } else {
        basis = (basis - implication) + ((A, B | Y))
      }
    }
  }

  def unitBasis = {
    val nonRedundant = this.basis.map(t => (t._1, t._2 &~ t._1))

    val restricted = nonRedundant.map { t =>
      val stronger = (nonRedundant - t).filter(x => x._1.subsetOf(t._1))
      val strongerConsequents = stronger.map(_._2).flatten
      (t._1, t._2 &~ strongerConsequents)
    }

    for (
      tuple <- restricted;
      right <- tuple._2
    ) yield ((tuple._1, Set(right)))
  }
}
