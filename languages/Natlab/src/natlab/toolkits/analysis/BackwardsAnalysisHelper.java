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
public class BackwardsAnalysisHelper extends AnalysisHelper
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
    public BackwardsAnalysisHelper( StructuralAnalysis helpee )
    {
        super( helpee );
    }

    public void caseASTNode(ASTNode node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseASTNode( node );
    }
    public void caseList( List node )
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseList( node );
    }
    public void caseProgram(Program node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseProgram( node );
    }
    public void caseStmt(Stmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseStmt( node );
    }
    public void caseBreakStmt(BreakStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseBreakStmt( node );
    }
    public void caseContinueStmt(ContinueStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseContinueStmt( node );
    }
    public void caseReturnStmt(ReturnStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseReturnStmt( node );
    }
    public void caseForStmt(ForStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseForStmt( node );
    }
    public void caseRangeForStmt(ForStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseRangeForStmt( node );
    }
    public void caseWhileStmt(WhileStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseWhileStmt( node );
    }
    public void caseTryStmt(TryStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseTryStmt( node );
    }
    public void caseSwitchStmt(SwitchStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseSwitchStmt( node );
    }
    public void caseIfStmt(IfStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseIfStmt( node );
    }
    public void caseScript(Script node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseScript( node );
    }
    public void caseFunctionList(FunctionList node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFunctionList( node );
    }
    public void caseExprStmt(ExprStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseExprStmt( node );
    }
    public void caseAssignStmt(AssignStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseAssignStmt( node );
    }
    public void caseGlobalStmt(GlobalStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseGlobalStmt( node );
    }
    public void casePersistentStmt(PersistentStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.casePersistentStmt( node );
    }
    public void caseShellCommandStmt(ShellCommandStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseShellCommandStmt( node );
    }
    public void caseExpr(Expr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseExpr( node );
    }
    public void caseRangeExpr(RangeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseRangeExpr( node );
    }
    public void caseColonExpr(ColonExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseColonExpr( node );
    }
    public void caseEndExpr(EndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEndExpr( node );
    }
    public void caseLValueExpr(LValueExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseLValueExpr( node );
    }
    public void caseNameExpr(NameExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseNameExpr( node );
    }
    public void caseParameterizedExpr(ParameterizedExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseParameterizedExpr( node );
    }
    public void caseCellIndexExpr(CellIndexExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseCellIndexExpr( node );
    }
    public void caseDotExpr(DotExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseDotExpr( node );
    }
    public void caseMatrixExpr(MatrixExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMatrixExpr( node );
    }
    public void caseCellArrayExpr(CellArrayExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseCellArrayExpr( node );
    }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseSuperClassMethodExpr( node );
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseLiteralExpr( node );
    }
    public void caseIntLiteralExpr(IntLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseIntLiteralExpr( node );
    }
    public void caseFPLiteralExpr(FPLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFPLiteralExpr( node );
    }
    public void caseStringLiteralExpr(StringLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseStringLiteralExpr( node );
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUnaryExpr( node );
    }
    public void caseUMinusExpr(UMinusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUMinusExpr( node );
    }
    public void caseUPlusExpr(UPlusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUPlusExpr( node );
    }
    public void caseNotExpr(NotExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseNotExpr( node );
    }
    public void caseMTransposeExpr(MTransposeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMTransposeExpr( node );
    }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseArrayTransposeExpr( node );
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseBinaryExpr( node );
    }
    public void casePlusExpr(PlusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.casePlusExpr( node );
    }
    public void caseMinusExpr(MinusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMinusExpr( node );
    }
    public void caseMTimesExpr(MTimesExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMTimesExpr( node );
    }
    public void caseMDivExpr(MDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMDivExpr( node );
    }
    public void caseMLDivExpr(MLDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMLDivExpr( node );
    }
    public void caseMPowExpr(MPowExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMPowExpr( node );
    }
    public void caseETimesExpr(ETimesExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseETimesExpr( node );
    }
    public void caseEDivExpr(EDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEDivExpr( node );
    }
    public void caseELDivExpr(ELDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseELDivExpr( node );
    }
    public void caseEPowExpr(EPowExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEPowExpr( node );
    }
    public void caseAndExpr(AndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseAndExpr( node );
    }
    public void caseOrExpr(OrExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseOrExpr( node );
    }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseShortCircuitAndExpr( node );
    }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseShortCircuitOrExpr( node );
    }
    public void caseLTExpr(LTExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseLTExpr( node );
    }
    public void caseGTExpr(GTExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseGTExpr( node );
    }
    public void caseLEExpr(LEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseLEExpr( node );
    }
    public void caseGEExpr(GEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseGEExpr( node );
    }
    public void caseEQExpr(EQExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEQExpr( node );
    }
    public void caseNEExpr(NEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseNEExpr( node );
    }
    public void caseFunctionHandleExpr(FunctionHandleExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFunctionHandleExpr( node );
    }
    public void caseLambdaExpr(LambdaExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseLambdaExpr( node );
    }
    public void caseVariableDecl(VariableDecl node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseVariableDecl( node );
    }
    public void caseFunctionDecl(FunctionDecl node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFunctionDecl( node );
    }
    public void caseFunction(Function node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFunction( node );
    }
    public void caseType(Type node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseType( node );
    }
    public void caseUnionType(UnionType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUnionType( node );
    }
    public void caseIntersectionType(IntersectionType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseIntersectionType( node );
    }
    public void caseArrowType(ArrowType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseArrowType( node );
    }
    public void caseArgTupleType(ArgTupleType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseArgTupleType( node );
    }
    public void caseUnitType(UnitType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUnitType( node );
    }
    public void caseVarArgType(VarArgType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseVarArgType( node );
    }
    public void caseTupleType(TupleType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseTupleType( node );
    }
    public void caseCellArrayType(CellArrayType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseCellArrayType( node );
    }
    public void caseMatrixType(MatrixType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMatrixType( node );
    }
    public void caseBaseType(BaseType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseBaseType( node );
    }
    public void casePrimitiveType(PrimitiveType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.casePrimitiveType( node );
    }
    public void caseTypeVarType(TypeVarType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseTypeVarType( node );
    }
    public void caseHandleType(HandleType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseHandleType( node );
    }
    public void caseUnknownType(UnknownType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseUnknownType( node );
    }
    public void caseStructType(StructType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseStructType( node );
    }
    public void caseIntType(IntType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseIntType( node );
    }
    public void caseInt8(Int8 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseInt8( node );
    }
    public void caseInt16(Int16 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseInt16( node );
    }
    public void caseInt32(Int32 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseInt32( node );
    }
    public void caseInt64(Int64 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseInt64( node );
    }
    public void caseFloatType(FloatType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFloatType( node );
    }
    public void caseFloat32(Float32 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFloat32( node );
    }
    public void caseFloat64(Float64 node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFloat64( node );
    }
    public void caseCharType(CharType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseCharType( node );
    }
    public void caseBoolType(BoolType node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseBoolType( node );
    }
    public void caseFieldEntry(FieldEntry node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFieldEntry( node );
    }
    public void caseShape(Shape node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseShape( node );
    }
    public void caseKnownExtentsShape(KnownExtentsShape node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseKnownExtentsShape( node );
    }
    public void caseRank(Rank node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseRank( node );
    }
    public void caseKnownRank(KnownRank node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseKnownRank( node );
    }
    public void caseExtents(Extents node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseExtents( node );
    }
    public void caseKnownExtents(KnownExtents node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseKnownExtents( node );
    }
    public void caseExpandedAnnotation(ExpandedAnnotation node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseExpandedAnnotation( node );
    }
    public void caseAnnotation(Annotation node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseAnnotation( node );
    }
    public void caseEmptyStmt(EmptyStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEmptyStmt( node );
    }
    public void caseMultiLineHelpComment(MultiLineHelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMultiLineHelpComment( node );
    }
    public void caseOneLineHelpComment(OneLineHelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseOneLineHelpComment( node );
    }
    public void caseClassEvents(ClassEvents node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseClassEvents( node );
    }
    public void casePropertyAccess(PropertyAccess node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.casePropertyAccess( node );
    }
    public void caseSignature(Signature node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseSignature( node );
    }
    public void caseMethods(Methods node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseMethods( node );
    }
    public void caseProperties(Properties node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseProperties( node );
    }
    public void caseClassDef(ClassDef node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseClassDef( node );
    }
    public void caseEmptyProgram(EmptyProgram node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEmptyProgram( node );
    }
    public void caseClassBody(ClassBody node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseClassBody( node );
    }
    public void caseRow(Row node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseRow( node );
    }
    public void caseElseBlock(ElseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseElseBlock( node );
    }
    public void caseIfBlock(IfBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseIfBlock( node );
    }
    public void caseDefaultCaseBlock(DefaultCaseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseDefaultCaseBlock( node );
    }
    public void caseSwitchCaseBlock(SwitchCaseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseSwitchCaseBlock( node );
    }
    public void caseName(Name node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseName( node );
    }
    public void caseEvent(Event node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseEvent( node );
    }
    public void caseProperty(Property node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseProperty( node );
    }
    public void caseSuperClass(SuperClass node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseSuperClass( node );
    }
    public void caseAttribute(Attribute node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseAttribute( node );
    }
    public void caseCompilationUnits(CompilationUnits node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseCompilationUnits( node );
    }
    public void caseHelpComment(HelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseHelpComment( node );
    }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseFunctionOrSignatureOrPropertyAccessOrStmt( node );
    }
    public void caseBody(Body node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        helpee.caseBody( node );
    }
}