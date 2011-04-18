package natlab.Static.ir;

import java.util.Map;

import natlab.Static.toolkits.analysis.IRNodeCaseHandler;

import ast.*;

public class IRFunction extends Function implements IRNode {
    
    public IRFunction(List<Name> outputParams,String name,List<Name> inputParams,
            List<HelpComment> helpComments,IRStatementList stmts,List<IRFunction> nestedFunctions){
        super(outputParams,name,inputParams,helpComments,stmts,makeNestedFunctilnList(nestedFunctions));
    }
    static private List<Function> makeNestedFunctilnList(List<IRFunction> nestedFunctions){
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
