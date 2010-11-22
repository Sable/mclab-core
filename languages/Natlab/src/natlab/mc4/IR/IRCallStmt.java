package natlab.mc4.IR;

import java.util.ArrayList;
import ast.*;

/**
 * a call is of the form
 * 
 * [lhs1,lhs2,...] = f(arg1,arg2,...)
 * 
 * where the number of left-hand-side and right-hand-side variables
 * is 0 or more
 * @author ant6n
 *
 */
public class IRCallStmt extends IRAbstractAssignStmt {
    public IRCallStmt(NameExpr function,IRCommaSeparatedlist targets,IRCommaSeparatedlist args) {
        super();
        //set lhs
        List<Row> rows = new List<Row>();
        rows.add(new Row((targets)));
        setLHS(new MatrixExpr(rows));
        
        //set rhs
        setRHS(new ParameterizedExpr(function, args));
    }
    
    //function name get
    public NameExpr getFunctionNameExpr(){
        return ((NameExpr)(((ParameterizedExpr)getRHS()).getTarget()));
    }
    public String getFunctionName(){
        return getFunctionNameExpr().getName().getID();
    }
    
    //get targets
    public IRCommaSeparatedlist getTargets(){
        ArrayList<String> list = new ArrayList<String>();
        for (Expr e : ((MatrixExpr)getLHS()).getRow(0).getElementList()){
            list.add(((NameExpr)e).getName().getID());
        }
        return (IRCommaSeparatedlist)(((MatrixExpr)getLHS()).getRow(0).getElementList());
    }
    
    //get arguments
    public IRCommaSeparatedlist getArguments(){
         return (IRCommaSeparatedlist)(((ParameterizedExpr)getRHS()).getArgList());
    }

}



