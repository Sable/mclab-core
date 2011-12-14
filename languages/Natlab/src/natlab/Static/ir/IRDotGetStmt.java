package natlab.Static.ir;

import ast.*;
import natlab.Static.ir.analysis.IRNodeCaseHandler;

public class IRDotGetStmt extends IRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;

    
    public IRDotGetStmt(IRCommaSeparatedList lhs,Name var,Name field) {
        super(lhs);
        this.setRHS(new DotExpr(new NameExpr(var),field));
    }
    
    public Name getDotName(){
        return ((NameExpr)((DotExpr)this.getRHS()).getTarget()).getName();
    }
    
    public Name getFieldName(){
        return (((DotExpr)this.getRHS()).getField());
    }
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRDotGetStmt(this);
    }
}
