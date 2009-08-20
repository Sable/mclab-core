package natlab.toolkits.analysis.functionhandle;

import natlab.FlowAnalysisTestTool;
import natlab.ast.*;
import natlab.toolkits.analysis.*;

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
    	currentOutSet = new VariableEntryFlowSet();
		copy(currentInSet, currentOutSet);
		//find the new variable entry
		String name = node.getLHS().getPrettyPrinted();
				
		VariableEntry var = new VariableEntry(name);

		
		
		currentOutSet.addNew(var);
		
		
		//end of find new var entry
		System.out.println(currentInSet);
		System.out.println(currentOutSet);
		System.out.println("bla");
		outFlowSets.put(node, currentOutSet);
		inFlowSets.put(node, currentInSet);
    }    
    
    
	@Override
	public void caseCondition(Expr condExpr) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsCondition(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsInit(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsUpdate(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}
	
	@Override
	public void copy(VariableEntryFlowSet source, VariableEntryFlowSet dest) {
		source.copy(dest);
	}
	
	@Override
	public void merge(VariableEntryFlowSet in1, VariableEntryFlowSet in2,
			VariableEntryFlowSet out) {
		VariableEntryFlowSet out2 = new VariableEntryFlowSet();
		copy(in1,out2);
		out2.union(in2);
		copy(out2,out);
	}

	@Override
	public VariableEntryFlowSet newInitialFlow() {
		return new VariableEntryFlowSet();
	}
}
