object MathTermizer {

    data class Term(val tokens: ArrayList<MathLexer.Token>, val level: Int) : Comparable<Term> {
        override fun compareTo(other: Term): Int {
            if(this.level > other.level){ return 1 }
            if(this.level < other.level){ return -1 }
            return 0
        }

        override fun toString(): String {
            var term = ""
            tokens.forEach { term += it.text }
            return "$term [$level]"
        }

        fun replace(key: ArrayList<MathLexer.Token>, value: ArrayList<MathLexer.Token>) {
            var keyPos = 0
            var startPos = -1
            for(index in 0 until tokens.size){
                val token = tokens[index]
                if(keyPos == key.lastIndex){
                    break
                }
                if(keyPos == 0){
                    startPos = index
                }
                if(token == key[keyPos]){
                    keyPos++
                }else{
                    keyPos = 0
                    startPos = -1
                }
            }
            //println(MathLexer.tokensToString(this.tokens))
            //val fake = this.tokens.clone() as ArrayList<MathLexer.Token>
            if(startPos > -1){
                for(i in 0 until key.size){
                    this.tokens.removeAt(startPos)
                }
                this.tokens.addAll(startPos, value)
            }
            //println(MathLexer.tokensToString(this.tokens))
            //println("${MathLexer.tokensToString(this.tokens)} contains ${MathLexer.tokensToString(key)}: ${this.tokens.containsAll(key)}")
        }
    }

    fun parseTerms(tokens: ArrayList<MathLexer.Token>): ArrayList<Term> {
        val terms = ArrayList<Term>()

        val rootTerm = Term(tokens, 0)
        val subTerms = arrayListOf(rootTerm)

        while(subTerms.size > 0) {
            val tmpSubTerms = ArrayList<Term>()
            subTerms.forEach { tmpSubTerms.addAll(getSubTerms(it)) }
            subTerms.clear()
            subTerms.addAll(tmpSubTerms)
            terms.addAll(tmpSubTerms)
        }

        terms.sortByDescending { it.level }
        return terms
    }

    fun getSubTerms(term: Term): Array<Term> {
        val terms = ArrayList<Term>()

        var hasSub = false
        term.tokens.forEach { if(it.text == "("){ hasSub = true } }
        if(!hasSub) {return terms.toTypedArray()}

        var openBrackets = term.level
        var expectedCloseLv = -1
        var capture = false
        val captured = ArrayList<MathLexer.Token>()
        term.tokens.forEach {
            if(it.text == "("){
                openBrackets++
                if(expectedCloseLv == -1) {
                    expectedCloseLv = openBrackets
                    capture = true
                }
            }

            if(capture){
                captured.add(it)
            }

            if(it.text == ")"){
                if(openBrackets == expectedCloseLv){
                    capture = false
                    expectedCloseLv = -1
                    captured.removeAt(captured.lastIndex)
                    captured.removeAt(0)
                    terms.add(Term(captured.clone() as ArrayList<MathLexer.Token>, term.level + 1))
                    captured.clear()
                }
                openBrackets--
            }
        }

        return terms.toTypedArray()
    }

}
