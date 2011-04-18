package natlab.Static.ir;
import ast.*;


public abstract class IRAbstractAssignToVarStmt extends IRAbstractAssignStmt {
    public IRAbstractAssignToVarStmt(NameExpr lhs) {
        super();
        setLHS(lhs);
    }
    
    public NameExpr getTarget(){
        return (NameExpr)getRHS();
    }
}
