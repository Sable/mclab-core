package natlab.tame.builtin.shapeprop.mathmode.ast;

import natlab.tame.builtin.shapeprop.ast.SPAbstractMatchExpr;

public class SPMathModeUnOpExpr extends SPMathModeAbstractExpr{
    SPMathModeAbstractExpr expr;

    public SPMathModeUnOpExpr(SPMathModeAbstractExpr expr) {
        this.expr = expr;
    }

    @Override
    public <V> V apply(AbstractMathModeVisitor<V> visitor) {
        return visitor.visitUnOp(this);
    }

    @Override
    public String toString() {
        return "-("+expr.toString()+")";
    }
}

