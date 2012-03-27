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
import ast.*;


/**
 * In the IR, the If statement has the form
 * 
 * if (var)
 *   ...
 * else
 *   ...
 * end
 * 
 * There will always only be one if block, and there will always be an else block
 * - which may be empty.
 */
public class TIRIfStmt extends IfStmt implements TIRStmt {

    private static final long serialVersionUID = 1L;

    public TIRIfStmt(Name conditionVar,TIRStatementList IfStmts,TIRStatementList ElseStmts){
        super(
                new List<IfBlock>().add(new IfBlock(new NameExpr(conditionVar),IfStmts)),
                new ast.Opt<ElseBlock>(new ElseBlock(ElseStmts))
        );
    }

    public Name getConditionVarName(){
         return ((NameExpr)(this.getIfBlock(0).getCondition())).getName();
    }
    
    public TIRStatementList getIfStameents(){
        return (TIRStatementList)(this.getIfBlock(0).getStmtList());
    }
    
    public TIRStatementList getElseStatements(){
        return (TIRStatementList)(this.getElseBlock().getStmtList());
    }

    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRIfStmt(this);
    }
}



