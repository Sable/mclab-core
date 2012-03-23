package natlab.tame.ir;

import natlab.tame.ir.analysis.IRNodeCaseHandler;
import ast.BreakStmt;

public class IRBreakStmt extends BreakStmt implements IRStmt {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRBreakStmt(this);
    }
}
