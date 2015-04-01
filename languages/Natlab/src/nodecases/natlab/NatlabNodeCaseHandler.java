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

package nodecases.natlab;

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

/**
 * A collection of cases to match each node type in the AST. This can
 * be used to create visitors.
 */
public interface NatlabNodeCaseHandler
{
    public void caseASTNode(ASTNode node);
    public void caseList(List node);
    public void caseProgram(Program node);
    public void caseBody(Body node);
    public void caseFunctionOrSignatureOrPropertyAccessOrStmt(FunctionOrSignatureOrPropertyAccessOrStmt node);
    public void caseHelpComment(HelpComment node);
    public void caseExpr(Expr node);
    public void caseCompilationUnits(CompilationUnits node);
    public void caseAttribute(Attribute node);
    public void caseSuperClass(SuperClass node);
    public void caseProperty(Property node);
    public void caseEvent(Event node);
    public void caseName(Name node);
    public void caseSwitchCaseBlock(SwitchCaseBlock node);
    public void caseDefaultCaseBlock(DefaultCaseBlock node);
    public void caseIfBlock(IfBlock node);
    public void caseElseBlock(ElseBlock node);
    public void caseRow(Row node);
    public void caseClassBody(ClassBody node);
    public void caseStmt(Stmt node);
    public void caseLValueExpr(LValueExpr node);
    public void caseLiteralExpr(LiteralExpr node);
    public void caseUnaryExpr(UnaryExpr node);
    public void caseBinaryExpr(BinaryExpr node);
    public void caseScript(Script node);
    public void caseFunctionList(FunctionList node);
    public void caseClassDef(ClassDef node);
    public void caseProperties(Properties node);
    public void caseMethods(Methods node);
    public void caseSignature(Signature node);
    public void casePropertyAccess(PropertyAccess node);
    public void caseClassEvents(ClassEvents node);
    public void caseFunction(Function node);
    public void caseOneLineHelpComment(OneLineHelpComment node);
    public void caseMultiLineHelpComment(MultiLineHelpComment node);
    public void caseExprStmt(ExprStmt node);
    public void caseAssignStmt(AssignStmt node);
    public void caseGlobalStmt(GlobalStmt node);
    public void casePersistentStmt(PersistentStmt node);
    public void caseShellCommandStmt(ShellCommandStmt node);
    public void caseBreakStmt(BreakStmt node);
    public void caseContinueStmt(ContinueStmt node);
    public void caseReturnStmt(ReturnStmt node);
    public void caseEmptyStmt(EmptyStmt node);
    public void caseForStmt(ForStmt node);
    public void caseWhileStmt(WhileStmt node);
    public void caseTryStmt(TryStmt node);
    public void caseSwitchStmt(SwitchStmt node);
    public void caseIfStmt(IfStmt node);
    public void caseRangeExpr(RangeExpr node);
    public void caseColonExpr(ColonExpr node);
    public void caseEndExpr(EndExpr node);
    public void caseNameExpr(NameExpr node);
    public void caseParameterizedExpr(ParameterizedExpr node);
    public void caseCellIndexExpr(CellIndexExpr node);
    public void caseDotExpr(DotExpr node);
    public void caseMatrixExpr(MatrixExpr node);
    public void caseCellArrayExpr(CellArrayExpr node);
    public void caseSuperClassMethodExpr(SuperClassMethodExpr node);
    public void caseIntLiteralExpr(IntLiteralExpr node);
    public void caseFPLiteralExpr(FPLiteralExpr node);
    public void caseStringLiteralExpr(StringLiteralExpr node);
    public void caseUMinusExpr(UMinusExpr node);
    public void caseUPlusExpr(UPlusExpr node);
    public void caseNotExpr(NotExpr node);
    public void caseMTransposeExpr(MTransposeExpr node);
    public void caseArrayTransposeExpr(ArrayTransposeExpr node);
    public void casePlusExpr(PlusExpr node);
    public void caseMinusExpr(MinusExpr node);
    public void caseMTimesExpr(MTimesExpr node);
    public void caseMDivExpr(MDivExpr node);
    public void caseMLDivExpr(MLDivExpr node);
    public void caseMPowExpr(MPowExpr node);
    public void caseETimesExpr(ETimesExpr node);
    public void caseEDivExpr(EDivExpr node);
    public void caseELDivExpr(ELDivExpr node);
    public void caseEPowExpr(EPowExpr node);
    public void caseAndExpr(AndExpr node);
    public void caseOrExpr(OrExpr node);
    public void caseShortCircuitAndExpr(ShortCircuitAndExpr node);
    public void caseShortCircuitOrExpr(ShortCircuitOrExpr node);
    public void caseLTExpr(LTExpr node);
    public void caseGTExpr(GTExpr node);
    public void caseLEExpr(LEExpr node);
    public void caseGEExpr(GEExpr node);
    public void caseEQExpr(EQExpr node);
    public void caseNEExpr(NEExpr node);
    public void caseFunctionHandleExpr(FunctionHandleExpr node);
    public void caseLambdaExpr(LambdaExpr node);
    public void caseCSLExpr(CSLExpr node);
    public void caseEndCallExpr(EndCallExpr node);
    public void caseCheckScalarStmt(CheckScalarStmt node);
}
