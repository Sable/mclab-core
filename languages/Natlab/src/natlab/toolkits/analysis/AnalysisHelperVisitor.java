package natlab.toolkits.analysis;

import natlab.ast.*;

/**
 * An analysis helper used to hide some of the details of the
 * analysis.  Specifically it hides the copying of currentOutSet to
 * currentInSet. This guarantees that when a node is processed it will
 * have the out of the previous node as in. The helpee should still
 * make a copy of the in set. This of course only makes sense for
 * nodes with a single predecessor. For cases such as in for loops
 * where a loop variable might have multiple predecessor, the
 * containing node case, e.g. the for node, must deal with setting up
 * the correct currentInSet value.
 */
public class AnalysisHelperVisitor extends AnalysisVisitor
{

    /** 
     * The analysis being helped
     */
    private AbstractStructuralAnalysis helpee;

    /**
     * Class contstructor
     *
     * @param helpee  the analysis being helped
     * @param tree    the root of the tree being analyzed
     */
    public AnalysisHelperVisitor( AbstractStructuralAnalysis helpee, ASTNode tree )
    {
        super( tree );
        this.helpee = helpee;
    }

    public void caseASTNode(ASTNode node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseASTNode( node );
    }
    public void caseProgram(Program node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseProgram( node );
    }
    public void caseStmt(Stmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseStmt( node );
    }
    public void caseBreakStmt(BreakStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseBreakStmt( node );
    }
    public void caseContinueStmt(ContinueStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseContinueStmt( node );
    }
    public void caseReturnStmt(ReturnStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseReturnStmt( node );
    }
    public void caseForStmt(ForStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseForStmt( node );
    }
    public void caseRangeForStmt(ForStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseRangeForStmt( node );
    }
    public void caseWhileStmt(WhileStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseWhileStmt( node );
    }
    public void caseTryStmt(TryStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseTryStmt( node );
    }
    public void caseSwitchStmt(SwitchStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseSwitchStmt( node );
    }
    public void caseIfStmt(IfStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseIfStmt( node );
    }
    public void caseScript(Script node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseScript( node );
    }
    public void caseFunctionList(FunctionList node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFunctionList( node );
    }
    public void caseExprStmt(ExprStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseExprStmt( node );
    }
    public void caseAssignStmt(AssignStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseAssignStmt( node );
    }
    public void caseGlobalStmt(GlobalStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseGlobalStmt( node );
    }
    public void casePersistentStmt(PersistentStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.casePersistentStmt( node );
    }
    public void caseShellCommandStmt(ShellCommandStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseShellCommandStmt( node );
    }
    public void caseExpr(Expr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseExpr( node );
    }
    public void caseRangeExpr(RangeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseRangeExpr( node );
    }
    public void caseColonExpr(ColonExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseColonExpr( node );
    }
    public void caseEndExpr(EndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEndExpr( node );
    }
    public void caseLValueExpr(LValueExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseLValueExpr( node );
    }
    public void caseNameExpr(NameExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseNameExpr( node );
    }
    public void caseParameterizedExpr(ParameterizedExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseParameterizedExpr( node );
    }
    public void caseCellIndexExpr(CellIndexExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseCellIndexExpr( node );
    }
    public void caseDotExpr(DotExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseDotExpr( node );
    }
    public void caseMatrixExpr(MatrixExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMatrixExpr( node );
    }
    public void caseCellArrayExpr(CellArrayExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseCellArrayExpr( node );
    }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseSuperClassMethodExpr( node );
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseLiteralExpr( node );
    }
    public void caseIntLiteralExpr(IntLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseIntLiteralExpr( node );
    }
    public void caseFPLiteralExpr(FPLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFPLiteralExpr( node );
    }
    public void caseStringLiteralExpr(StringLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseStringLiteralExpr( node );
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUnaryExpr( node );
    }
    public void caseUMinusExpr(UMinusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUMinusExpr( node );
    }
    public void caseUPlusExpr(UPlusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUPlusExpr( node );
    }
    public void caseNotExpr(NotExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseNotExpr( node );
    }
    public void caseMTransposeExpr(MTransposeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMTransposeExpr( node );
    }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseArrayTransposeExpr( node );
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseBinaryExpr( node );
    }
    public void casePlusExpr(PlusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.casePlusExpr( node );
    }
    public void caseMinusExpr(MinusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMinusExpr( node );
    }
    public void caseMTimesExpr(MTimesExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMTimesExpr( node );
    }
    public void caseMDivExpr(MDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMDivExpr( node );
    }
    public void caseMLDivExpr(MLDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMLDivExpr( node );
    }
    public void caseMPowExpr(MPowExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMPowExpr( node );
    }
    public void caseETimesExpr(ETimesExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseETimesExpr( node );
    }
    public void caseEDivExpr(EDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEDivExpr( node );
    }
    public void caseELDivExpr(ELDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseELDivExpr( node );
    }
    public void caseEPowExpr(EPowExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEPowExpr( node );
    }
    public void caseAndExpr(AndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseAndExpr( node );
    }
    public void caseOrExpr(OrExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseOrExpr( node );
    }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseShortCircuitAndExpr( node );
    }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseShortCircuitOrExpr( node );
    }
    public void caseLTExpr(LTExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseLTExpr( node );
    }
    public void caseGTExpr(GTExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseGTExpr( node );
    }
    public void caseLEExpr(LEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseLEExpr( node );
    }
    public void caseGEExpr(GEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseGEExpr( node );
    }
    public void caseEQExpr(EQExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEQExpr( node );
    }
    public void caseNEExpr(NEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseNEExpr( node );
    }
    public void caseFunctionHandleExpr(FunctionHandleExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFunctionHandleExpr( node );
    }
    public void caseLambdaExpr(LambdaExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseLambdaExpr( node );
    }
}