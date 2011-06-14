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

public class IRStatementList extends List<Stmt> implements IRNode {
    public IRStatementList(List<Stmt> list){
        super();
        for (Stmt s : list){
            add(s);
        }
    }

    public List<Stmt> add(IRStmt stmt){
        return super.add((Stmt)stmt);
    }
    
    
    @Override
    public List<Stmt> add(Stmt node) {
        /*
        if (!(node instanceof IRStmt)){
            throw new UnsupportedOperationException("attempting to put non IR stmt "
                    +(node.getClass().getName())+" in a IRStatementList");
        }*/
        return super.add(node);
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRStatementList(this);
    }

}
