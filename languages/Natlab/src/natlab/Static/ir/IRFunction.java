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

import java.util.Map;

import natlab.Static.ir.analysis.IRNodeCaseHandler;

import ast.*;

public class IRFunction extends Function implements IRNode {
    private static final long serialVersionUID = 1L;
    
    public IRFunction(List<Name> outputParams,String name,List<Name> inputParams,
            List<HelpComment> helpComments,IRStatementList stmts,List<IRFunction> nestedFunctions){
        super(outputParams,name,inputParams,helpComments,stmts,makeNestedFunctionList(nestedFunctions));
    }
    //helper method for above constructor
    static private List<Function> makeNestedFunctionList(List<IRFunction> nestedFunctions){
        List<Function> list = new List<Function>();
        if (nestedFunctions == null) return list;
        for (IRFunction f : nestedFunctions){
            list.add(f);
        }
        return list;
    }
    
    // *** getter methods ********************************************************
    @Override
    public IRStatementList getStmtList() {
        // TODO Auto-generated method stub
        return (IRStatementList)super.getStmtList();
    }
    
    
     @Override
    public Map<String, Function> getNested() {
        // TODO Auto-generated method stub
        return super.getNested();
    }
     
    @Override
    public Function getNestedFunction(int i) {
        // TODO Auto-generated method stub
        return super.getNestedFunction(i);
    }
    
    @Override
    public List<Function> getNestedFunctionList() {
        // TODO Auto-generated method stub
        return super.getNestedFunctionList();
    }
    
    @Override
    public List<Function> getNestedFunctions() {
        // TODO Auto-generated method stub
        return super.getNestedFunctions();
    }
    
    //*** setter methods ***********************************************************
    
    
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRFunction(this);
    }

}
