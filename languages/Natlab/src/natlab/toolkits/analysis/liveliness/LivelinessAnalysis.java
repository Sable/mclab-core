package natlab.toolkits.analysis.liveliness;

import java.util.List;
import java.util.Set;

import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.utils.NodeFinder;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.Function;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Sets;

public class LivelinessAnalysis extends
		AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>> {

	public LivelinessAnalysis(ASTNode<?> tree) {
		super(tree);	
	}	
	
	@Override
	public void caseStmt(Stmt s) {
		outFlowSets.put(s, currentOutSet.copy());
		caseASTNode(s);
		inFlowSets.put(s, currentInSet.copy());
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
		currentInSet.add(ne.getName().getID());
	}
	
	@Override
	public void caseAssignStmt(AssignStmt s) {
		outFlowSets.put(s, currentOutSet.copy());	
		Set<NameExpr> lValues = Sets.newHashSet();
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
		
		for (NameExpr n : lValues)
			currentOutSet.remove(n.getName().getID());
		
		caseASTNode(s.getRHS());
		
		for (NameExpr n : all)
			if (!lValues.contains(n))
				currentOutSet.add(n.getName().getID());

		inFlowSets.put(s, currentInSet.copy());
	}

	@Override
	public void caseFunction(Function node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentInSet);
	}

	@Override
	public void caseScript(Script node) {
		currentOutSet = newInitialFlow();
		currentInSet = currentOutSet.copy();
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentInSet);
	}

	public boolean isLive(ASTNode<?> n, String s) {
		while (!(n instanceof Stmt)) {
			n = n.getParent();
		}
		if (inFlowSets.containsKey(n))
			return inFlowSets.get(n).contains(s);
		throw new RuntimeException("NameExpr not inside an Stmt");
	}

	@Override
	public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest) {
		source.copy(dest);
	}

	@Override
	public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2,
			HashSetFlowSet<String> out) {
		in1.union(in2, out);
	}

	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}
}
