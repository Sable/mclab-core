package natlab.toolkits.analysis;
/**
 * a node case handler which simply forwards a case call to the
 * same case call of another NodeCaseHandler
 */
import ast.*;
public class ForwardingNodeCaseHandler implements NodeCaseHandler {
    protected NodeCaseHandler callback;
    public ForwardingNodeCaseHandler (NodeCaseHandler callback){
        this.callback = callback;
    }
    
    public void caseASTNode(ASTNode node) { callback.caseASTNode(node); }
    public void caseAndExpr(AndExpr node) { callback.caseAndExpr(node); }
    public void caseAnnotation(Annotation node) { callback.caseAnnotation(node); }
    public void caseArgTupleType(ArgTupleType node) { callback.caseArgTupleType(node); }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node) { callback.caseArrayTransposeExpr(node); }
    public void caseArrowType(ArrowType node) { callback.caseArrowType(node); }
    public void caseAssignStmt(AssignStmt node) { callback.caseAssignStmt(node); }
    public void caseAttribute(Attribute node) { callback.caseAttribute(node); }
    public void caseBaseType(BaseType node) { callback.caseBaseType(node); }
    public void caseBinaryExpr(BinaryExpr node) { callback.caseBinaryExpr(node); }
    public void caseBody(Body node) { callback.caseBody(node); }
    public void caseBoolType(BoolType node) { callback.caseBoolType(node); }
    public void caseBreakStmt(BreakStmt node) { callback.caseBreakStmt(node); }
    public void caseCSLExpr(CSLExpr node) { callback.caseCSLExpr(node); }
    public void caseCellArrayExpr(CellArrayExpr node) { callback.caseCellArrayExpr(node); }
    public void caseCellArrayType(CellArrayType node) { callback.caseCellArrayType(node); }
    public void caseCellIndexExpr(CellIndexExpr node) { callback.caseCellIndexExpr(node); }
    public void caseCharType(CharType node) { callback.caseCharType(node); }
    public void caseCheckScalarStmt(CheckScalarStmt node) { callback.caseCheckScalarStmt(node); }
    public void caseClassBody(ClassBody node) { callback.caseClassBody(node); }
    public void caseClassDef(ClassDef node) { callback.caseClassDef(node); }
    public void caseClassEvents(ClassEvents node) { callback.caseClassEvents(node); }
    public void caseColonExpr(ColonExpr node) { callback.caseColonExpr(node); }
    public void caseCompilationUnits(CompilationUnits node) { callback.caseCompilationUnits(node); }
    public void caseContinueStmt(ContinueStmt node) { callback.caseContinueStmt(node); }
    public void caseDefaultCaseBlock(DefaultCaseBlock node) { callback.caseDefaultCaseBlock(node); }
    public void caseDotExpr(DotExpr node) { callback.caseDotExpr(node); }
    public void caseEDivExpr(EDivExpr node) { callback.caseEDivExpr(node); }
    public void caseELDivExpr(ELDivExpr node) { callback.caseELDivExpr(node); }
    public void caseEPowExpr(EPowExpr node) { callback.caseEPowExpr(node); }
    public void caseEQExpr(EQExpr node) { callback.caseEQExpr(node); }
    public void caseETimesExpr(ETimesExpr node) { callback.caseETimesExpr(node); }
    public void caseElseBlock(ElseBlock node) { callback.caseElseBlock(node); }
    public void caseEmptyProgram(EmptyProgram node) { callback.caseEmptyProgram(node); }
    public void caseEmptyStmt(EmptyStmt node) { callback.caseEmptyStmt(node); }
    public void caseEndCallExpr(EndCallExpr node) { callback.caseEndCallExpr(node); }
    public void caseEndExpr(EndExpr node) { callback.caseEndExpr(node); }
    public void caseEvent(Event node) { callback.caseEvent(node); }
    public void caseExpandedAnnotation(ExpandedAnnotation node) { callback.caseExpandedAnnotation(node); }
    public void caseExpr(Expr node) { callback.caseExpr(node); }
    public void caseExprStmt(ExprStmt node) { callback.caseExprStmt(node); }
    public void caseExtents(Extents node) { callback.caseExtents(node); }
    public void caseFPLiteralExpr(FPLiteralExpr node) { callback.caseFPLiteralExpr(node); }
    public void caseFieldEntry(FieldEntry node) { callback.caseFieldEntry(node); }
    public void caseFloat32(Float32 node) { callback.caseFloat32(node); }
    public void caseFloat64(Float64 node) { callback.caseFloat64(node); }
    public void caseFloatType(FloatType node) { callback.caseFloatType(node); }
    public void caseForStmt(ForStmt node) { callback.caseForStmt(node); }
    public void caseFunction(Function node) { callback.caseFunction(node); }
    public void caseFunctionDecl(FunctionDecl node) { callback.caseFunctionDecl(node); }
    public void caseFunctionHandleExpr(FunctionHandleExpr node) { callback.caseFunctionHandleExpr(node); }
    public void caseFunctionList(FunctionList node) { callback.caseFunctionList(node); }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(
            FunctionOrSignatureOrPropertyAccessOrStmt node) { callback.caseFunctionOrSignatureOrPropertyAccessOrStmt(node); }
    public void caseGEExpr(GEExpr node) { callback.caseGEExpr(node); }
    public void caseGTExpr(GTExpr node) { callback.caseGTExpr(node); }
    public void caseGlobalStmt(GlobalStmt node) { callback.caseGlobalStmt(node); }
    public void caseHandleType(HandleType node) { callback.caseHandleType(node); }
    public void caseHelpComment(HelpComment node) { callback.caseHelpComment(node); }
    public void caseIfBlock(IfBlock node) { callback.caseIfBlock(node); }
    public void caseIfStmt(IfStmt node) { callback.caseIfStmt(node); }
    public void caseInt16(Int16 node) { callback.caseInt16(node); }
    public void caseInt32(Int32 node) { callback.caseInt32(node); }
    public void caseInt64(Int64 node) { callback.caseInt64(node); }
    public void caseInt8(Int8 node) { callback.caseInt8(node); }
    public void caseIntLiteralExpr(IntLiteralExpr node) { callback.caseIntLiteralExpr(node); }
    public void caseIntType(IntType node) { callback.caseIntType(node); }
    public void caseIntersectionType(IntersectionType node) { callback.caseIntersectionType(node); }
    public void caseKnownExtents(KnownExtents node) { callback.caseKnownExtents(node); }
    public void caseKnownExtentsShape(KnownExtentsShape node) { callback.caseKnownExtentsShape(node); }
    public void caseKnownRank(KnownRank node) { callback.caseKnownRank(node); }
    public void caseLEExpr(LEExpr node) { callback.caseLEExpr(node); }
    public void caseLTExpr(LTExpr node) { callback.caseLTExpr(node); }
    public void caseLValueExpr(LValueExpr node) { callback.caseLValueExpr(node); }
    public void caseLambdaExpr(LambdaExpr node) { callback.caseLambdaExpr(node); }
    public void caseList(List node) { callback.caseList(node); }
    public void caseLiteralExpr(LiteralExpr node) { callback.caseLiteralExpr(node); }
    public void caseMDivExpr(MDivExpr node) { callback.caseMDivExpr(node); }
    public void caseMLDivExpr(MLDivExpr node) { callback.caseMLDivExpr(node); }
    public void caseMPowExpr(MPowExpr node) { callback.caseMPowExpr(node); }
    public void caseMTimesExpr(MTimesExpr node) { callback.caseMTimesExpr(node); }
    public void caseMTransposeExpr(MTransposeExpr node) { callback.caseMTransposeExpr(node); }
    public void caseMatrixExpr(MatrixExpr node) { callback.caseMatrixExpr(node); }
    public void caseMatrixType(MatrixType node) { callback.caseMatrixType(node); }
    public void caseMethods(Methods node) { callback.caseMethods(node); }
    public void caseMinusExpr(MinusExpr node) { callback.caseMinusExpr(node); }
    public void caseMultiLineHelpComment(MultiLineHelpComment node) { callback.caseMultiLineHelpComment(node); }
    public void caseNEExpr(NEExpr node) { callback.caseNEExpr(node); }
    public void caseName(Name node) { callback.caseName(node); }
    public void caseNameExpr(NameExpr node) { callback.caseNameExpr(node); }
    public void caseNotExpr(NotExpr node) { callback.caseNotExpr(node); }
    public void caseOneLineHelpComment(OneLineHelpComment node) { callback.caseOneLineHelpComment(node); }
    public void caseOrExpr(OrExpr node) { callback.caseOrExpr(node); }
    public void caseParameterizedExpr(ParameterizedExpr node) { callback.caseParameterizedExpr(node); }
    public void casePersistentStmt(PersistentStmt node) { callback.casePersistentStmt(node); }
    public void casePlusExpr(PlusExpr node) { callback.casePlusExpr(node); }
    public void casePrimitiveType(PrimitiveType node) { callback.casePrimitiveType(node); }
    public void caseProgram(Program node) { callback.caseProgram(node); }
    public void caseProperties(Properties node) { callback.caseProperties(node); }
    public void caseProperty(Property node) { callback.caseProperty(node); }
    public void casePropertyAccess(PropertyAccess node) { callback.casePropertyAccess(node); }
    public void caseRangeExpr(RangeExpr node) { callback.caseRangeExpr(node); }
    public void caseRangeForStmt(ForStmt node) { callback.caseForStmt(node); }
    public void caseRank(Rank node) { callback.caseRank(node); }
    public void caseReturnStmt(ReturnStmt node) { callback.caseReturnStmt(node); }
    public void caseRow(Row node) { callback.caseRow(node); }
    public void caseScript(Script node) { callback.caseScript(node); }
    public void caseShape(Shape node) { callback.caseShape(node); }
    public void caseShellCommandStmt(ShellCommandStmt node) { callback.caseShellCommandStmt(node); }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node) { callback.caseShortCircuitAndExpr(node); }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node) { callback.caseShortCircuitOrExpr(node); }
    public void caseSignature(Signature node) { callback.caseSignature(node); }
    public void caseStmt(Stmt node) { callback.caseStmt(node); }
    public void caseStringLiteralExpr(StringLiteralExpr node) { callback.caseStringLiteralExpr(node); }
    public void caseStructType(StructType node) { callback.caseStructType(node); }
    public void caseSuperClass(SuperClass node) { callback.caseSuperClass(node); }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node) { callback.caseSuperClassMethodExpr(node); }
    public void caseSwitchCaseBlock(SwitchCaseBlock node) { callback.caseSwitchCaseBlock(node); }
    public void caseSwitchStmt(SwitchStmt node) { callback.caseSwitchStmt(node); }
    public void caseTryStmt(TryStmt node) { callback.caseTryStmt(node); }
    public void caseTupleType(TupleType node) { callback.caseTupleType(node); }
    public void caseType(Type node) { callback.caseType(node); }
    public void caseTypeVarType(TypeVarType node) { callback.caseTypeVarType(node); }
    public void caseUMinusExpr(UMinusExpr node) { callback.caseUMinusExpr(node); }
    public void caseUPlusExpr(UPlusExpr node) { callback.caseUPlusExpr(node); }
    public void caseUnaryExpr(UnaryExpr node) { callback.caseUnaryExpr(node); }
    public void caseUnionType(UnionType node) { callback.caseUnionType(node); }
    public void caseUnitType(UnitType node) { callback.caseUnitType(node); }
    public void caseUnknownType(UnknownType node) { callback.caseUnknownType(node); }
    public void caseVarArgType(VarArgType node) { callback.caseVarArgType(node); }
    public void caseVariableDecl(VariableDecl node) { callback.caseVariableDecl(node); }
    public void caseWhileStmt(WhileStmt node) { callback.caseWhileStmt(node); }
}
