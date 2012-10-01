package mclint.patterns;

import java.util.LinkedList;

import nodecases.AbstractNodeCaseHandler;
import ast.*;

public class LazyUnparser extends AbstractNodeCaseHandler {
  private java.util.List<Object> tokens = new LinkedList<Object>();
  public static java.util.List<Object> unparse(ASTNode node) {
    LazyUnparser unparser = new LazyUnparser();
    node.analyze(unparser);
    return unparser.tokens;
  }
  
  private void tokens(Object... args) {
    for (Object object : args) {
      tokens.add(object);
    }
  }
  
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      node.getChild(i).analyze(this);
    }
  }

  @Override
  public void caseList(List node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      tokens(node.getChild(i));
    }
  }

  @Override
  public void caseAttribute(Attribute node) {
    tokens(node.getKey(), "=", node.getExpr());
  }

  @Override
  public void caseSuperClass(SuperClass node) {
    tokens(node.getName());
  }

  @Override
  public void caseProperty(Property node) {
    tokens(node.getName(), "=", node.getExpr(), ";");
  }

  @Override
  public void caseEvent(Event node) {
    tokens(node.getName());
  }

  @Override
  public void caseName(Name node) {
    tokens(node.getID());
  }

  @Override
  public void caseSwitchCaseBlock(SwitchCaseBlock node) {
    tokens("case", node.getExpr(), node.getStmts());
  }

  @Override
  public void caseDefaultCaseBlock(DefaultCaseBlock node) {
    tokens("otherwise", node.getStmts());
  }

  @Override
  public void caseIfBlock(IfBlock node) {
    tokens(node.getCondition(), node.getStmts());
  }

  @Override
  public void caseElseBlock(ElseBlock node) {
    tokens("else", node.getStmts());
  }

  @Override
  public void caseRow(Row node) {
    tokens(node.getElements());
  }

  @Override
  public void caseClassBody(ClassBody node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseLValueExpr(LValueExpr node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseFunctionList(FunctionList node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseClassDef(ClassDef node) {

  }

  @Override
  public void caseProperties(Properties node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseMethods(Methods node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseSignature(Signature node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void casePropertyAccess(PropertyAccess node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseClassEvents(ClassEvents node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void caseFunction(Function node) {
    tokens("function", "[", node.getOutputParams(), "]", node.getName(), "=", "(",
        node.getInputParams(), ")", node.getStmts(), "end");
  }

  @Override
  public void caseExprStmt(ExprStmt node) {
    tokens(node.getExpr());
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    tokens(node.getLHS(), "=", node.getRHS());
  }

  @Override
  public void caseGlobalStmt(GlobalStmt node) {
    tokens("global", node.getNames());
  }

  @Override
  public void casePersistentStmt(PersistentStmt node) {
    tokens("persistent", node.getNames());
  }

  @Override
  public void caseShellCommandStmt(ShellCommandStmt node) {
    tokens("!", node.getCommand());
  }

  @Override
  public void caseBreakStmt(BreakStmt node) {
    tokens("break");
  }

  @Override
  public void caseContinueStmt(ContinueStmt node) {
    tokens("continue");
  }

  @Override
  public void caseReturnStmt(ReturnStmt node) {
    tokens("return");
  }

  @Override
  public void caseForStmt(ForStmt node) {
    tokens("for", node.getAssignStmt(), node.getStmts(), "end");
  }

  @Override
  public void caseWhileStmt(WhileStmt node) {
    tokens("while", node.getExpr(), node.getStmts(), "end");
  }

  @Override
  public void caseTryStmt(TryStmt node) {
    tokens("try", node.getTryStmts());
    if (node.getCatchStmts().getNumChild() > 0) {
      tokens("catch", node.getCatchStmts());
    }
    tokens("end");
  }

  @Override
  public void caseSwitchStmt(SwitchStmt node) {
    tokens("switch", node.getExpr(), node.getSwitchCaseBlocks());
    if (node.hasDefaultCaseBlock()) {
      tokens(node.getDefaultCaseBlock());
    }
    tokens("end");
  }

  @Override
  public void caseIfStmt(IfStmt node) {
    boolean first = true;
    for (IfBlock block : node.getIfBlocks()) {
      if (first) {
        tokens("if");
      } else {
        tokens("elseif");
      }
      tokens(block);
    }
    if (node.hasElseBlock()) {
      tokens(node.getElseBlock());
    }
    tokens("end");
  }

  @Override
  public void caseRangeExpr(RangeExpr node) {
    tokens("(", node.getLower(), ":");
    if (node.hasIncr()) {
      tokens(node.getIncr(), ":");
    }
    tokens(node.getUpper(), ")");
  }

  @Override
  public void caseColonExpr(ColonExpr node) {
    tokens(":");
  }

  @Override
  public void caseEndExpr(EndExpr node) {
    tokens("end");
  }

  @Override
  public void caseNameExpr(NameExpr node) {
    tokens(node.getName().getID());
  }

  @Override
  public void caseParameterizedExpr(ParameterizedExpr node) {
    tokens(node.getTarget(), "(", node.getArgs(), ")");
  }

  @Override
  public void caseCellIndexExpr(CellIndexExpr node) {
    tokens(node.getTarget(), "{", node.getArgs(), "}");
  }

  @Override
  public void caseDotExpr(DotExpr node) {
    tokens(node.getTarget(), ".", node.getField());
  }

  @Override
  public void caseMatrixExpr(MatrixExpr node) {
    tokens("[", node.getRows(), "]");
  }

  @Override
  public void caseCellArrayExpr(CellArrayExpr node) {
    tokens("{", node.getRows(), "}");
  }

  @Override
  public void caseSuperClassMethodExpr(SuperClassMethodExpr node) {
    tokens(node.getFuncName(), "@", node.getClassName());
  }

  @Override
  public void caseIntLiteralExpr(IntLiteralExpr node) {
    tokens(node.getValue().getText());
  }

  @Override
  public void caseFPLiteralExpr(FPLiteralExpr node) {
    tokens(node.getValue().getText());
  }

  @Override
  public void caseStringLiteralExpr(StringLiteralExpr node) {
    tokens("\"", node.getValue(), "\"");
  }

  @Override
  public void caseUMinusExpr(UMinusExpr node) {
    tokens("(", "-", node.getOperand(), ")");
  }

  @Override
  public void caseUPlusExpr(UPlusExpr node) {
    tokens("(", "+", node.getOperand(), ")");
  }

  @Override
  public void caseNotExpr(NotExpr node) {
    tokens("(", "~", node.getOperand(), ")");
  }

  @Override
  public void caseMTransposeExpr(MTransposeExpr node) {
    tokens("(", node.getOperand(), "'", ")");
  }

  @Override
  public void caseArrayTransposeExpr(ArrayTransposeExpr node) {
    tokens("(", node.getOperand(), ".", ")");
  }
  
  private void binary(BinaryExpr node, String op) {
    tokens("(", node.getLHS(), op, node.getRHS(), ")");
  }

  @Override
  public void casePlusExpr(PlusExpr node) {
    binary(node, "+");
  }

  @Override
  public void caseMinusExpr(MinusExpr node) {
    binary(node, "-");
  }

  @Override
  public void caseMTimesExpr(MTimesExpr node) {
    binary(node, "*");
  }

  @Override
  public void caseMDivExpr(MDivExpr node) {
    binary(node, "/");
  }

  @Override
  public void caseMLDivExpr(MLDivExpr node) {
    binary(node, "\\\\");
  }

  @Override
  public void caseMPowExpr(MPowExpr node) {
    binary(node, "^");
  }

  @Override
  public void caseETimesExpr(ETimesExpr node) {
    binary(node, ".*");
  }

  @Override
  public void caseEDivExpr(EDivExpr node) {
    binary(node, "./");
  }

  @Override
  public void caseELDivExpr(ELDivExpr node) {
    binary(node, ".\\\\");
  }

  @Override
  public void caseEPowExpr(EPowExpr node) {
    binary(node, ".^");
  }

  @Override
  public void caseAndExpr(AndExpr node) {
    binary(node, "&");
  }

  @Override
  public void caseOrExpr(OrExpr node) {
    binary(node, "|");
  }

  @Override
  public void caseShortCircuitAndExpr(ShortCircuitAndExpr node) {
    binary(node, "&&");
  }

  @Override
  public void caseShortCircuitOrExpr(ShortCircuitOrExpr node) {
    binary(node, "||");
  }

  @Override
  public void caseLTExpr(LTExpr node) {
    binary(node, "<");
  }

  @Override
  public void caseGTExpr(GTExpr node) {
    binary(node, ">");
  }

  @Override
  public void caseLEExpr(LEExpr node) {
    binary(node, "<=");
  }

  @Override
  public void caseGEExpr(GEExpr node) {
    binary(node, ">=");
  }

  @Override
  public void caseEQExpr(EQExpr node) {
    binary(node, "==");
  }

  @Override
  public void caseNEExpr(NEExpr node) {
    binary(node, "~=");
  }

  @Override
  public void caseFunctionHandleExpr(FunctionHandleExpr node) {
    tokens("@", node.getName().getStructureString());
  }

  @Override
  public void caseLambdaExpr(LambdaExpr node) {
    tokens("(", "@", "(", node.getInputParams(), ")", node.getBody(), ")");
  }
}
