package natlab.Static.ir;

import ast.*;
import natlab.Static.ir.analysis.IRNodeCaseHandler;

//TODO give cell/array/det set a single parent '... = t' (assign from var?)
// - alternative: remove altogether, and have one complex assign (?)
public class IRDotSetStmt extends IRAbstractAssignFromVarStmt{
    private static final long serialVersionUID = 1L;

    public IRDotSetStmt(Name dot, Name field, Name rhs) {
        super(rhs);
        this.setLHS(new DotExpr(new NameExpr(dot),field));
    }

    public Name getDotName(){
        return ((NameExpr)(((DotExpr)getLHS()).getTarget())).getName();
    }
    public Name getFieldName(){
        return ((DotExpr)getLHS()).getField();
    }
    
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRDotSetStmt(this);
    }
}
