package equivalences

import scala.collection.mutable.TreeSet

object syntax {
  // EqImplication syntax
  object eqimplication {
    implicit class stringImpliesSyntax(s1: String) {
      def implies(s2: String): EqImplication = EqImplication.implies(s1, s2)

      def implies(eqc: EquivalenceClass): EqImplication = EqImplication.implies(s1, eqc)

      def -->(s2: String) = implies(s2)

      def -->(eqc: EquivalenceClass) = implies(eqc)
    }

    implicit class equivalenceClassSetImpliesSyntax(eqs1: Set[EquivalenceClass]) {
      def implies(eqs2: Set[EquivalenceClass]): EqImplication = EqImplication(eqs1, eqs2)

      def implies(eqc: EquivalenceClass): EqImplication = implies(Set(eqc))

      def implies(s: String): EqImplication = EqImplication(eqs1, Set(EquivalenceClass(TreeSet(Set(s)))))

      def -->(eqs2: Set[EquivalenceClass]) = implies(eqs2)

      def -->(eqc: EquivalenceClass) = implies(eqc)

      def -->(s: String) = implies(s)
    }

    implicit class equivalenceClassImpliesSyntax(eq1: EquivalenceClass) {
      def implies(eq2: EquivalenceClass) = EqImplication.implies(eq1, eq2)

      def -->(eq2: EquivalenceClass) = implies(eq2)
    }
  }

  // EquivalenceClass syntax
  object equivalenceclass {
    implicit class stringEquivalenceClassConstructorSyntax(s: String) {
      def eqClass(): EquivalenceClass =
        EquivalenceClass(TreeSet(Set(s)))
    }

    implicit class stringSetEquivalenceClassConstructorSyntax(s: ClosedSet) {
      def eqClass(): EquivalenceClass =
        EquivalenceClass(TreeSet(s))
    }

    implicit class stringEquivalentSyntax(s1: String) {
      def equivalentTo(s2: String): EquivalenceClass =
        EquivalenceClass(TreeSet(Set(s1), Set(s2)))

      def <=>(s2: String) = equivalentTo(s2)

      def equivalentTo(s2: ClosedSet): EquivalenceClass =
        EquivalenceClass(TreeSet(Set(s1), s2))

      def <=>(s2: ClosedSet) = equivalentTo(s2)
    }

    implicit class closedSetEquivalentSyntax(cs: ClosedSet) {
      def equivalentTo(cs2: ClosedSet): EquivalenceClass =
        EquivalenceClass(TreeSet(cs, cs2))

      def <=>(cs2: ClosedSet) = equivalentTo(cs2)

      def equivalentTo(s: String): EquivalenceClass =
        equivalentTo(Set(s))

      def <=>(s: String) = equivalentTo(s)
    }
  }
}
