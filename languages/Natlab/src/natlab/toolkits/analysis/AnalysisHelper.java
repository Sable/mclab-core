package natlab.toolkits.analysis;

import ast.*;

/**
 * An analysis helper used to hide some of the details of the
 * analysis.  Specifically it hides the assignment of currentOutSet to
 * currentInSet. This guarantees that when a node is processed it will
 * have the out of the previous node as in. The helpee should still
 * make a copy of the in set. This of course only makes sense for
 * nodes with a single predecessor. For cases such as in for loops
 * where a loop variable might have multiple predecessor, the
 * containing node case, e.g. the for node, must deal with setting up
 * the correct currentInSet value.
 */
public class AnalysisHelper implements NodeCaseHandler //extends AbstractNodeCaseHandler
{

    /** 
     * The analysis being helped
     */
    private StructuralAnalysis helpee;

    /**
     * Class constructor with given helpee.
     *
     * @param helpee  the analysis being helped.
     */
    public AnalysisHelper( StructuralAnalysis helpee )
    {
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
    public void caseVariableDecl(VariableDecl node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseVariableDecl( node );
    }
    public void caseFunctionDecl(FunctionDecl node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFunctionDecl( node );
    }
    public void caseFunction(Function node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFunction( node );
    }
    public void caseType(Type node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseType( node );
    }
    public void caseUnionType(UnionType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUnionType( node );
    }
    public void caseIntersectionType(IntersectionType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseIntersectionType( node );
    }
    public void caseArrowType(ArrowType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseArrowType( node );
    }
    public void caseArgTupleType(ArgTupleType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseArgTupleType( node );
    }
    public void caseUnitType(UnitType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUnitType( node );
    }
    public void caseVarArgType(VarArgType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseVarArgType( node );
    }
    public void caseTupleType(TupleType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseTupleType( node );
    }
    public void caseCellArrayType(CellArrayType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseCellArrayType( node );
    }
    public void caseMatrixType(MatrixType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMatrixType( node );
    }
    public void caseBaseType(BaseType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseBaseType( node );
    }
    public void casePrimitiveType(PrimitiveType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.casePrimitiveType( node );
    }
    public void caseTypeVarType(TypeVarType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseTypeVarType( node );
    }
    public void caseHandleType(HandleType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseHandleType( node );
    }
    public void caseUnknownType(UnknownType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseUnknownType( node );
    }
    public void caseStructType(StructType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseStructType( node );
    }
    public void caseIntType(IntType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseIntType( node );
    }
    public void caseInt8(Int8 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseInt8( node );
    }
    public void caseInt16(Int16 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseInt16( node );
    }
    public void caseInt32(Int32 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseInt32( node );
    }
    public void caseInt64(Int64 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseInt64( node );
    }
    public void caseFloatType(FloatType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFloatType( node );
    }
    public void caseFloat32(Float32 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFloat32( node );
    }
    public void caseFloat64(Float64 node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFloat64( node );
    }
    public void caseCharType(CharType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseCharType( node );
    }
    public void caseBoolType(BoolType node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseBoolType( node );
    }
    public void caseFieldEntry(FieldEntry node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFieldEntry( node );
    }
    public void caseShape(Shape node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseShape( node );
    }
    public void caseKnownExtentsShape(KnownExtentsShape node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseKnownExtentsShape( node );
    }
    public void caseRank(Rank node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseRank( node );
    }
    public void caseKnownRank(KnownRank node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseKnownRank( node );
    }
    public void caseExtents(Extents node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseExtents( node );
    }
    public void caseKnownExtents(KnownExtents node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseKnownExtents( node );
    }
    public void caseExpandedAnnotation(ExpandedAnnotation node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseExpandedAnnotation( node );
    }
    public void caseAnnotation(Annotation node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseAnnotation( node );
    }
    public void caseEmptyStmt(EmptyStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEmptyStmt( node );
    }
    public void caseMultiLineHelpComment(MultiLineHelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMultiLineHelpComment( node );
    }
    public void caseOneLineHelpComment(OneLineHelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseOneLineHelpComment( node );
    }
    public void caseClassEvents(ClassEvents node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseClassEvents( node );
    }
    public void casePropertyAccess(PropertyAccess node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.casePropertyAccess( node );
    }
    public void caseSignature(Signature node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseSignature( node );
    }
    public void caseMethods(Methods node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseMethods( node );
    }
    public void caseProperties(Properties node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseProperties( node );
    }
    public void caseClassDef(ClassDef node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseClassDef( node );
    }
    public void caseEmptyProgram(EmptyProgram node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEmptyProgram( node );
    }
    public void caseClassBody(ClassBody node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseClassBody( node );
    }
    public void caseRow(Row node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseRow( node );
    }
    public void caseElseBlock(ElseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseElseBlock( node );
    }
    public void caseIfBlock(IfBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseIfBlock( node );
    }
    public void caseDefaultCaseBlock(DefaultCaseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseDefaultCaseBlock( node );
    }
    public void caseSwitchCaseBlock(SwitchCaseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseSwitchCaseBlock( node );
    }
    public void caseName(Name node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseName( node );
    }
    public void caseEvent(Event node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseEvent( node );
    }
    public void caseProperty(Property node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseProperty( node );
    }
    public void caseSuperClass(SuperClass node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseSuperClass( node );
    }
    public void caseAttribute(Attribute node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseAttribute( node );
    }
    public void caseCompilationUnits(CompilationUnits node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseCompilationUnits( node );
    }
    public void caseHelpComment(HelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseHelpComment( node );
    }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseFunctionOrSignatureOrPropertyAccessOrStmt( node );
    }
    public void caseBody(Body node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        helpee.caseBody( node );
    }
}