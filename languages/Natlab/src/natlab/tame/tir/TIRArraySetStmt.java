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
 * statements of the form
 * 
 * v(i1,i2,i3,..,in) = t
 * 
 * where v is an array
 * @author ant6n
 */


public class TIRArraySetStmt extends TIRAbstractAssignFromVarStmt {
    private static final long serialVersionUID = 1L;

    public TIRArraySetStmt(Name array, TIRCommaSeparatedList indizes, Name rhs){
        super(rhs);
        setLHS(new ParameterizedExpr(new NameExpr(array),indizes));
    }
    
    public Name getArrayName(){
        return ((NameExpr)((ParameterizedExpr)getLHS()).getTarget()).getName();
    }
        
    public Name getValueName(){
        return ((NameExpr)getRHS()).getName();
    }
    
    public TIRCommaSeparatedList getIndizes(){
        return (TIRCommaSeparatedList)(((ParameterizedExpr)getLHS()).getArgList());
    }
    
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRArraySetStmt(this);
    }

}
