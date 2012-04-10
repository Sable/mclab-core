package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.Name;
import ast.NameExpr;

/**
 * statements of the form
 * u = t
 */
public class TIRCopyStmt extends TIRAbstractAssignToVarStmt {
    private static final long serialVersionUID = 1L;

    public TIRCopyStmt(Name lhs, Name rhs) {
        super(lhs);
        this.setRHS(new NameExpr(rhs));
    }
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRCopyStmt(this);
    }
    
    public Name getSourceName(){
        return ((NameExpr)(getRHS())).getName();
    }

}
