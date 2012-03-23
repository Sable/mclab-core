package natlab.tame.ir;

import natlab.tame.ir.analysis.IRNodeCaseHandler;
import ast.Name;
import ast.PersistentStmt;
/**
 * this exists mostly to have a complete IR.
 * In practice, symbol tables should provide this information
 * (i.e. in StaticFunction)
 * @author ant6n
 */

public class IRPersistentSmt extends PersistentStmt implements IRStmt {
    private static final long serialVersionUID = 1L;

    public IRPersistentSmt(ast.List<Name> names) {
        super( names);
    }
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRPersistentStmt(this);
    }
}
