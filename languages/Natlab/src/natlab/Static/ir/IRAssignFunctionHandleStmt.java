package natlab.Static.ir;
import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import natlab.toolkits.analysis.NodeCaseHandler;
import ast.*;

/**
 * assignments of the form
 * t = @foo
 * where foo is a name
 * 
 */

public class IRAssignFunctionHandleStmt extends IRAbstractAssignToVarStmt {
    public IRAssignFunctionHandleStmt(Name lhs,Name function) {
        super(new NameExpr(lhs));
        FunctionHandleExpr fHandleExpr = new FunctionHandleExpr(function);
        setRHS(fHandleExpr);
    }
    
    public Name getFunction(){
        return ((FunctionHandleExpr)getRHS()).getName();
    }
    

    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRAssignFunctionHandleStmt(this);
    }

}

