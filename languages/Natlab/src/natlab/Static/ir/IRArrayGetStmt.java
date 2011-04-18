package natlab.Static.ir;

import java.util.ArrayList;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import ast.*;

/**
 * an array get is a an assignment statement, which has
 * a name expression as the LHS and a parametric expression
 * as the right hand side, which in turn targets a name expression,
 * whose arguments are again a name expressions; i.e. it is
 * of the form
 * 
 * n = m(i1,i2,...)
 * 
 * @author ant6n
 *
 */

public class IRArrayGetStmt extends IRAbstractAssignToVarStmt {
    public IRArrayGetStmt(NameExpr lhs,NameExpr rhs,IRCommaSeparatedList indizes){
        super(lhs);
        setRHS(new ParameterizedExpr(rhs,indizes));
    }
    
    public String getArrayName(){
        return ((NameExpr)(((ParameterizedExpr)getLHS())).getTarget()).getName().getID();
    }
    public NameExpr getArrayNameExpr(){
        return (NameExpr)(((ParameterizedExpr)getLHS()).getTarget());
    }
    
    public IRCommaSeparatedList getIndizes(){
        return (IRCommaSeparatedList)(((ParameterizedExpr)getLHS()).getArgList());
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRArrayGetStmt(this);
    }
}


