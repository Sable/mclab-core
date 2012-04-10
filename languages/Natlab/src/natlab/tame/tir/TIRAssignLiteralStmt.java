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
import ast.LiteralExpr;
import ast.Name;

/**
 * assignments of the form
 * t = x
 * where x is a constant...
 * 
 */

public class TIRAssignLiteralStmt extends TIRAbstractAssignToVarStmt {
    private static final long serialVersionUID = 1L;
    
    public TIRAssignLiteralStmt(Name lhs,LiteralExpr rhs) {
        super(lhs);
        setRHS(rhs);
    }
    
    /**
     * returns the rhs literal
     */
    public LiteralExpr getRHS(){
        return (LiteralExpr)super.getRHS();
    }
    
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRAssignLiteralStmt(this);
    }
}

