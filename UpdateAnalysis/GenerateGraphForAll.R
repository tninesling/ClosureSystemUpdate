attach(df)

# Generate graph for all three
plot(broken, naive, main = "Basis Update Comparison", xlab = "Number of Broken Implications", ylab = "Time to complete update (ms)", pch = 21)
points(broken, wild, pch = 22, col = "blue")
points(broken, modified, pch = 23, col = "green")
legend(x = max(broken) - 30, y = max(naive, wild, modified), legend = c("naive", "wild", "modified"), pch = c(21, 22, 23), col = c("black", "blue", "green"))