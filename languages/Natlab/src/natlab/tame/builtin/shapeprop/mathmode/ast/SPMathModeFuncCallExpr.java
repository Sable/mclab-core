package natlab.tame.builtin.shapeprop.mathmode.ast;

public class SPMathModeFuncCallExpr extends SPMathModeAbstractExpr{

    String funcName;


    SPMathModeArglist arguments;

    public SPMathModeFuncCallExpr(String funcName, SPMathModeArglist arguments) {
        this.funcName = funcName;
        this.arguments = arguments;
    }

    @Override
    public <V> V apply(AbstractMathModeVisitor<V> visitor) {
        return visitor.visitFuncCall(this);
    }

    @Override
    public String toString() {
        return this.funcName+"("+  arguments.toString() +")";
    }


}
