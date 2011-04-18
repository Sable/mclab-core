package natlab.Static.ir;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import ast.*;

/**
 * statements of the form
 * 
 * v(i1,i2,i3,..,in) = t
 * 
 * where v is an array
 * @author ant6n
 */


public class IRArraySetStmt extends IRAbstractAssignStmt {
    public IRArraySetStmt(NameExpr array, IRCommaSeparatedList indizes,NameExpr rhs){
        super();
        setLHS(new ParameterizedExpr(array,indizes));
        setRHS(rhs);
    }
    
    public NameExpr getArray(){
        return (NameExpr)((ParameterizedExpr)getLHS()).getTarget();
    }
    
    public String getArrayName(){
        return getArray().getName().getID();
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRArraySetStmt(this);
    }

}
