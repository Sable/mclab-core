package natlab.toolkits.analysis.core;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

import java.util.Set;

import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.utils.AstFunctions;
import natlab.utils.NodeFinder;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

public class LivenessAnalysis extends
		AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>> {

	public LivenessAnalysis(ASTNode<?> tree) {
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

		if (s.getLHS() instanceof MatrixExpr) {
			for (Expr expr : ((MatrixExpr) s.getLHS()).getRow(0).getElementList()) {
				NameExpr ne = getLValue(expr);
				lValues.add(ne);
				currentOutSet.remove(ne.getName().getID());
			}
		} else {
			lValues.add(getLValue(s.getLHS()));
		}
		
		for (NameExpr n : lValues) {
			currentOutSet.remove(n.getName().getID());
		}
		
		caseASTNode(s.getRHS());
		currentOutSet.addAll(FluentIterable.from(NodeFinder.find(NameExpr.class, s))
		    .filter(not(in(lValues)))
		    .transform(AstFunctions.nameExprToID())
		    .toImmutableList());

		inFlowSets.put(s, currentInSet.copy());
	}

	@Override
	public void caseFunction(ast.Function node) {
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
