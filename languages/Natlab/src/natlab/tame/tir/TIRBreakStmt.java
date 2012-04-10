package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.BreakStmt;

public class TIRBreakStmt extends BreakStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRBreakStmt(this);
    }
}
