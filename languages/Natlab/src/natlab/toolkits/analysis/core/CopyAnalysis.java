package natlab.toolkits.analysis.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import natlab.toolkits.analysis.HashMapFlowMap;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Stmt;
public class CopyAnalysis extends AbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, AssignStmt>>{
	
	private LivenessAnalysis live;
	
	public CopyAnalysis(ASTNode<?> tree) {
		super(tree);
		live = new LivenessAnalysis(tree);
		live.analyze();
	}

	private static NameExpr getLValue(Expr lv) {
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
	
	private String getName(Expr e){
		return ((NameExpr)e).getName().getID();
	}

	@Override
	public void caseStmt(Stmt s){
        outFlowSets.put( s, currentOutSet.copy() );
	}


	@Override
	public void caseAssignStmt(AssignStmt s){
		Set<NameExpr> lValues = new HashSet<NameExpr>();
		if (s.getLHS() instanceof MatrixExpr) {
			for (Expr expr : ((MatrixExpr) s.getLHS()).getRow(0)
					.getElementList()) {
				NameExpr ne = getLValue(expr);
				lValues.add(ne);
				currentOutSet.remove(ne.getName().getID());
			}
		} else
			lValues.add(getLValue(s.getLHS()));
		Set<String> killset = new HashSet<String>();
		for (NameExpr n: lValues){
			String id = n.getName().getID();
			currentOutSet.remove(id);
			for (String key: currentOutSet.keySet())
				if (currentOutSet.containsKey(key)&& id.equals(getName(currentOutSet.get(key).getRHS())))
					killset.add(key);
		}
		for (String key: killset)
			currentOutSet.remove(key);
		if ((s.getRHS() instanceof NameExpr) && (s.getLHS() instanceof NameExpr)){
			currentOutSet.put(((NameExpr)s.getLHS()).getName().getID(), s);
		}
        outFlowSets.put( s, currentOutSet.copy() );
	}
	
	@Override
	public void copy(HashMapFlowMap<String, AssignStmt> source,
			HashMapFlowMap<String, AssignStmt> dest) {
		source.copy(dest);		
	}

	@Override
	public void merge(HashMapFlowMap<String, AssignStmt> in1,
			HashMapFlowMap<String, AssignStmt> in2, HashMapFlowMap<String, AssignStmt> out) {
		HashMap<String, AssignStmt> res = new HashMap<String, AssignStmt>();
		for (String s: in1.keySet()){
			if (in1.get(s).equals(in2.get(s)))
					res.put(s, in1.get(s));
		}
		out.clear();
		for (String s: res.keySet())
			out.put(s, res.get(s) );
		
	}

	@Override
	public HashMapFlowMap<String, AssignStmt> newInitialFlow() {
		return new HashMapFlowMap<String, AssignStmt>();
	}

}
