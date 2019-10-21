package natlab.tame.builtin.shapeprop.mathmode.ast;

public class SPMathModeArglist extends SPMathModeNode {

    SPMathModeAbstractExpr expr;
    SPMathModeArglist next;

    public SPMathModeArglist(SPMathModeAbstractExpr expr, SPMathModeArglist next) {
        this.expr = expr;
        this.next = next;
    }

    @Override
    public String toString() {
        SPMathModeArglist next = this;
        StringBuilder str = new StringBuilder();
        while (next!=null){
            str.append(next.expr.toString());
            str.append((next.next!=null)?", ":"");
            next = next.next;
        }
        return str.toString();
    }
}
