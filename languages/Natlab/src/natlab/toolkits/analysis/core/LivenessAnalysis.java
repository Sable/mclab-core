package natlab.toolkits.analysis.core;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

import java.util.Set;

import natlab.utils.AstFunctions;
import natlab.utils.NodeFinder;
import analysis.BackwardAnalysis;
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

import com.google.common.collect.Sets;

public class LivenessAnalysis extends
		BackwardAnalysis<Set<String>> {

	public LivenessAnalysis(ASTNode<?> tree) {
		super(tree);	
	}	
	
	@Override
	public void caseStmt(Stmt s) {
		outFlowSets.put(s, Sets.newHashSet(currentOutSet));
		caseASTNode(s);
		inFlowSets.put(s, Sets.newHashSet(currentInSet));
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
	
	@Override
  public void caseNameExpr(NameExpr ne){
		currentInSet.add(ne.getName().getID());
	}
	
	@Override
	public void caseAssignStmt(AssignStmt s) {
		outFlowSets.put(s, Sets.newHashSet(currentOutSet));	
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
		currentOutSet.addAll(NodeFinder.find(NameExpr.class, s)
		    .filter(not(in(lValues)))
		    .transform(AstFunctions.nameExprToID())
		    .toList());

		inFlowSets.put(s, Sets.newHashSet(currentInSet));
	}

	@Override
	public void caseFunction(ast.Function node) {
		currentOutSet = newInitialFlow();
		currentInSet = Sets.newHashSet(currentOutSet);
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentInSet);
	}

	@Override
	public void caseScript(Script node) {
		currentOutSet = newInitialFlow();
		currentInSet = Sets.newHashSet(currentOutSet);
		outFlowSets.put(node, currentOutSet);
		node.getStmts().analyze(this);
		inFlowSets.put(node, currentInSet);
	}

	@Override
	public Set<String> copy(Set<String> source) {
	  return Sets.newHashSet(source);
	}

	@Override
	public Set<String> merge(Set<String> in1, Set<String> in2) {
	  Set<String> out = Sets.newHashSet(in1);
	  out.addAll(in2);
	  return out;
	}

	@Override
	public Set<String> newInitialFlow() {
		return Sets.newHashSet();
	}
}
