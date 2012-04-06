package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.GlobalStmt;
import ast.Name;
/**
 * this exists mostly to have a complete IR.
 * In practice, symbol tables should provide this information
 * (i.e. in StaticFunction)
 * @author ant6n
 */

public class TIRGlobalStmt extends GlobalStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRGlobalStmt(ast.List<Name> names) {
        super( names);
    }
    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRGlobalStmt(this);
    }
}
