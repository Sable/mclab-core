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

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;
import ast.*;

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
public class IRForStmt extends ForStmt implements IRStmt {
    public IRForStmt(Name var,Name low,Name inc,Name up,IRStatementList stmts){
        super(new AssignStmt(
                new NameExpr(var),
                new RangeExpr(
                        new NameExpr(low),
                        (inc==null)?new Opt<Expr>():new Opt<Expr>(new NameExpr(inc)),
                        new NameExpr(up))),
                stmts);
    }
    
    
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRForStmt(this);
    }
}


