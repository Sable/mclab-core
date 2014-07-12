// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
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

import nodecases.NodeCaseHandler;
/**
 * a node case handler which simply forwards a case call to the
 * same case call of another NodeCaseHandler
 */
import ast.ASTNode;
import ast.AndExpr;
import ast.ArrayTransposeExpr;
import ast.AssignStmt;
import ast.Attribute;
import ast.BinaryExpr;
import ast.Body;
import ast.BreakStmt;
import ast.CSLExpr;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.CheckScalarStmt;
import ast.ClassBody;
import ast.ClassDef;
import ast.ClassEvents;
import ast.ColonExpr;
import ast.CompilationUnits;
import ast.ContinueStmt;
import ast.DefaultCaseBlock;
import ast.DotExpr;
import ast.EDivExpr;
import ast.ELDivExpr;
import ast.EPowExpr;
import ast.EQExpr;
import ast.ETimesExpr;
import ast.ElseBlock;
import ast.EmptyStmt;
import ast.EndCallExpr;
import ast.EndExpr;
import ast.Event;
import ast.Expr;
import ast.ExprStmt;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.FunctionOrSignatureOrPropertyAccessOrStmt;
import ast.GEExpr;
import ast.GTExpr;
import ast.GlobalStmt;
import ast.HelpComment;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.LEExpr;
import ast.LTExpr;
import ast.LValueExpr;
import ast.LambdaExpr;
import ast.List;
import ast.LiteralExpr;
import ast.MDivExpr;
import ast.MLDivExpr;
import ast.MPowExpr;
import ast.MTimesExpr;
import ast.MTransposeExpr;
import ast.MatrixExpr;
import ast.Methods;
import ast.MinusExpr;
import ast.MultiLineHelpComment;
import ast.NEExpr;
import ast.Name;
import ast.NameExpr;
import ast.NotExpr;
import ast.OneLineHelpComment;
import ast.OrExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.PlusExpr;
import ast.Program;
import ast.Properties;
import ast.Property;
import ast.PropertyAccess;
import ast.RangeExpr;
import ast.ReturnStmt;
import ast.Row;
import ast.Script;
import ast.ShellCommandStmt;
import ast.ShortCircuitAndExpr;
import ast.ShortCircuitOrExpr;
import ast.Signature;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.SuperClass;
import ast.SuperClassMethodExpr;
import ast.SwitchCaseBlock;
import ast.SwitchStmt;
import ast.TryStmt;
import ast.UMinusExpr;
import ast.UPlusExpr;
import ast.UnaryExpr;
import ast.WhileStmt;
public class NatlabForwardingNodeCaseHandler implements NodeCaseHandler {
    protected NodeCaseHandler callback;
    public NatlabForwardingNodeCaseHandler (NodeCaseHandler callback){
        this.callback = callback;
    }
    
    public void caseASTNode(ASTNode node) { callback.caseASTNode(node); }
    public void caseAndExpr(AndExpr node) { callback.caseAndExpr(node); }
    public void caseArrayTransposeExpr(ArrayTransposeExpr node) { callback.caseArrayTransposeExpr(node); }
    public void caseAssignStmt(AssignStmt node) { callback.caseAssignStmt(node); }
    public void caseAttribute(Attribute node) { callback.caseAttribute(node); }
    public void caseBinaryExpr(BinaryExpr node) { callback.caseBinaryExpr(node); }
    public void caseBody(Body node) { callback.caseBody(node); }
    public void caseBreakStmt(BreakStmt node) { callback.caseBreakStmt(node); }
    public void caseCSLExpr(CSLExpr node) { callback.caseCSLExpr(node); }
    public void caseCellArrayExpr(CellArrayExpr node) { callback.caseCellArrayExpr(node); }
    public void caseCellIndexExpr(CellIndexExpr node) { callback.caseCellIndexExpr(node); }
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
    public void caseEmptyStmt(EmptyStmt node) { callback.caseEmptyStmt(node); }
    public void caseEndCallExpr(EndCallExpr node) { callback.caseEndCallExpr(node); }
    public void caseEndExpr(EndExpr node) { callback.caseEndExpr(node); }
    public void caseEvent(Event node) { callback.caseEvent(node); }
    public void caseExpr(Expr node) { callback.caseExpr(node); }
    public void caseExprStmt(ExprStmt node) { callback.caseExprStmt(node); }
    public void caseFPLiteralExpr(FPLiteralExpr node) { callback.caseFPLiteralExpr(node); }
    public void caseForStmt(ForStmt node) { callback.caseForStmt(node); }
    public void caseFunction(Function node) { callback.caseFunction(node); }
    public void caseFunctionHandleExpr(FunctionHandleExpr node) { callback.caseFunctionHandleExpr(node); }
    public void caseFunctionList(FunctionList node) { callback.caseFunctionList(node); }
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(
            FunctionOrSignatureOrPropertyAccessOrStmt node) { callback.caseFunctionOrSignatureOrPropertyAccessOrStmt(node); }
    public void caseGEExpr(GEExpr node) { callback.caseGEExpr(node); }
    public void caseGTExpr(GTExpr node) { callback.caseGTExpr(node); }
    public void caseGlobalStmt(GlobalStmt node) { callback.caseGlobalStmt(node); }
    public void caseHelpComment(HelpComment node) { callback.caseHelpComment(node); }
    public void caseIfBlock(IfBlock node) { callback.caseIfBlock(node); }
    public void caseIfStmt(IfStmt node) { callback.caseIfStmt(node); }
    public void caseIntLiteralExpr(IntLiteralExpr node) { callback.caseIntLiteralExpr(node); }
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
    public void caseProgram(Program node) { callback.caseProgram(node); }
    public void caseProperties(Properties node) { callback.caseProperties(node); }
    public void caseProperty(Property node) { callback.caseProperty(node); }
    public void casePropertyAccess(PropertyAccess node) { callback.casePropertyAccess(node); }
    public void caseRangeExpr(RangeExpr node) { callback.caseRangeExpr(node); }
    public void caseRangeForStmt(ForStmt node) { callback.caseForStmt(node); }
    public void caseReturnStmt(ReturnStmt node) { callback.caseReturnStmt(node); }
    public void caseRow(Row node) { callback.caseRow(node); }
    public void caseScript(Script node) { callback.caseScript(node); }
    public void caseShellCommandStmt(ShellCommandStmt node) { callback.caseShellCommandStmt(node); }
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node) { callback.caseShortCircuitAndExpr(node); }
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node) { callback.caseShortCircuitOrExpr(node); }
    public void caseSignature(Signature node) { callback.caseSignature(node); }
    public void caseStmt(Stmt node) { callback.caseStmt(node); }
    public void caseStringLiteralExpr(StringLiteralExpr node) { callback.caseStringLiteralExpr(node); }
    public void caseSuperClass(SuperClass node) { callback.caseSuperClass(node); }
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node) { callback.caseSuperClassMethodExpr(node); }
    public void caseSwitchCaseBlock(SwitchCaseBlock node) { callback.caseSwitchCaseBlock(node); }
    public void caseSwitchStmt(SwitchStmt node) { callback.caseSwitchStmt(node); }
    public void caseTryStmt(TryStmt node) { callback.caseTryStmt(node); }
    public void caseUMinusExpr(UMinusExpr node) { callback.caseUMinusExpr(node); }
    public void caseUPlusExpr(UPlusExpr node) { callback.caseUPlusExpr(node); }
    public void caseUnaryExpr(UnaryExpr node) { callback.caseUnaryExpr(node); }
    public void caseWhileStmt(WhileStmt node) { callback.caseWhileStmt(node); }
}
