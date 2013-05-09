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

import java.util.Map;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.Function;
import ast.HelpComment;
import ast.List;
import ast.Name;

public class TIRFunction extends Function implements TIRNode {
    private static final long serialVersionUID = 1L;
    
    public TIRFunction(List<Name> outputParams,String name,List<Name> inputParams,
            List<HelpComment> helpComments,TIRStatementList stmts,List<TIRFunction> nestedFunctions){
        super(outputParams,name,inputParams,helpComments,stmts,makeNestedFunctionList(nestedFunctions));
    }
    //helper method for above constructor
    static private List<Function> makeNestedFunctionList(List<TIRFunction> nestedFunctions){
        List<Function> list = new List<Function>();
        if (nestedFunctions == null) return list;
        for (TIRFunction f : nestedFunctions){
            list.add(f);
        }
        return list;
    }

    @Override
    public TIRStatementList getStmtList() {
        return (TIRStatementList)super.getStmtList();
    }
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRFunction(this);
    }

}
