package natlab.tame.ir;

import natlab.tame.ir.analysis.IRNodeCaseHandler;
import ast.ContinueStmt;

public class IRContinueStmt extends ContinueStmt implements IRStmt{
    private static final long serialVersionUID = 1L;

    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRContinueStmt(this);
    }
}
