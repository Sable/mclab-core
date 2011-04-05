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


public class IRWhileStmt extends WhileStmt implements IRStmt {
   public IRWhileStmt(Name condition,IRStatementList body) {
       super();
       this.setExpr(new NameExpr(condition));
       this.setStmtList(body);
   }
   
   public NameExpr getCondition(){
       return (NameExpr)super.getExpr();
   }
   
   public IRStatementList getStatements(){
       return (IRStatementList)super.getStmts();
   }
   
}





