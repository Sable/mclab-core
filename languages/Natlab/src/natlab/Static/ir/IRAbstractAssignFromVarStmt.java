package natlab.Static.ir;

import ast.*;
import natlab.Static.ir.analysis.IRNodeCaseHandler;

abstract public class IRAbstractAssignFromVarStmt extends IRAbstractAssignStmt {
    private static final long serialVersionUID = 1L;

    public IRAbstractAssignFromVarStmt(Name rhs) {
        super();
        setRHS(new NameExpr(rhs));
    }

    /**
     * returns the rhs name
     */
    public Name getValueName(){
        return ((NameExpr)getRHS()).getName();
    }
}
