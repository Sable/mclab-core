package natlab.tame.builtin.shapeprop.mathmode.ast;

public class SPMathModeLowercaseExpr extends SPMathModeAbstractExpr{

    String lowercase;

    public SPMathModeLowercaseExpr(String lowercase) {
        this.lowercase = lowercase;
    }

    @Override
    public <V> V apply(AbstractMathModeVisitor<V> visitor) {
        return visitor.visitLowerCase(this);
    }

    @Override
    public String toString() {
        return lowercase;
    }
}
