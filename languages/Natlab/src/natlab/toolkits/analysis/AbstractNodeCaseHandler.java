package natlab.toolkits.analysis;

import ast.*;

/** 
 * An abstract implementation of the NodeCaseHandler, which simply
 * forwards the case to the case higher on the node class hierarchy.
 */
public abstract class AbstractNodeCaseHandler implements NodeCaseHandler
{
    public abstract void caseASTNode(ASTNode node);

    public void caseList(List node)
    {
        caseASTNode(node);
    }
    public void caseProgram(Program node)
    {
        caseASTNode(node);
    }
    public void caseBody(Body node)
    {
        caseASTNode(node);
    }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node)
    {
        caseASTNode(node);
    }
    public void caseHelpComment(HelpComment node)
    {
        caseASTNode(node);
    }
    public void caseExpr(Expr node)
    {
        caseASTNode(node);
    }
    public void caseCompilationUnits(CompilationUnits node)
    {
        caseASTNode(node);
    }
    public void caseAttribute(Attribute node)
    {
        caseASTNode(node);
    }
    public void caseSuperClass(SuperClass node)
    {
        caseASTNode(node);
    }
    public void caseProperty(Property node)
    {
        caseASTNode(node);
    }
    public void caseEvent(Event node)
    {
        caseASTNode(node);
    }
    public void caseName(Name node)
    {
        caseASTNode(node);
    }
    public void caseSwitchCaseBlock(SwitchCaseBlock node)
    {
        caseASTNode(node);
    }
    public void caseDefaultCaseBlock(DefaultCaseBlock node)
    {
        caseASTNode(node);
    }
    public void caseIfBlock(IfBlock node)
    {
        caseASTNode(node);
    }
    public void caseElseBlock(ElseBlock node)
    {
        caseASTNode(node);
    }
    public void caseRow(Row node)
    {
        caseASTNode(node);
    }
    public void caseClassBody(ClassBody node)
    {
        caseBody(node);
    }
    public void caseStmt(Stmt node)
    {
        caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
    }
    public void caseLValueExpr(LValueExpr node)
    {
        caseExpr(node);
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        caseExpr(node);
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        caseExpr(node);
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        caseExpr(node);
    }
    public void caseScript(Script node)
    {
        caseProgram(node);
    }
    public void caseFunctionList(FunctionList node)
    {
        caseProgram(node);
    }
    public void caseEmptyProgram(EmptyProgram node)
    {
        caseProgram(node);
    }
    public void caseClassDef(ClassDef node)
    {
        caseProgram(node);
    }
    public void caseProperties(Properties node)
    {
        caseClassBody(node);
    }
    public void caseMethods(Methods node)
    {
        caseClassBody(node);
    }
    public void caseSignature(Signature node)
    {
        caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
    }
    public void casePropertyAccess(PropertyAccess node)
    {
        caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
    }
    public void caseClassEvents(ClassEvents node)
    {
        caseClassBody(node);
    }
    public void caseFunction(Function node)
    {
        caseFunctionOrSignatureOrPropertyAccessOrStmt(node);
    }
    public void caseOneLineHelpComment(OneLineHelpComment node)
    {
        caseHelpComment(node);
    }
    public void caseMultiLineHelpComment(MultiLineHelpComment node)
    {
        caseHelpComment(node);
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
    public void caseEmptyStmt(EmptyStmt node)
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
    public void caseAnnotation(Annotation node)
    {
        caseStmt(node);
    }
    public void caseExpandedAnnotation(ExpandedAnnotation node)
    {
        caseAnnotation(node);
    }
    public void caseVariableDecl(VariableDecl node)
    {
        caseStmt(node);
    }
    public void caseFunctionDecl(FunctionDecl node)
    {
        caseFunction(node);
    }
    public void caseType(Type node)
    {
        caseASTNode(node);
    }
    public void caseBaseType(BaseType node)
    {
        caseType(node);
    }
    public void casePrimitiveType(PrimitiveType node)
    {
        caseBaseType(node);
    }
    public void caseIntType(IntType node)
    {
        casePrimitiveType(node);
    }
    public void caseFloatType(FloatType node)
    {
        casePrimitiveType(node);
    }
    public void caseShape(Shape node)
    {
        caseASTNode(node);
    }
    public void caseRank(Rank node)
    {
        caseASTNode(node);
    }
    public void caseExtents(Extents node)
    {
        caseASTNode(node);
    }
    public void caseFieldEntry(FieldEntry node)
    {
        caseASTNode(node);
    }
    public void caseUnionType(UnionType node)
    {
        caseType(node);
    }
    public void caseIntersectionType(IntersectionType node)
    {
        caseType(node);
    }
    public void caseArrowType(ArrowType node)
    {
        caseType(node);
    }
    public void caseArgTupleType(ArgTupleType node)
    {
        caseType(node);
    }
    public void caseUnitType(UnitType node)
    {
        caseType(node);
    }
    public void caseVarArgType(VarArgType node)
    {
        caseType(node);
    }
    public void caseTupleType(TupleType node)
    {
        caseType(node);
    }
    public void caseCellArrayType(CellArrayType node)
    {
        caseType(node);
    }
    public void caseMatrixType(MatrixType node)
    {
        caseType(node);
    }
    public void caseTypeVarType(TypeVarType node)
    {
        caseBaseType(node);
    }
    public void caseHandleType(HandleType node)
    {
        caseBaseType(node);
    }
    public void caseUnknownType(UnknownType node)
    {
        caseBaseType(node);
    }
    public void caseStructType(StructType node)
    {
        caseBaseType(node);
    }
    public void caseInt8(Int8 node)
    {
        caseIntType(node);
    }
    public void caseInt16(Int16 node)
    {
        caseIntType(node);
    }
    public void caseInt32(Int32 node)
    {
        caseIntType(node);
    }
    public void caseInt64(Int64 node)
    {
        caseIntType(node);
    }
    public void caseFloat32(Float32 node)
    {
        caseFloatType(node);
    }
    public void caseFloat64(Float64 node)
    {
        caseFloatType(node);
    }
    public void caseCharType(CharType node)
    {
        casePrimitiveType(node);
    }
    public void caseBoolType(BoolType node)
    {
        casePrimitiveType(node);
    }
    public void caseKnownExtentsShape(KnownExtentsShape node)
    {
        caseShape(node);
    }
    public void caseKnownRank(KnownRank node)
    {
        caseRank(node);
    }
    public void caseKnownExtents(KnownExtents node)
    {
        caseExtents(node);
    }
}
