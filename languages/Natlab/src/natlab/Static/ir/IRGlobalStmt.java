package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;
import ast.Name;
import ast.GlobalStmt;;
/**
 * this exists mostly to have a complete IR.
 * In practice, symbol tables should provide this information
 * (i.e. in StaticFunction)
 * @author ant6n
 */

public class IRGlobalStmt extends GlobalStmt implements IRStmt {
    private static final long serialVersionUID = 1L;

    public IRGlobalStmt(ast.List<Name> names) {
        super( names);
    }
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRGlobalStmt(this);
    }
}
