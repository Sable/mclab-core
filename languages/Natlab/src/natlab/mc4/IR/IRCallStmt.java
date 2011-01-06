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
public class IRCallStmt extends IRAbstractAssignToListStmt {
    public IRCallStmt(NameExpr function,IRCommaSeparatedList targets,IRCommaSeparatedList args) {
        //set lhs
        super(targets);
        
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
        
    //get arguments
    public IRCommaSeparatedList getArguments(){
         return (IRCommaSeparatedList)(((ParameterizedExpr)getRHS()).getArgList());
    }

}



