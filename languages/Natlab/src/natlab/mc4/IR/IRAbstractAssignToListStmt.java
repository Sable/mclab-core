package natlab.mc4.IR;

import java.util.ArrayList;


import ast.*;

/**
 * nodes of the form
 * t = ...
 *
 * Note that this does not include calls
 */

public class IRAbstractAssignToListStmt extends IRAbstractAssignStmt {
    public IRAbstractAssignToListStmt(IRCommaSeparatedList targets) {
        super();
        //set lhs
        List<Row> rows = new List<Row>();
        rows.add(new Row(targets));
        setLHS(new MatrixExpr(rows));
    }


    //get targets
    public IRCommaSeparatedList getTargets(){
        return (IRCommaSeparatedList)((((MatrixExpr)getLHS()).getRow(0)).getElementList());
    }
}
