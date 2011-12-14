package natlab.Static.ir;

import ast.*;
import natlab.Static.ir.analysis.IRNodeCaseHandler;

/**
 * statements of the form
 * u = t
 */
public class IRCopyStmt extends IRAbstractAssignToVarStmt {

    public IRCopyStmt(Name lhs, Name rhs) {
        super(lhs);
        this.setRHS(new NameExpr(rhs));
    }
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCopyStmt(this);
    }
    
    public Name getSourceName(){
        return ((NameExpr)(getRHS())).getName();
    }

}
