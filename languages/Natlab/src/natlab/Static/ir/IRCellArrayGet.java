package natlab.Static.ir;

import java.util.ArrayList;


import ast.*;

/**
 * a cell array get is of the form
 * 
 * [lhs1,lhs2,...] = c{arg1,arg2,...}
 * 
 * 
 * @author ant6n
 *
 */
public class IRCellArrayGet extends IRAbstractAssignToListStmt {
    public IRCellArrayGet(Name cell,Expr target,Expr... indizes) {
        this(new NameExpr(cell),
                new IRCommaSeparatedList(target),new IRCommaSeparatedList(indizes));
    }
    
    
    public IRCellArrayGet(NameExpr cell,IRCommaSeparatedList targets,IRCommaSeparatedList indizes) {
        //set lhs
        super(targets);
        
        //set rhs
        setRHS(new CellIndexExpr(cell, indizes));
    }
    
    //function name get
    public NameExpr getCellNameExpr(){
        return ((NameExpr)(((CellIndexExpr)getRHS()).getTarget()));
    }
    public String getFunctionName(){
        return getCellNameExpr().getName().getID();
    }
        
    //get arguments
    public IRCommaSeparatedList getArguments(){
         return (IRCommaSeparatedList)(((CellIndexExpr)getRHS()).getArgList());
    }    
}



