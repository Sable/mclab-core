// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //


package analysis.natlab;

import nodecases.*;
import analysis.*;

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
 * 
 * This function allows to specify a different callback than the analysis itself
 */
public class NatlabAnalysisHelper<A> implements nodecases.NodeCaseHandler //extends AbstractNodeCaseHandler
{

    /** 
     * The analysis being helped
     */
    protected StructuralAnalysis<A> helpee;
    protected NodeCaseHandler callback;    
    

    /**
     * Class constructor with given helpee.
     *
     * @param helpee  the analysis being helped.
     */
    public NatlabAnalysisHelper( StructuralAnalysis<A> helpee )
    {
        this.helpee = helpee;
        this.callback = helpee;
    }
    /**
     * Class constructor with given helpee and callback.
     *
     * @param helpee  the analysis being helped.
     * @param callback calls the corresponding case on the callback after bookkeeping
     */
    public NatlabAnalysisHelper( StructuralAnalysis<A> helpee , NodeCaseHandler callback )
    {
        this.helpee = helpee;
        this.callback = callback;
    }

    protected void setCallback(NodeCaseHandler callback){
        this.callback = callback;
    }
    
    public void caseASTNode(ASTNode node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseASTNode( node );
    }
    public void caseList( List node )
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseList( node );
    }
    public void caseProgram(Program node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseProgram( node );
    }
    public void caseStmt(Stmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseStmt( node );
    }
    public void caseBreakStmt(BreakStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseBreakStmt( node );
    }
    public void caseContinueStmt(ContinueStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseContinueStmt( node );
    }
    public void caseReturnStmt(ReturnStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseReturnStmt( node );
    }
    public void caseForStmt(ForStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseForStmt( node );
    }
    public void caseWhileStmt(WhileStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseWhileStmt( node );
    }
    public void caseTryStmt(TryStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseTryStmt( node );
    }
    public void caseSwitchStmt(SwitchStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseSwitchStmt( node );
    }
    public void caseIfStmt(IfStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseIfStmt( node );
    }
    public void caseScript(Script node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseScript( node );
    }
    public void caseFunctionList(FunctionList node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseFunctionList( node );
    }
    public void caseExprStmt(ExprStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseExprStmt( node );
    }
    public void caseAssignStmt(AssignStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseAssignStmt( node );
    }
    public void caseGlobalStmt(GlobalStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseGlobalStmt( node );
    }
    public void casePersistentStmt(PersistentStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.casePersistentStmt( node );
    }
    public void caseShellCommandStmt(ShellCommandStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseShellCommandStmt( node );
    }
    public void caseExpr(Expr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseExpr( node );
    }
    public void caseRangeExpr(RangeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseRangeExpr( node );
    }
    public void caseColonExpr(ColonExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseColonExpr( node );
    }
    public void caseEndExpr(EndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEndExpr( node );
    }
    public void caseLValueExpr(LValueExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseLValueExpr( node );
    }
    public void caseNameExpr(NameExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseNameExpr( node );
    }
    public void caseParameterizedExpr(ParameterizedExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseParameterizedExpr( node );
    }
    public void caseCellIndexExpr(CellIndexExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseCellIndexExpr( node );
    }
    public void caseDotExpr(DotExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseDotExpr( node );
    }
    public void caseMatrixExpr(MatrixExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMatrixExpr( node );
    }
    public void caseCellArrayExpr(CellArrayExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseCellArrayExpr( node );
    }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseSuperClassMethodExpr( node );
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseLiteralExpr( node );
    }
    public void caseIntLiteralExpr(IntLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseIntLiteralExpr( node );
    }
    public void caseFPLiteralExpr(FPLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseFPLiteralExpr( node );
    }
    public void caseStringLiteralExpr(StringLiteralExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseStringLiteralExpr( node );
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseUnaryExpr( node );
    }
    public void caseUMinusExpr(UMinusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseUMinusExpr( node );
    }
    public void caseUPlusExpr(UPlusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseUPlusExpr( node );
    }
    public void caseNotExpr(NotExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseNotExpr( node );
    }
    public void caseMTransposeExpr(MTransposeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMTransposeExpr( node );
    }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseArrayTransposeExpr( node );
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseBinaryExpr( node );
    }
    public void casePlusExpr(PlusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.casePlusExpr( node );
    }
    public void caseMinusExpr(MinusExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMinusExpr( node );
    }
    public void caseMTimesExpr(MTimesExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMTimesExpr( node );
    }
    public void caseMDivExpr(MDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMDivExpr( node );
    }
    public void caseMLDivExpr(MLDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMLDivExpr( node );
    }
    public void caseMPowExpr(MPowExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMPowExpr( node );
    }
    public void caseETimesExpr(ETimesExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseETimesExpr( node );
    }
    public void caseEDivExpr(EDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEDivExpr( node );
    }
    public void caseELDivExpr(ELDivExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseELDivExpr( node );
    }
    public void caseEPowExpr(EPowExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEPowExpr( node );
    }
    public void caseAndExpr(AndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseAndExpr( node );
    }
    public void caseOrExpr(OrExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseOrExpr( node );
    }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseShortCircuitAndExpr( node );
    }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseShortCircuitOrExpr( node );
    }
    public void caseLTExpr(LTExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseLTExpr( node );
    }
    public void caseGTExpr(GTExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseGTExpr( node );
    }
    public void caseLEExpr(LEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseLEExpr( node );
    }
    public void caseGEExpr(GEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseGEExpr( node );
    }
    public void caseEQExpr(EQExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEQExpr( node );
    }
    public void caseNEExpr(NEExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseNEExpr( node );
    }
    public void caseFunctionHandleExpr(FunctionHandleExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseFunctionHandleExpr( node );
    }
    public void caseLambdaExpr(LambdaExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseLambdaExpr( node );
    }
    public void caseFunction(Function node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseFunction( node );
    }
    public void caseEmptyStmt(EmptyStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEmptyStmt( node );
    }
    public void caseMultiLineHelpComment(MultiLineHelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMultiLineHelpComment( node );
    }
    public void caseOneLineHelpComment(OneLineHelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseOneLineHelpComment( node );
    }
    public void caseClassEvents(ClassEvents node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseClassEvents( node );
    }
    public void casePropertyAccess(PropertyAccess node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.casePropertyAccess( node );
    }
    public void caseSignature(Signature node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseSignature( node );
    }
    public void caseMethods(Methods node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseMethods( node );
    }
    public void caseProperties(Properties node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseProperties( node );
    }
    public void caseClassDef(ClassDef node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseClassDef( node );
    }
    public void caseEmptyProgram(EmptyProgram node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEmptyProgram( node );
    }
    public void caseClassBody(ClassBody node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseClassBody( node );
    }
    public void caseRow(Row node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseRow( node );
    }
    public void caseElseBlock(ElseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseElseBlock( node );
    }
    public void caseIfBlock(IfBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseIfBlock( node );
    }
    public void caseDefaultCaseBlock(DefaultCaseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseDefaultCaseBlock( node );
    }
    public void caseSwitchCaseBlock(SwitchCaseBlock node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseSwitchCaseBlock( node );
    }
    public void caseName(Name node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseName( node );
    }
    public void caseEvent(Event node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEvent( node );
    }
    public void caseProperty(Property node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseProperty( node );
    }
    public void caseSuperClass(SuperClass node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseSuperClass( node );
    }
    public void caseAttribute(Attribute node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseAttribute( node );
    }
    public void caseCompilationUnits(CompilationUnits node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseCompilationUnits( node );
    }
    public void caseHelpComment(HelpComment node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseHelpComment( node );
    }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseFunctionOrSignatureOrPropertyAccessOrStmt( node );
    }
    public void caseBody(Body node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseBody( node );
    }
    public void caseCSLExpr(CSLExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseCSLExpr( node );
    }
    public void caseEndCallExpr(EndCallExpr node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseEndCallExpr( node );
    }
    public void caseCheckScalarStmt(CheckScalarStmt node)
    {
        helpee.setCurrentInSet( helpee.getCurrentOutSet() );
        callback.caseCheckScalarStmt( node );
    }
}
