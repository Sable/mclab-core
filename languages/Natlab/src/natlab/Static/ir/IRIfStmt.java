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

package natlab.Static.ir;

import natlab.Static.ir.analysis.IRNodeCaseHandler;
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
public class IRIfStmt extends IfStmt implements IRStmt {

    private static final long serialVersionUID = 1L;

    public IRIfStmt(Name conditionVar,IRStatementList IfStmts,IRStatementList ElseStmts){
        super(
                new List<IfBlock>().add(new IfBlock(new NameExpr(conditionVar),IfStmts)),
                new ast.Opt<ElseBlock>(new ElseBlock(ElseStmts))
        );
    }

    public Name getConditionVarName(){
         return ((NameExpr)(this.getIfBlock(0).getCondition())).getName();
    }
    
    public IRStatementList getIfStameents(){
        return (IRStatementList)(this.getIfBlock(0).getStmtList());
    }
    
    public IRStatementList getElseStatements(){
        return (IRStatementList)(this.getElseBlock().getStmtList());
    }

    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRIfStmt(this);
    }
}



