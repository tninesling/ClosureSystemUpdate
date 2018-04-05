package basis

object syntax {
  implicit class implicableSet(s1: Set[String]) {
    def implies(s2: Set[String]): Implication = Implication(s1, s2)

    def implies(s2: String): Implication = implies(Set(s2))

    def -->(s2: Set[String]) = implies(s2)

    def -->(s2: String) = implies(s2)
  }

  implicit class implicableString(s1: String) {
    def implies(s2: Set[String]) = Implication(Set(s1), s2)

    def implies(s2: String): Implication = implies(Set(s2))

    def -->(s2: Set[String]) = implies(s2)

    def -->(s2: String) = implies(s2)
  }
}
