package natlab.tame.tir;

import ast.*;
import natlab.tame.tir.analysis.TIRNodeCaseHandler;

public class TIRDotGetStmt extends TIRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;

    
    public TIRDotGetStmt(TIRCommaSeparatedList lhs,Name var,Name field) {
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
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRDotGetStmt(this);
    }
}
