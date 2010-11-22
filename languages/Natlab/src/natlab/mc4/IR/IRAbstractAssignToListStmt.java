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
    public IRAbstractAssignToListStmt(IRCommaSeparatedlist targets) {
        super();
        //set lhs
        List<Row> rows = new List<Row>();
        rows.add(new Row(targets));
        setLHS(new MatrixExpr(rows));
    }


    //get targets
    public IRCommaSeparatedlist getTargets(){
        return (IRCommaSeparatedlist)((((MatrixExpr)getLHS()).getRow(0)).getElementList());
    }
}
