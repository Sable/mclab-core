package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.ContinueStmt;

public class TIRContinueStmt extends ContinueStmt implements TIRStmt{
    private static final long serialVersionUID = 1L;

    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRContinueStmt(this);
    }
}
