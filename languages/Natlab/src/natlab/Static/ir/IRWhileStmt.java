package natlab.Static.ir;

import ast.*;

/**
 * while loop of the form
 * 
 * while(t)
 *   body
 *   
 * end
 * 
 * where t is a variable (nameExpression)
 * 
 * @author ant6n
 *
 */


public class IRWhileStmt extends WhileStmt implements IRNode {
   public IRWhileStmt(NameExpr condition,IRStatementList body) {
       this.setExpr(condition);
       this.setStmtList(body);
   }
   
   public NameExpr getCondition(){
       return (NameExpr)super.getExpr();
   }
   
   public IRStatementList getStatements(){
       return (IRStatementList)super.getStmts();
   }
   
}





