package natlab.tame.builtin.shapeprop.mathmode.ast;

public interface AbstractMathModeVisitor<V> {

    V visitBinOp(SPMathModeBinOpExpr binOp);

    V visitUnOp(SPMathModeUnOpExpr unOp);

    V visitLowerCase(SPMathModeLowercaseExpr lowerCaseExpr);
    V visitNumber(SPMathModeNumberExpr numberExpr);

    V visitFuncCall(SPMathModeFuncCallExpr funcCallExpr);
}
