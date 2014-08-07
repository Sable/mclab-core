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

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.Name;
import ast.NameExpr;
import ast.Opt;
import ast.RangeExpr;

/**
 * IR For statement is of the form
 * 
 * 
 * for i = low:inc:up
 *   ...
 * end
 * 
 * where i,low,inc,up are variables, inc is optional - it may be null
 *
 * @author ant6n
 */
public class TIRForStmt extends ForStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRForStmt(Name var,Name lower,Name inc,Name upper,TIRStatementList stmts){
      this(var, lower, inc, upper, stmts, false);
    }


    public TIRForStmt(Name var,Name lower,Name inc,Name upper,TIRStatementList stmts,
                      boolean isParfor) {
      super(new AssignStmt(
              new NameExpr(var),
              new RangeExpr(
                  new NameExpr(lower),
                  (inc==null)?new Opt<Expr>():new Opt<Expr>(new NameExpr(inc)),
                  new NameExpr(upper))),
          stmts, isParfor);
    }

    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRForStmt(this);
    }
    

    /**
     * returns the name of the loopvar in the loop header's 'for loopvar = low:inc:high'
     */
    public Name getLoopVarName(){
        return ((NameExpr)(getAssignStmt().getLHS())).getName();
    }
    
    /**
     * returns the name of the low in the loop header's 'for loopvar = low:inc:high'
     */
    public Name getLowerName(){
        return ((NameExpr)(((RangeExpr)(getAssignStmt().getRHS())).getLower())).getName();
    }

    /**
     * returns the name of the inc in the loop header's 'for loopvar = low:inc:high',
     * or null if it doesn't exist
     */
    public Name getIncName(){
        if (!hasIncr()) return null;
        return ((NameExpr)(((RangeExpr)(getAssignStmt().getRHS())).getIncr())).getName();
    }

    /**
     * returns the name of the low in the loop header's 'for loopvar = low:inc:high'
     */
    public Name getUpperName(){
        return ((NameExpr)(((RangeExpr)(getAssignStmt().getRHS())).getUpper())).getName();
    }
    
    /**
     * returns true if the loop header's range expression has an increment value
     */
    public boolean hasIncr(){
         return ((RangeExpr)(getAssignStmt().getRHS())).hasIncr();
    }
    
    public TIRStatementList getStatements(){
        return (TIRStatementList)getStmtList();
    }
}


