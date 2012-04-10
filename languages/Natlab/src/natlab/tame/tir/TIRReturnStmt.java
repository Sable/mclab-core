package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.ReturnStmt;

public class TIRReturnStmt extends ReturnStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRReturnStmt(this);
    }
}
