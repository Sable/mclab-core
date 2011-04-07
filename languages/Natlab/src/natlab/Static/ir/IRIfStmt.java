package natlab.Static.ir;

import ast.*;


/**
 * In the IR, the If statement has the form
 * 
 * if (var)
 *   ...
 * else
 *   ...
 * end
 * 
 * There will always only be one if block, and there will always be an else block
 * - which may be empty.
 */


public class IRIfStmt extends IfStmt implements IRStmt {
  public IRIfStmt(Name conditionVar,IRStatementList IfStmts,IRStatementList ElseStmts){
      super(
         new List<IfBlock>().add(new IfBlock(new NameExpr(conditionVar),IfStmts)),
         new ast.Opt<ElseBlock>(new ElseBlock(ElseStmts))
      );
  }
}



