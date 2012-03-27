package natlab.tame.tir;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.Name;
import ast.PersistentStmt;
/**
 * this exists mostly to have a complete IR.
 * In practice, symbol tables should provide this information
 * (i.e. in StaticFunction)
 * @author ant6n
 */

public class TIRPersistentSmt extends PersistentStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRPersistentSmt(ast.List<Name> names) {
        super( names);
    }
    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRPersistentStmt(this);
    }
}
