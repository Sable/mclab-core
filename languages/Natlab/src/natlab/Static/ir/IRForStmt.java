package natlab.Static.ir;

import ast.*;

/**
 * IR For statement is of the form
 * 
 * 
 * for i = low:inc:up
 *   ...
 * end
 * 
 * where i,low,inc,up are variables, inc is optional - it may be null
 *
 * @author ant6n
 */
public class IRForStmt extends ForStmt implements IRStmt {
    public IRForStmt(Name var,Name low,Name inc,Name up,IRStatementList stmts){
        super(new AssignStmt(
                new NameExpr(var),
                new RangeExpr(
                        new NameExpr(low),
                        (inc==null)?new Opt<Expr>():new Opt<Expr>(new NameExpr(inc)),
                        new NameExpr(up))),
                stmts);
    }

}


