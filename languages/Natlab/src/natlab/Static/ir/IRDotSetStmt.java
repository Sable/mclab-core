package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;

public class IRDotSetStmt extends IRAbstractAssignStmt{
    private static final long serialVersionUID = 1L;


    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRDotSetStmt(this);
    }
}
