package equivalences

object syntax {
  implicit class stringImpliesSyntax(s1: String) {
    def implies(s2: String) = EqImplication.implies(s1, s2)

    def -->(s2: String) = implies(s2)
  }

  implicit class equivalenceClassSetImpliesSyntax(eqs1: Set[EquivalenceClass]) {
    def implies(eqs2: Set[EquivalenceClass]) = EqImplication(eqs1, eqs2)

    def -->(eqs2: Set[EquivalenceClass]) = implies(eqs2)
  }

  implicit class equivalenceClassImpliesSyntax(eq1: EquivalenceClass) {
    def implies(eq2: EquivalenceClass) = EqImplication.implies(eq1, eq2)

    def -->(eq2: EquivalenceClass) = implies(eq2)
  }
}
