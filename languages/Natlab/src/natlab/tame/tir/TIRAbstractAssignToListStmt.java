// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.tir;
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
 * 
 * 
 */

public abstract class TIRAbstractAssignToListStmt extends TIRAbstractAssignStmt {
    private static final long serialVersionUID = 1L;

    public TIRAbstractAssignToListStmt(TIRCommaSeparatedList targets){
        super();
        //set lhs
        List<Row> rows = new List<Row>();
        rows.add(new Row(targets));
        setLHS(new MatrixExpr(rows));
        getTargets();
    }
    
    
    /**
     * special constructor if there is only one value on the right
     */
    public TIRAbstractAssignToListStmt(Name target){
        this(new TIRCommaSeparatedList(new NameExpr(target)));
    }

    
    
    @SuppressWarnings("unchecked")
    @Override
    public void setChild(ASTNode node, int i){
        if (i == 0 && !(node instanceof MatrixExpr)){
            throw new UnsupportedOperationException();
        }
        super.setChild(node, i);
    }
    
    
    /**
     * if the assigned LHS is not a matrix expression, put the expression into a matrix
     */
    @Override
    public void setLHS(Expr node){
        if (node instanceof MatrixExpr){
            super.setLHS(node);
        } else {
            List<Row> rows = new List<Row>();
            rows.add(new Row(new TIRCommaSeparatedList(node)));
            setLHS(new MatrixExpr(rows));
        }
    }
    
    /**
     * returns true if there is only one name target
     */
    public boolean isAssignToVar(){
        TIRCommaSeparatedList targets = getTargets();
        return targets.isAllNameExpressions() && targets.size() == 1;
    }
    
    /**
     * returns the single target name, if isAssignToVar is true. Otherwise, the
     * result is undefined and may result in an exception.
     */
    public Name getTargetName(){
        return ((NameExpr)(getTargets().getChild(0))).getName();
    }
    
    /**
     * get targets
     * @return
     */
    public TIRCommaSeparatedList getTargets(){
        return (TIRCommaSeparatedList)((((MatrixExpr)getLHS()).getRow(0)).getElementList());
    }
    
    /**
     * get number of targets
     */
    public int getNumTargets(){
        return (((MatrixExpr)getLHS()).getRow(0)).getNumElement();
    }
    
    
    
    public String getPrettyPrintedLessComments(){
        if (this.getNumTargets() > 0){
            return super.getPrettyPrintedLessComments();
        } else {
            return this.getIndent()
                  //+getTargets().getChild(0).getPrettyPrintedLessComments()
                  //+" = "
                  +this.getRHS().getPrettyPrintedLessComments()
                  +(isOutputSuppressed()?";":"")
                  +" % [] = ...";
        }
    }
    
    
}
