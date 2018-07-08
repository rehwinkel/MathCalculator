object MathTermSolver {
    fun solveTerms(terms: ArrayList<MathTermizer.Term>): MathTermizer.Term {
        val solutions = HashMap<MathTermizer.Term, MathTermizer.Term>()
        for(i in 0 until terms.size){
            //println(terms[i])
            var processed = applySolutions(solutions.toList().sortedByDescending(Pair<MathTermizer.Term, MathTermizer.Term>::first).toMap(), terms[i])
            //println(processed)
            processed = processTerm(processed)
            //println(processed)
            //println()
            if(terms[i] != processed){
                solutions.put(terms[i], processed)
            }
            if(i == terms.lastIndex){
                return processed
            }
        }
        return MathTermizer.Term(arrayListOf(MathLexer.Token(MathLexer.EnumType.TMP, "ERROR")), -1)
    }

    private fun applySolutions(solutions: Map<MathTermizer.Term, MathTermizer.Term>, term: MathTermizer.Term): MathTermizer.Term {
        for(key in solutions.keys){
            val k2 = key.tokens.clone() as ArrayList<MathLexer.Token>
            k2.add(0, MathLexer.Token(MathLexer.EnumType.PT, "("))
            k2.add(MathLexer.Token(MathLexer.EnumType.PT, ")"))
            term.replace(k2, solutions[key]!!.tokens)
        }
        return term
    }

    private fun processTerm(term: MathTermizer.Term): MathTermizer.Term {
        var currentTerm = term
        val tokenStrings = term.tokens.map { it.text }
        if(!(tokenStrings.contains("(") || tokenStrings.contains(")"))) {
            currentTerm = calcOp(currentTerm, "/")
            currentTerm = calcOp(currentTerm, "*")
            currentTerm = calcOp(currentTerm, "-")
            currentTerm = calcOp(currentTerm, "+")
            //currentTerm = calcPlus(currentTerm,"+")
            return currentTerm
        }
        return term
    }

    /*private fun calcOp(inTerm: MathTermizer.Term, op: String): MathTermizer.Term {
        val tokens = inTerm.tokens
        val tmpTokens = ArrayList<MathLexer.Token>()
        var doThisOne = true
        for(i in 0 until tokens.size){
            val pop = tokens.get(i)
            if(pop.type == MathLexer.EnumType.OP && (pop.text == "*" || pop.text == "/" || pop.text == "+" || pop.text == "-") && pop.text == op){
                val a = tokens.get(i-1)
                val b = tokens.get(i+1)
                tmpTokens.removeAt(tmpTokens.lastIndex)
                if(op == "*"){
                    tmpTokens.add(MathLexer.Token(MathLexer.EnumType.NUM, (a.text.toDouble() * b.text.toDouble()).toString()))
                }else if(op == "/"){
                    tmpTokens.add(MathLexer.Token(MathLexer.EnumType.NUM, (a.text.toDouble() / b.text.toDouble()).toString()))
                }else if(op == "+"){
                    tmpTokens.add(MathLexer.Token(MathLexer.EnumType.NUM, (a.text.toDouble() + b.text.toDouble()).toString()))
                }else if(op == "-"){
                    tmpTokens.add(MathLexer.Token(MathLexer.EnumType.NUM, (a.text.toDouble() - b.text.toDouble()).toString()))
                }else{
                    if(doThisOne){
                        tmpTokens.add(tokens.get(i))
                    }
                }
                doThisOne = false
                continue
            }else{
                if(doThisOne){
                    tmpTokens.add(tokens.get(i))
                }
            }
            doThisOne = true
            println(tmpTokens)
        }
        return MathTermizer.Term(tmpTokens, inTerm.level)
    }*/

    private fun calcOp(inTerm: MathTermizer.Term, op: String): MathTermizer.Term {
        val myTokens = inTerm.tokens.clone() as ArrayList<MathLexer.Token>

        while(true){
            for(i in 0 until myTokens.size){
                val token = myTokens[i]

                if(token.text == op){
                    val a = myTokens.get(i-1).text.toDouble()
                    val b = myTokens.get(i+1).text.toDouble()
                    if(op == "+"){
                        myTokens[i] = MathLexer.Token(MathLexer.EnumType.NUM, (a + b).toString())
                    }else if(op == "-"){
                        myTokens[i] = MathLexer.Token(MathLexer.EnumType.NUM, (a - b).toString())
                    }else if(op == "*"){
                        myTokens[i] = MathLexer.Token(MathLexer.EnumType.NUM, (a * b).toString())
                    }else if(op == "/"){
                        myTokens[i] = MathLexer.Token(MathLexer.EnumType.NUM, (a / b).toString())
                    }
                    myTokens.removeAt(i+1)
                    myTokens.removeAt(i-1)
                    break
                }
            }
            if(!myTokens.map { it.text }.contains(op)){
                break
            }
        }

        return MathTermizer.Term(myTokens, inTerm.level)
    }

}
