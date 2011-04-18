package natlab.Static.ir;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import ast.*;

/**
 * statements of the form
 * 
 * v{i1,i2,i3,..,in} = t;
 * 
 * where v is a cell array
 */


public class IRCellArraySet extends IRAbstractAssignStmt {
    public IRCellArraySet(NameExpr array, IRCommaSeparatedList indizes,NameExpr rhs){
        super();
        setLHS(new CellIndexExpr(array,indizes));
        setRHS(rhs);
    }
    
    public NameExpr getCellArray(){
        return (NameExpr)((CellIndexExpr)getLHS()).getTarget();
    }
    
    public String getCellArrayName(){
        return getCellArray().getName().getID();
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCellArraySetStmt(this);
    }

}
