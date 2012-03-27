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

public class TIRStatementList extends List<Stmt> implements TIRNode {
    private static final long serialVersionUID = 1L;


    public TIRStatementList(List<Stmt> list){
        super();
        for (Stmt s : list){
            add(s);
        }
    }

    public List<Stmt> add(TIRStmt stmt){
        return super.add((Stmt)stmt);
    }
    
    
    @Override
    public List<Stmt> add(Stmt node) {
        if (!(node instanceof TIRStmt)){
            throw new UnsupportedOperationException("attempting to put non IR stmt "
                    +(node.getClass().getName())+":\n"+node.getPrettyPrinted()+" in a TIRStatementList");
        }
        return super.add(node);
    }
    
    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRStatementList(this);
    }

}
