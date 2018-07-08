fun main(args: Array<String>) {
    val input = "3 + 4 * (4 / 49-.4-(77.6+55   * 5) + 3 * (4 - 1) + 3) + 4 + 2"
    val tokens = MathLexer.parseTokens("$input ")
    //println(MathLexer.tokensToString(tokens))
    val terms = MathTermizer.parseTerms(tokens)
    val solution = MathTermSolver.solveTerms(terms)
    println(solution)
    println(3 + 4 * (4 / 49-.4-(77.6+55   * 5) + 3 * (4 - 1) + 3) + 4 + 2)
}