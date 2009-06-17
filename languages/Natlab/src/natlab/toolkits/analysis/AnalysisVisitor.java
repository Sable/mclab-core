
package natlab.toolkits.analysis;

import natlab.ast.*;

public abstract class AnalysisVisitor
{
    protected ASTNode tree;
    public AnalysisVisitor(ASTNode tree){
        this.tree = tree;
    }

    public abstract void caseASTNode(ASTNode node);
    public void caseProgram(Program node)
    {
        caseASTNode(node);
    }
    public void caseStmt(Stmt node)
    {
        caseASTNode(node);
    }
    public void caseBreakStmt(BreakStmt node)
    {
        caseStmt(node);
    }
    public void caseContinueStmt(ContinueStmt node)
    {
        caseStmt(node);
    }
    public void caseReturnStmt(ReturnStmt node)
    {
        caseStmt(node);
    }
    public void caseForStmt(ForStmt node)
    {
        caseStmt(node);
    }
    public void caseRangeForStmt(ForStmt node)
    {
        caseForStmt(node);
    }
    public void caseWhileStmt(WhileStmt node)
    {
        caseStmt(node);
    }
    public void caseTryStmt(TryStmt node)
    {
        caseStmt(node);
    }
    public void caseSwitchStmt(SwitchStmt node)
    {
        caseStmt(node);
    }
    public void caseIfStmt(IfStmt node)
    {
        caseStmt(node);
    }
    public void caseScript(Script node)
    {
        caseProgram(node);
    }
    public void caseFunctionList(FunctionList node)
    {
        caseProgram(node);
    }
    public void caseExprStmt(ExprStmt node)
    {
        caseStmt(node);
    }
    public void caseAssignStmt(AssignStmt node)
    {
        caseStmt(node);
    }
    public void caseGlobalStmt(GlobalStmt node)
    {
        caseStmt(node);
    }
    public void casePersistentStmt(PersistentStmt node)
    {
        caseStmt(node);
    }
    public void caseShellCommandStmt(ShellCommandStmt node)
    {
        caseStmt(node);
    }
    public void caseExpr(Expr node)
    {
        caseASTNode(node);
    }
    public void caseRangeExpr(RangeExpr node)
    {
        caseExpr(node);
    }
    public void caseColonExpr(ColonExpr node)
    {
        caseExpr(node);
    }
    public void caseEndExpr(EndExpr node)
    {
        caseExpr(node);
    }
    public void caseLValueExpr(LValueExpr node)
    {
        caseExpr(node);
    }
    public void caseNameExpr(NameExpr node)
    {
        caseLValueExpr(node);
    }
    public void caseParameterizedExpr(ParameterizedExpr node)
    {
        caseLValueExpr(node);
    }
    public void caseCellIndexExpr(CellIndexExpr node)
    {
        caseLValueExpr(node);
    }
    public void caseDotExpr(DotExpr node)
    {
        caseLValueExpr(node);
    }
    public void caseMatrixExpr(MatrixExpr node)
    {
        caseLValueExpr(node);
    }
    public void caseCellArrayExpr(CellArrayExpr node)
    {
        caseExpr(node);
    }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node)
    {
        caseExpr(node);
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        caseExpr(node);
    }
    public void caseIntLiteralExpr(IntLiteralExpr node)
    {
        caseLiteralExpr(node);
    }
    public void caseFPLiteralExpr(FPLiteralExpr node)
    {
        caseLiteralExpr(node);
    }
    public void caseStringLiteralExpr(StringLiteralExpr node)
    {
        caseLiteralExpr(node);
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        caseExpr(node);
    }
    public void caseUMinusExpr(UMinusExpr node)
    {
        caseUnaryExpr(node);
    }
    public void caseUPlusExpr(UPlusExpr node)
    {
        caseUnaryExpr(node);
    }
    public void caseNotExpr(NotExpr node)
    {
        caseUnaryExpr(node);
    }
    public void caseMTransposeExpr(MTransposeExpr node)
    {
        caseUnaryExpr(node);
    }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node)
    {
        caseUnaryExpr(node);
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        caseExpr(node);
    }
    public void casePlusExpr(PlusExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseMinusExpr(MinusExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseMTimesExpr(MTimesExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseMDivExpr(MDivExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseMLDivExpr(MLDivExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseMPowExpr(MPowExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseETimesExpr(ETimesExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseEDivExpr(EDivExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseELDivExpr(ELDivExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseEPowExpr(EPowExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseAndExpr(AndExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseOrExpr(OrExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseLTExpr(LTExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseGTExpr(GTExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseLEExpr(LEExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseGEExpr(GEExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseEQExpr(EQExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseNEExpr(NEExpr node)
    {
        caseBinaryExpr(node);
    }
    public void caseFunctionHandleExpr(FunctionHandleExpr node)
    {
        caseExpr(node);
    }
    public void caseLambdaExpr(LambdaExpr node)
    {
        caseExpr(node);
    }
}
