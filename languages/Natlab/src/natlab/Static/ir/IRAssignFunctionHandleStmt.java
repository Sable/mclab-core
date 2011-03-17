package natlab.Static.ir;
import ast.*;

/**
 * assignments of the form
 * t = @foo
 * where foo is a name
 * 
 */

public class IRAssignFunctionHandleStmt extends IRAbstractAssignToVarStmt {
    public IRAssignFunctionHandleStmt(NameExpr lhs,NameExpr function) {
        super(lhs);
        FunctionHandleExpr fHandleExpr = new FunctionHandleExpr(function.getName());
        setRHS(fHandleExpr);
    }
    
    public Name getFunction(){
        return ((FunctionHandleExpr)getRHS()).getName();
    }
}

