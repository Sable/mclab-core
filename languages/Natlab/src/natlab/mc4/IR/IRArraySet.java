package natlab.mc4.IR;

import ast.*;

/**
 * statements of the form
 * 
 * v(i1,i2,i3,..,in) = t
 * 
 * where v is an array
 * @author ant6n
 */


public class IRArraySet extends IRAbstractAssignStmt {
    IRArraySet(NameExpr array, IRCommaSeparatedlist indizes,NameExpr rhs){
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
}
