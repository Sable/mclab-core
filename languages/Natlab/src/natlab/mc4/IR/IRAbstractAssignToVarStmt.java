package natlab.mc4.IR;
import ast.*;


public class IRAbstractAssignToVarStmt extends IRAbstractAssignStmt {
    public IRAbstractAssignToVarStmt(NameExpr lhs) {
        super();
        setLHS(lhs);
    }
    
    public NameExpr getTarget(){
        return (NameExpr)getRHS();
    }
}
