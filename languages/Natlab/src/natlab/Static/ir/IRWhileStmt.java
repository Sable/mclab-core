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


public class IRWhileStmt extends WhileStmt implements IRStmt {
   public IRWhileStmt(Name condition,IRStatementList body) {
       super();
       this.setExpr(new NameExpr(condition));
       this.setStmtList(body);
   }
   
   public NameExpr getCondition(){
       return (NameExpr)super.getExpr();
   }
   
   public IRStatementList getStatements(){
       return (IRStatementList)super.getStmts();
   }
   
   
   
   @Override
   public void irAnalyize(IRNodeCaseHandler irHandler) {
       irHandler.caseIRWhileStmt(this);
   }
}





