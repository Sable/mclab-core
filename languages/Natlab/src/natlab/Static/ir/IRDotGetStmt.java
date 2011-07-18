package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;

public class IRDotGetStmt extends IRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;

    
    public IRDotGetStmt(IRCommaSeparatedList targets) {
        super(targets);
        // TODO Auto-generated constructor stub
    }


    //TODO    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRDotGetStmt(this);
    }
}
