package natlab.tame.builtin.shapeprop.mathmode.ast;

public class SPMathModeBinOpExpr extends SPMathModeAbstractExpr{

    BinOp op;
    SPMathModeAbstractExpr left;
    SPMathModeAbstractExpr right;

    public SPMathModeBinOpExpr(BinOp op, SPMathModeAbstractExpr left, SPMathModeAbstractExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }


    @Override
    public <V> V apply(AbstractMathModeVisitor<V> visitor) {
        return visitor.visitBinOp(this);
    }

    @Override
    public String toString() {
        return "("+left.toString() + op.toString()+right.toString() +")";
    }
}
