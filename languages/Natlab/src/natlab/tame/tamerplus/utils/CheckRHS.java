package natlab.tame.tamerplus.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.UDChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRNode;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.LiteralExpr;
import ast.NameExpr;

public class CheckRHS {
	private static boolean debug = true;

	public static IntOk handleAssignLiteralStmt(TIRAssignLiteralStmt defNode) {
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());

		
		LiteralExpr rhs = ((LiteralExpr)(((TIRAssignLiteralStmt)defNode).getRHS()));
		if (rhs instanceof IntLiteralExpr){
			varIntOk.setIsInt(true);
			
		}
		else if (rhs instanceof FPLiteralExpr){
			double x = ((FPLiteralExpr)rhs).getValue().getValue().doubleValue();
			if (debug ==true) System.out.println("I am a literal "+x);
			
			if (Math.ceil(x) == Math.floor(x)){
				varIntOk.setIsInt(true);
			}
			else
				varIntOk.setIsInt(false);
		}
		
		return varIntOk;
	}

	public static IntOk handleCopyStmt(TIRCopyStmt defNode, UDDUWeb web) {
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		NameExpr rhs = ((NameExpr)(((TIRCopyStmt)defNode).getRHS()));
		
		String name = rhs.getVarName();
		UDChain vUDChain = web.getUDChain();
		Map<String, Set<TIRNode>> varDefs = vUDChain.getDefinitionsMapFoUseStmt(defNode); 
		
		varIntOk.getDependsOn().add(name);
		varIntOk.setDepends(true);
		return varIntOk;
	}

	public static IntOk handleCopyStmtUse(TIRCopyStmt useNode) {
		IntOk varIntOk = new IntOk(true, false, new ArrayList<String>());
		
		return varIntOk;
	}
}
