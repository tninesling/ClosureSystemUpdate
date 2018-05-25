package basis

class WildCanonicalDirectBasis extends NaiveCanonicalDirectBasis {

  override def update(X: Set[String]) = {
    var sigma: Set[Implication] = Set()

    // Build extended basis
    basis foreach { implication =>
      // implication is A -> B
      val A = implication.premise
      val B = implication.conclusion

      if (!A.subsetOf(X)) {
        sigma = sigma + implication
      } else {
        sigma = sigma + (Implication(A, B & X))
        (baseSet &~ X) foreach { y =>
          sigma = sigma + (Implication(A + y, B))
        }
      }
    }
    basis = sigma

    // Remove unnecessary implications
    sigma foreach { implication => // for each (A -> B)
      var A = implication.premise
      var B = implication.conclusion
      var Y = A

      (basis - implication) foreach { otherImplication =>
        val C = otherImplication.premise
        val D = otherImplication.conclusion

        if (C.subsetOf(A))
          Y = Y | D
      }

      if (B.subsetOf(Y)) {
        basis = basis - implication
      } else {
        basis = (basis - implication) + (Implication(A, B | Y))
      }
    }
  }

  def unitBasis = {
    val nonRedundant = this.basis.map(t => Implication(t.premise, t.conclusion &~ t.premise))

    val restricted = nonRedundant.map { t =>
      val stronger = (nonRedundant - t).filter(x => x.premise.subsetOf(t.premise))
      val strongerConsequents = stronger.map(_.conclusion).flatten
      Implication(t.premise, t.conclusion &~ strongerConsequents)
    }

    for (
      tuple <- restricted;
      right <- tuple.conclusion
    ) yield (Implication(tuple.premise, Set(right)))
  }
}
