package natlab.Static.ir;
import ast.*;

/**
 * assignments of the form
 * t = @foo
 * where foo is a name
 * 
 */

public class IRAssignFunctionHandleStmt extends IRAbstractAssignToVarStmt {
    public IRAssignFunctionHandleStmt(Name lhs,Name function) {
        super(new NameExpr(lhs));
        FunctionHandleExpr fHandleExpr = new FunctionHandleExpr(function);
        setRHS(fHandleExpr);
    }
    
    public Name getFunction(){
        return ((FunctionHandleExpr)getRHS()).getName();
    }
}

