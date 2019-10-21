package natlab.tame.builtin.shapeprop.mathmode.ast;

public class SPMathModeNumberExpr extends SPMathModeAbstractExpr{
    Number number;

    public SPMathModeNumberExpr(Number number) {
        this.number = number;
    }

    @Override
    public <V> V apply(AbstractMathModeVisitor<V> visitor) {
        return visitor.visitNumber(this);
    }

    @Override
    public String toString() {
        return number.intValue()+"";
    }
}
