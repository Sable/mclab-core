package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;
import ast.ReturnStmt;

public class IRReturnStmt extends ReturnStmt implements IRStmt {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRReturnStmt(this);
    }
}
