package natlab.toolkits.analysis.functionhandle;

import natlab.FlowAnalysisTestTool;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.FunctionHandleExpr;
import ast.LambdaExpr;

import com.google.common.collect.Sets;

public class SimpleFunctionHandleAnalysis extends
		AbstractSimpleStructuralForwardAnalysis<VariableEntryFlowSet> {
	
	public static void main(String[] args) throws Exception {
		String source = "D:/Classes/McLab/matlabfiddle/small/test2.m";
		FlowAnalysisTestTool tool = new FlowAnalysisTestTool(source,SimpleFunctionHandleAnalysis.class);
		System.out.println(tool.run());
	}
	
	
    public SimpleFunctionHandleAnalysis(ASTNode tree){
        super( tree );
    }
	
    
    @Override
    public void caseAssignStmt(AssignStmt node) {
        currentOutSet = copy(currentInSet);
		//find the new variable entry
		String name = node.getLHS().getPrettyPrinted();
		Expr expr = node.getRHS();
		VariableEntry var;
		if (expr instanceof LambdaExpr){
			System.out.println("found lambda!");
			var = new VariableEntry(name,new FunctionReference((LambdaExpr)expr));
			currentOutSet.addNew(var);
		}
		if (expr instanceof FunctionHandleExpr){
			System.out.println("found fhandle!");
			var = new VariableEntry(name,new FunctionReference((FunctionHandleExpr)expr));
			currentOutSet.addNew(var);
		}
		
		//end of find new var entry
		System.out.println(currentInSet);
		System.out.println(currentOutSet);
		System.out.println("bla");
		outFlowSets.put(node, currentOutSet);
		inFlowSets.put(node, currentInSet);
    }    
    
    
	@Override
	public void caseCondition(Expr condExpr) {
	  currentOutSet = copy(currentInSet);
	}

	@Override
	public void caseLoopVarAsCondition(AssignStmt loopVar) {
	  currentOutSet = copy(currentInSet);
	}

	@Override
	public void caseLoopVarAsInit(AssignStmt loopVar) {
	  currentOutSet = copy(currentInSet);
	}

	@Override
	public void caseLoopVarAsUpdate(AssignStmt loopVar) {
	  currentOutSet = copy(currentInSet);
	}
	
	@Override
	public VariableEntryFlowSet copy(VariableEntryFlowSet source) {
		return (VariableEntryFlowSet) Sets.newHashSet(source);
	}
	
	@Override
	public VariableEntryFlowSet merge(VariableEntryFlowSet in1, VariableEntryFlowSet in2) {
	  return (VariableEntryFlowSet) Sets.newHashSet(Sets.union(in1, in2));
	}

	@Override
	public VariableEntryFlowSet newInitialFlow() {
		return new VariableEntryFlowSet();
	}
}
