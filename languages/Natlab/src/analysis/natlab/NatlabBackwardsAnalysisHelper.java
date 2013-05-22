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
 */
public class NatlabBackwardsAnalysisHelper<A> extends analysis.AnalysisHelper<A>
{

    /**
     * Class constructor with given helpee.
     *
     * @param helpee  the analysis being helped.
     */
    public NatlabBackwardsAnalysisHelper( StructuralAnalysis<A> helpee )
    {
        super( helpee );
    }
    /**
     * Class constructor with given helpee and callback.
     *
     * @param helpee  the analysis being helped.
     * @param callback calls the corresponding case on the callback after bookkeeping
     */
    public NatlabBackwardsAnalysisHelper( StructuralAnalysis<A> helpee , NodeCaseHandler callback )
    {
        super(helpee, callback);
    }
    
    public void caseASTNode(ASTNode node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseASTNode( node );
    }
    public void caseList( List node )
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseList( node );
    }
    public void caseProgram(Program node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseProgram( node );
    }
    public void caseStmt(Stmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseStmt( node );
    }
    public void caseBreakStmt(BreakStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseBreakStmt( node );
    }
    public void caseContinueStmt(ContinueStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseContinueStmt( node );
    }
    public void caseReturnStmt(ReturnStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseReturnStmt( node );
    }
    public void caseForStmt(ForStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseForStmt( node );
    }
    public void caseWhileStmt(WhileStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseWhileStmt( node );
    }
    public void caseTryStmt(TryStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseTryStmt( node );
    }
    public void caseSwitchStmt(SwitchStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseSwitchStmt( node );
    }
    public void caseIfStmt(IfStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseIfStmt( node );
    }
    public void caseScript(Script node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseScript( node );
    }
    public void caseFunctionList(FunctionList node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseFunctionList( node );
    }
    public void caseExprStmt(ExprStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseExprStmt( node );
    }
    public void caseAssignStmt(AssignStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseAssignStmt( node );
    }
    public void caseGlobalStmt(GlobalStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseGlobalStmt( node );
    }
    public void casePersistentStmt(PersistentStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.casePersistentStmt( node );
    }
    public void caseShellCommandStmt(ShellCommandStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseShellCommandStmt( node );
    }
    public void caseExpr(Expr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseExpr( node );
    }
    public void caseRangeExpr(RangeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseRangeExpr( node );
    }
    public void caseColonExpr(ColonExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseColonExpr( node );
    }
    public void caseEndExpr(EndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEndExpr( node );
    }
    public void caseLValueExpr(LValueExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseLValueExpr( node );
    }
    public void caseNameExpr(NameExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseNameExpr( node );
    }
    public void caseParameterizedExpr(ParameterizedExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseParameterizedExpr( node );
    }
    public void caseCellIndexExpr(CellIndexExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseCellIndexExpr( node );
    }
    public void caseDotExpr(DotExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseDotExpr( node );
    }
    public void caseMatrixExpr(MatrixExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMatrixExpr( node );
    }
    public void caseCellArrayExpr(CellArrayExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseCellArrayExpr( node );
    }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseSuperClassMethodExpr( node );
    }
    public void caseLiteralExpr(LiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseLiteralExpr( node );
    }
    public void caseIntLiteralExpr(IntLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseIntLiteralExpr( node );
    }
    public void caseFPLiteralExpr(FPLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseFPLiteralExpr( node );
    }
    public void caseStringLiteralExpr(StringLiteralExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseStringLiteralExpr( node );
    }
    public void caseUnaryExpr(UnaryExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseUnaryExpr( node );
    }
    public void caseUMinusExpr(UMinusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseUMinusExpr( node );
    }
    public void caseUPlusExpr(UPlusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseUPlusExpr( node );
    }
    public void caseNotExpr(NotExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseNotExpr( node );
    }
    public void caseMTransposeExpr(MTransposeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMTransposeExpr( node );
    }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseArrayTransposeExpr( node );
    }
    public void caseBinaryExpr(BinaryExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseBinaryExpr( node );
    }
    public void casePlusExpr(PlusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.casePlusExpr( node );
    }
    public void caseMinusExpr(MinusExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMinusExpr( node );
    }
    public void caseMTimesExpr(MTimesExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMTimesExpr( node );
    }
    public void caseMDivExpr(MDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMDivExpr( node );
    }
    public void caseMLDivExpr(MLDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMLDivExpr( node );
    }
    public void caseMPowExpr(MPowExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMPowExpr( node );
    }
    public void caseETimesExpr(ETimesExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseETimesExpr( node );
    }
    public void caseEDivExpr(EDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEDivExpr( node );
    }
    public void caseELDivExpr(ELDivExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseELDivExpr( node );
    }
    public void caseEPowExpr(EPowExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEPowExpr( node );
    }
    public void caseAndExpr(AndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseAndExpr( node );
    }
    public void caseOrExpr(OrExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseOrExpr( node );
    }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseShortCircuitAndExpr( node );
    }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseShortCircuitOrExpr( node );
    }
    public void caseLTExpr(LTExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseLTExpr( node );
    }
    public void caseGTExpr(GTExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseGTExpr( node );
    }
    public void caseLEExpr(LEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseLEExpr( node );
    }
    public void caseGEExpr(GEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseGEExpr( node );
    }
    public void caseEQExpr(EQExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEQExpr( node );
    }
    public void caseNEExpr(NEExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseNEExpr( node );
    }
    public void caseFunctionHandleExpr(FunctionHandleExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseFunctionHandleExpr( node );
    }
    public void caseLambdaExpr(LambdaExpr node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseLambdaExpr( node );
    }
    public void caseFunction(Function node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseFunction( node );
    }
    public void caseEmptyStmt(EmptyStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEmptyStmt( node );
    }
    public void caseMultiLineHelpComment(MultiLineHelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMultiLineHelpComment( node );
    }
    public void caseOneLineHelpComment(OneLineHelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseOneLineHelpComment( node );
    }
    public void caseClassEvents(ClassEvents node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseClassEvents( node );
    }
    public void casePropertyAccess(PropertyAccess node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.casePropertyAccess( node );
    }
    public void caseSignature(Signature node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseSignature( node );
    }
    public void caseMethods(Methods node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseMethods( node );
    }
    public void caseProperties(Properties node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseProperties( node );
    }
    public void caseClassDef(ClassDef node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseClassDef( node );
    }
    public void caseEmptyProgram(EmptyProgram node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEmptyProgram( node );
    }
    public void caseClassBody(ClassBody node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseClassBody( node );
    }
    public void caseRow(Row node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseRow( node );
    }
    public void caseElseBlock(ElseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseElseBlock( node );
    }
    public void caseIfBlock(IfBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseIfBlock( node );
    }
    public void caseDefaultCaseBlock(DefaultCaseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseDefaultCaseBlock( node );
    }
    public void caseSwitchCaseBlock(SwitchCaseBlock node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseSwitchCaseBlock( node );
    }
    public void caseName(Name node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseName( node );
    }
    public void caseEvent(Event node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseEvent( node );
    }
    public void caseProperty(Property node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseProperty( node );
    }
    public void caseSuperClass(SuperClass node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseSuperClass( node );
    }
    public void caseAttribute(Attribute node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseAttribute( node );
    }
    public void caseCompilationUnits(CompilationUnits node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseCompilationUnits( node );
    }
    public void caseHelpComment(HelpComment node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseHelpComment( node );
    }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseFunctionOrSignatureOrPropertyAccessOrStmt( node );
    }
    public void caseBody(Body node)
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseBody( node );
    }
}
