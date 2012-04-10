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
import ast.WhileStmt;

/**
 * while loop of the form
 * 
 * while(t)
 *   body
 *   
 * end
 * 
 * where t is a variable (nameExpression)
 * 
 * @author ant6n
 *
 */


public class TIRWhileStmt extends WhileStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRWhileStmt(Name condition,TIRStatementList body) {
        super();
        this.setExpr(new NameExpr(condition));
        this.setStmtList(body);
    }

    public NameExpr getCondition(){
        return (NameExpr)super.getExpr();
    }

    public TIRStatementList getStatements(){
        return (TIRStatementList)super.getStmts();
    }

    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRWhileStmt(this);
    }
}
