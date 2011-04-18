package natlab.Static.ir;

import java.util.ArrayList;



import ast.*;

/**
 * nodes of the form
 * [t1,t2,..] = ...
 *
 * Note that with 0 variables on the left hand side, this becomes
 * [] = ...
 * which is invalid Matlab.
 * The pretty print method is overriden to still produce valid matlab code,
 * but analyses should be aware of this.
 */

public abstract class IRAbstractAssignToListStmt extends IRAbstractAssignStmt {
    public IRAbstractAssignToListStmt(IRCommaSeparatedList targets) {
        super();
        //set lhs
        List<Row> rows = new List<Row>();
        rows.add(new Row(targets));
        setLHS(new MatrixExpr(rows));
    }


    @Override
    public void setChild(ASTNode node, int i) {
        if (i == 0 && !(node instanceof MatrixExpr)){
            throw new UnsupportedOperationException();
        }
        super.setChild(node, i);
    }
    
    
    /**
     * if the assigned LHS is not a matrix expression, put the expression into a matrix
     */
    @Override
    public void setLHS(Expr node) {
        if (node instanceof MatrixExpr){
            super.setLHS(node);
        } else {
            List<Row> rows = new List<Row>();
            rows.add(new Row((new List<Expr>()).add(node)));
            setLHS(new MatrixExpr(rows));            
        }
    }
    
    /**
     * get targets
     * @return
     */
    public IRCommaSeparatedList getTargets(){
        return (IRCommaSeparatedList)((((MatrixExpr)getLHS()).getRow(0)).getElementList());
    }
    
    /**
     * get number of targets
     */
    public int getNumTargets(){
        return (((MatrixExpr)getLHS()).getRow(0)).getNumElement();
    }
    
    
    
    public String getPrettyPrintedLessComments() {
        if (this.getNumTargets() > 0){
            return super.getPrettyPrintedLessComments();
        } else {
            return this.getIndent()
                  +getTargets().getChild(0).getPrettyPrintedLessComments()
                  +" = "
                  +this.getRHS().getPrettyPrintedLessComments()
                  +(isOutputSuppressed()?";":"");
        }
    }
    
    
}
