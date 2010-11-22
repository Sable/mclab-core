package natlab.mc4.IR;
import ast.*;

/**
 * assignments of the form
 * t = x
 * where x is a constant...
 * 
 */

public class IRSetConstantStmt extends IRAbstractAssignToVarStmt {
    public IRSetConstantStmt(NameExpr lhs,Expr rhs) {
        super(lhs);
        setRHS(rhs);
    }
    
}

