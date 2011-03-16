package natlab.Static.ir;
import ast.*;

/**
 * assignments of the form
 * t = x
 * where x is a constant...
 * 
 */

public class IRAssignLiteralStmt extends IRAbstractAssignToVarStmt {
    public IRAssignLiteralStmt(NameExpr lhs,LiteralExpr rhs) {
        super(lhs);
        setRHS(rhs);
    }
    
}

