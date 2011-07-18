package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;

public class IRDotGetStmt extends IRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;


    public IRDotGetStmt() {
        super(null);
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRDotGetStmt(this);
    }
}
