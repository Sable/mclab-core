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
import ast.CellIndexExpr;
import ast.Expr;
import ast.Name;
import ast.NameExpr;

/**
 * a cell array get is of the form
 * 
 * [lhs1,lhs2,...] = c{arg1,arg2,...}
 * 
 * 
 * @author ant6n
 *
 */
public class TIRCellArrayGetStmt extends TIRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;
    
    public TIRCellArrayGetStmt(Name cell,Expr target,Expr... indizes) {
        this(new NameExpr(cell),
                new TIRCommaSeparatedList(target),new TIRCommaSeparatedList(indizes));
    }
    
    
    public TIRCellArrayGetStmt(NameExpr cell,TIRCommaSeparatedList targets,TIRCommaSeparatedList indizes) {
        //set lhs
        super(targets);
        
        //set rhs
        setRHS(new CellIndexExpr(cell, indizes));
    }
    
    public Name getCellArrayName(){
        return ((NameExpr)(((CellIndexExpr)getRHS()).getTarget())).getName();
    }
    
    //get arguments
    public TIRCommaSeparatedList getIndices(){
         return (TIRCommaSeparatedList)(((CellIndexExpr)getRHS()).getArgList());
    }    

    /**
     * deprecated, use getIndices() instead
     */
    @Deprecated
    public TIRCommaSeparatedList getArguments(){
        return (TIRCommaSeparatedList)(((CellIndexExpr)getRHS()).getArgList());
   }    

    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRCellArrayGetStmt(this);
    }

}



