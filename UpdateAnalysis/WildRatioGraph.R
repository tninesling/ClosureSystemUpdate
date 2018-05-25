attach(df)

wildWins <- function(numBroken) {
  sum((wild < modified)[broken == numBroken])
}

wildWinsRatio <- function(numBroken) {
  wildWins(numBroken) / sum(broken == numBroken)
}

# Graph ratio by number of broken implications

x <- min(broken) : max(broken)
plot(x, Map(wildWins, x))