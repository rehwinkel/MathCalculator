object MathLexer {

    data class Token(val type: EnumType, val text: String)
    enum class EnumType {
        PT,
        TMP,
        NUM,
        NUL,
        OP;
    }

    fun tokensToString(tokens: Collection<Token>): String {
        var text = ""
        tokens.forEach { text += it.text }
        return text
    }

    fun parseTokens(text: String): ArrayList<Token> {
        val punctuators = "+-*/ ()"
        var tokens = ArrayList<Token>()

        var currentTk = ""
        text.toCharArray().forEach {
            if(punctuators.contains(it)){
                if(currentTk.isNotEmpty()) {
                    tokens.add(Token(EnumType.TMP, currentTk))
                    currentTk = ""
                }
                if(it != ' '){
                    tokens.add(Token(EnumType.PT, it.toString()))
                }
            }else{
                currentTk += it
            }
        }

        tokens = makeNumbers(tokens)
        tokens.add(0, Token(EnumType.PT, "("))
        tokens.add(Token(EnumType.PT, ")"))
        tokens.forEach { if(it.type == EnumType.TMP){ throw RuntimeException("Syntax error in input Term!") } }

        tokens = makeOperators(tokens)

        return tokens
    }

    private fun makeOperators(input: ArrayList<Token>): ArrayList<Token> {
        val tokens = ArrayList<Token>()

        var prevToken: Token? = null
        input.forEach {
            if(it.type == EnumType.PT){
                if(it.text == "+" || it.text == "-" || it.text == "/"){
                    tokens.add(Token(EnumType.OP, it.text))
                }else if(it.text == "*"){
                    if(prevToken != null && prevToken!!.text == "*"){
                        tokens.removeAt(tokens.lastIndex)
                        tokens.add(Token(EnumType.OP, "**"))
                    }else{
                        tokens.add(Token(EnumType.OP, "*"))
                    }
                }else{
                    tokens.add(it)
                }
            }else{
                tokens.add(it)
            }
            prevToken = it
        }

        return tokens
    }

    private fun makeNumbers(input: ArrayList<Token>): ArrayList<Token> {
        val tokens = ArrayList<Token>()
        val headingZeros = Regex("^0+(?=[1-9]+)")
        val tailingZeros = Regex("(?<=\\.\\d{1,100})0+\$")
        val isNumber = Regex("(\\d+\\.\\d+)|(\\d+)")

        for(i in input){
            if(i.type == EnumType.TMP){
                var t = i.text
                if(t.startsWith(".") && t.length > 1){
                    t = "0$t"
                }
                if(t.endsWith(".") && t.length > 1){
                    t = "${t}0"
                }
                t = headingZeros.replace(t, "")
                t = tailingZeros.replace(t, "")
                if(isNumber.matches(t)){
                    tokens.add(Token(EnumType.NUM, t))
                }else{
                    tokens.add(i)
                }
            }else{
                tokens.add(i)
            }
        }

        return tokens
    }

}