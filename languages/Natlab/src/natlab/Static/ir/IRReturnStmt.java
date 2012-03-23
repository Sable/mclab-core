package natlab.tame.ir;

import natlab.tame.ir.analysis.IRNodeCaseHandler;
import ast.ReturnStmt;

public class IRReturnStmt extends ReturnStmt implements IRStmt {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRReturnStmt(this);
    }
}
