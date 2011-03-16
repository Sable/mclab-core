package natlab.Static.ir;

import java.util.ArrayList;

import natlab.toolkits.analysis.NodeCaseHandler;
import ast.*;

public abstract class IRAbstractAssignStmt extends AssignStmt implements IRStmt {
    public void analyze(NodeCaseHandler visitor) {
        // TODO Auto-generated method stub
        super.analyze(visitor);
    }

    
    public IRAbstractAssignStmt() {
        super();
    }
    
    /**
     * given a List<Expr>, returns a List<NameExpr> by casting
     * all the elements to NameExpr (i.e. they have to be NameExpr at runtime)
     * @return
     */
    protected static List<NameExpr> exprListToNameExprList(List<Expr> list){
        List<NameExpr> out = new List<NameExpr>();
        for (Expr e : list){
            out.add((NameExpr)e);
        }
        return out;
    }
    
    /**
     * given a List<Expr> which are just name expressions,
     * returns an ArrayList<String> which are the names
     */
    protected static ArrayList<String> exprListToStringList(List<Expr> args){
        ArrayList<String> list = new ArrayList<String>(args.getNumChild());
        for (Expr e : args){
            list.add(((NameExpr)e).getName().getID());
        }
        return list;
    }
    
    /**
     * given a List<NameExpr>, returns a List<Expr> which are the same
     */
    protected static List<Expr> nameExprListToExprList(List<NameExpr> list){
        List<Expr> out = new List<Expr>();
        for (NameExpr n : list) out.add(n);
        return out;
    }
}
