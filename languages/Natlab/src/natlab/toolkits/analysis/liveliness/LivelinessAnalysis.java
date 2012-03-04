package natlab.toolkits.analysis.liveliness;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.*;
import analysis.*;
import analysis.natlab.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.ValueDatumPair;
import natlab.toolkits.utils.NodeFinder;

public class LivelinessAnalysis extends
		AbstractStructuralBackwardAnalysis<HashSetFlowSet<String>> implements FixedPointScriptAnalysis<HashSetFlowSet<String>> {

	public LivelinessAnalysis(ASTNode tree) {
		super(tree);	
	}	
	
	@Override
	public void caseStmt(Stmt s) {
		outFlowSets.put(s, currentOutSet.copy());
		caseASTNode(s);
		inFlowSets.put(s, currentOutSet.copy());

	}
	
	private NameExpr getLValue(Expr lv) {
		NameExpr ne = null;
		if (lv instanceof ParameterizedExpr)
			lv = ((ParameterizedExpr) lv).getTarget();
		if (lv instanceof CellIndexExpr)
			lv = ((CellIndexExpr) lv).getTarget();
		if (lv instanceof DotExpr)
			lv = ((DotExpr) lv).getTarget();
		if (lv instanceof CellIndexExpr)
			lv = ((CellIndexExpr) lv).getTarget();
		if (lv instanceof DotExpr)
			lv = ((DotExpr) lv).getTarget();
		if (lv instanceof NameExpr)
			ne = (NameExpr) lv;
		else{
			System.out.println("LValue target not found at "
					+ lv.getPrettyPrinted());
			return lv.getNameExpressions().iterator().next();
		}
		return ne;
	}
	public void caseNameExpr(NameExpr ne){
		currentOutSet.add(ne.getName().getID());
	}
	
	@Override
	public void caseAssignStmt(AssignStmt s) {
		outFlowSets.put(s, currentOutSet.copy());	
		Set<NameExpr> lValues = new HashSet<NameExpr>();
		List<NameExpr> all = NodeFinder.find(s, NameExpr.class);
		if (s.getLHS() instanceof MatrixExpr) {
			for (Expr expr : ((MatrixExpr) s.getLHS()).getRow(0)
					.getElementList()) {
				NameExpr ne = getLValue(expr);
				lValues.add(ne);
				currentOutSet.remove(ne.getName().getID());
			}
		} else
			lValues.add(getLValue(s.getLHS()));
		
//		System.out.println(s.getPrettyPrinted()+ " "+ lValues);
		for (NameExpr n : lValues)
			currentOutSet.remove(n.getName().getID());
		
		caseASTNode(s.getRHS());
		
		for (NameExpr n : all)
			if (!lValues.contains(n))
				currentOutSet.add(n.getName().getID());

		inFlowSets.put(s, currentOutSet.copy());

	}

	@Override
	public void caseFunction(Function node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet;
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentOutSet);

	}


	@Override
	public void caseScript(Script node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet;
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentOutSet);
	}

	

	public boolean isLive(ASTNode n, String s) {
		while (!(n instanceof Stmt)) {
			n = n.getParent();
		}
		if (inFlowSets.containsKey(n))
			return inFlowSets.get(n).contains(s);
		throw new RuntimeException("NameExpr not inside an Stmt");
	}

	@Override
	public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest) {
		dest.clear();
		dest.addAll(source);
	}

	@Override
	public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2,
			HashSetFlowSet<String> out) {
		out.clear();
		out.addAll(in1);
		out.addAll(in2);		
	}

	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}

	@Override
	public void setCurrentResults(Map<Script, HashSetFlowSet<String>> res) {
		// TODO Auto-generated method stub
		
	}
}
