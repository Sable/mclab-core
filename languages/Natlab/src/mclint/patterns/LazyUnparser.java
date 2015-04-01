package mclint.patterns;

import java.util.LinkedList;
import java.util.List;

import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AndExpr;
import ast.ArrayTransposeExpr;
import ast.AssignStmt;
import ast.Attribute;
import ast.BinaryExpr;
import ast.BreakStmt;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.ClassBody;
import ast.ClassDef;
import ast.ClassEvents;
import ast.ColonExpr;
import ast.ContinueStmt;
import ast.DefaultCaseBlock;
import ast.DotExpr;
import ast.EDivExpr;
import ast.ELDivExpr;
import ast.EPowExpr;
import ast.EQExpr;
import ast.ETimesExpr;
import ast.ElseBlock;
import ast.EndExpr;
import ast.Event;
import ast.ExprStmt;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.GEExpr;
import ast.GTExpr;
import ast.GlobalStmt;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.LEExpr;
import ast.LTExpr;
import ast.LValueExpr;
import ast.LambdaExpr;
import ast.MDivExpr;
import ast.MLDivExpr;
import ast.MPowExpr;
import ast.MTimesExpr;
import ast.MTransposeExpr;
import ast.MatrixExpr;
import ast.Methods;
import ast.MinusExpr;
import ast.NEExpr;
import ast.Name;
import ast.NameExpr;
import ast.NotExpr;
import ast.OrExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.PlusExpr;
import ast.Properties;
import ast.Property;
import ast.PropertyAccess;
import ast.RangeExpr;
import ast.ReturnStmt;
import ast.Row;
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
import ast.WhileStmt;

public class LazyUnparser extends AbstractNodeCaseHandler {
  private List<Object> tokens = new LinkedList<>();

  public static List<Object> unparse(ASTNode<?> node) {
    LazyUnparser unparser = new LazyUnparser();
    node.analyze(unparser);
    return unparser.tokens;
  }

  public static String lookahead(ASTNode<?> node) {
    Object result = unparse(node).get(0);
    while (result instanceof ASTNode<?>) {
      result = unparse((ASTNode<?>) result).get(0);
    }
    return (String) result;
  }

  private void tokens(Object... args) {
    for (Object object : args) {
      tokens.add(object);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      node.getChild(i).analyze(this);
    }
  }

  private <T extends ASTNode<?> >ast.List<T> removeFirst(ast.List<T> node) {
    ast.List<T> copy = new ast.List<T>();
    for (int i = 1; i < node.getNumChild(); ++i) {
      copy.add(node.getChild(i));
    }
    copy.setParent(node.getParent());
    return copy;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void caseList(ast.List node) {
    if (node.getNumChild() == 0) {
      return;
    }
    tokens(node.getChild(0));
    if (node.getNumChild() > 1) {
      if (node.getParent() instanceof ParameterizedExpr) {
        tokens(",");
      }
      tokens(removeFirst(node));
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
  
  private boolean isInsideLoop(ASTNode<?> node) {
    return NodeFinder.findParent(ForStmt.class, node) != null
        || NodeFinder.findParent(WhileStmt.class, node) != null;
  }
  
  @Override
  public void caseStmt(Stmt node) {
    if (node instanceof AssignStmt && node.getParent() instanceof ForStmt &&
        node == ((ForStmt) node.getParent()).getAssignStmt()) {
      return;
    }
    if (isInsideLoop(node)) {
      tokens(";");
    }
  }

  @Override
  public void caseExprStmt(ExprStmt node) {
    tokens(node.getExpr());
    caseStmt(node);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    tokens(node.getLHS(), "=", node.getRHS());
    caseStmt(node);
  }

  @Override
  public void caseGlobalStmt(GlobalStmt node) {
    tokens("global", node.getNames());
    caseStmt(node);
  }

  @Override
  public void casePersistentStmt(PersistentStmt node) {
    tokens("persistent", node.getNames());
    caseStmt(node);
  }

  @Override
  public void caseShellCommandStmt(ShellCommandStmt node) {
    tokens("!", node.getCommand());
    caseStmt(node);
  }

  @Override
  public void caseBreakStmt(BreakStmt node) {
    tokens("break");
    caseStmt(node);
  }

  @Override
  public void caseContinueStmt(ContinueStmt node) {
    tokens("continue");
    caseStmt(node);
  }

  @Override
  public void caseReturnStmt(ReturnStmt node) {
    tokens("return");
    caseStmt(node);
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
