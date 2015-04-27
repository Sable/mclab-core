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
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

/**
 * an array get is a an assignment statement, which has
 * a list of name expressions as the LHS and a parametric expression
 * as the right hand side, which in turn targets a name expression,
 * whose arguments are again a name expressions; i.e. it is
 * of the form
 * 
 * [n1,n2] = m(i1,i2,...)
 * 
 * Note that for actual matrix indexing operations, there's always
 * only one name on the left. Only if indexing is overloaded, or if
 * m is a function handle, is it possible to get multiple results on
 * the left.
 * 
 * TODO - deal with unparmetrized assignments
 * 
 * @author ant6n
 *
 */

public class TIRArrayGetStmt extends TIRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;

    public TIRArrayGetStmt(Name lhs,Name rhs,TIRCommaSeparatedList indizes){
        super(lhs);
        setRHS(new ParameterizedExpr(new NameExpr(rhs),indizes));
    }
    
    public Name getArrayName(){
        return ((NameExpr)(((ParameterizedExpr)getRHS())).getTarget()).getName();
    }
    
    
    public TIRCommaSeparatedList getIndices(){
        return (TIRCommaSeparatedList)(((ParameterizedExpr)getRHS()).getArgList());
    }
    
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRArrayGetStmt(this);
    }
}


