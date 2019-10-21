package natlab.tame.builtin.shapeprop.mathmode.ast;

public enum BinOp {
    POW("^"),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIV("/"),
    LT("<"),
    GT(">"),
    LE("<="),
    GE(">="),
    EQ("=="),
    NE("<="),
    AND("&&");
    private String text = null;

    BinOp(String s) {
        this.text = s;
    }

    @Override
    public String toString() {
        return text;
    }
}
