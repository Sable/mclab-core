package natlab.Static.ir;
import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
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
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRAssignLiteralStmt(this);
    }
}

