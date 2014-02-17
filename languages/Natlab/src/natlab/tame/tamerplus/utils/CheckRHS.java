package natlab.tame.tamerplus.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.UDChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRNode;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.LiteralExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;

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
//		UDChain vUDChain = web.getUDChain();
//		Map<String, Set<TIRNode>> varDefs = vUDChain.getDefinitionsMapFoUseStmt(defNode); 
		
		varIntOk.getDependsOn().add(name);
		varIntOk.setDepends(true);
		return varIntOk;
	}

	public static IntOk handleCopyStmtUse(TIRCopyStmt useNode) {
		IntOk varIntOk = new IntOk(true, false, new ArrayList<String>());
		
		return varIntOk;
	}

	public static IntOk handleAssignToVarStmt(TIRAbstractAssignToVarStmt defNode) {
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		Expr rhs = defNode.getRHS();
		
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
		
		else if (rhs instanceof NameExpr){
			String name = rhs.getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				
				varIntOk.setDependsOn(args);
				if (args.size()>0)
					varIntOk.setDepends(true);
			}
			
			if (rhsName.equals("length") || rhsName.equals("ones") || rhsName.equals("zeros")){
				varIntOk.setIsInt(true);
			}
		}
		
		return varIntOk;
	}

	public static IntOk handleAssignToListStmt(
			TIRAbstractAssignToListStmt defNode) {
IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		Expr rhs = defNode.getRHS();
		
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
		
		else if (rhs instanceof NameExpr){
			String name = rhs.getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes") || rhsName.equals("colon")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				
				
				varIntOk.setDependsOn(args);
				if (args.size()>0)
					varIntOk.setDepends(true);
			}
			
			if (rhsName.equals("length") || rhsName.equals("ones") || rhsName.equals("zeros")){
				
				if (debug)System.out.println(defNode.getLHS().getNodeString()+"#R6$&%^$&&%*%^*^%&**%^&");
				
				varIntOk.setIsInt(true);
			}
		}
		
		return varIntOk;
	}

	public static IntOk handleArraySetStmt(TIRArraySetStmt defNode) {
IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		Expr rhs = defNode.getRHS();
		
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
		
		else if (rhs instanceof NameExpr){
			String name = rhs.getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				
				varIntOk.setDependsOn(args);
				if (args.size()>0)
					varIntOk.setDepends(true);
			}
			
			if (rhsName.equals("length") || rhsName.equals("ones") || rhsName.equals("zeros")){
				varIntOk.setIsInt(true);
			}
		}
		
		return varIntOk;
	}

	public static IntOk handleArrayGetStmt(TIRArrayGetStmt defNode) {
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		String name = defNode.getRHS().getVarName();
		
		varIntOk.getDependsOn().add(name);
		varIntOk.setDepends(true);
		return varIntOk;
	}

	public static IntOk handleUseRHSExpr(Expr rhs, String var ) {
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		if (rhs instanceof NameExpr){
			varIntOk.setIsInt(true);//??????????????why???????????
			
		}
		
		else if (rhs instanceof RangeExpr){
		
			varIntOk.setIsInt(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes") || rhsName.equals("colon")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				if(args.contains(var)){
					args.remove(var);
				}
				
			//	varIntOk.setDependsOn(args);
			//	if (args.size()>0)
			//		varIntOk.setDepends(true);
				varIntOk.setIsInt(true);
//				if (var.equals("x")){
//					varIntOk.setIsInt(false);
//					System.err.println(rhs.getNodeString());
//					//System.exit(0);
//				}
				
			}
			if (rhsName.equals("ones") || rhsName.equals("zeros")){
				
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
					args.add(	((NameExpr)arg).getVarName());
					}
					if(args.contains(var)){
						varIntOk.setIsInt(true);
					}
					
					
				
			}
			
			
			
		}
		return varIntOk;
	}

	public static IntOk handleForStmt(TIRForStmt defNode) {
	IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		Expr rhs = defNode.getAssignStmt().getRHS();
		
		
		
		if (rhs instanceof RangeExpr){
			String name = ((RangeExpr) rhs).getLower().getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
			
			name = ((RangeExpr) rhs).getUpper().getVarName();
			//varIntOk.getDependsOn().add(name);
			//varIntOk.setDepends(true);
			
			if (((RangeExpr) rhs).hasIncr()){
			name = ((RangeExpr) rhs).getIncr().getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
			}
		}
		
		else
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
		
		else if (rhs instanceof NameExpr){
			String name = rhs.getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				
				varIntOk.setDependsOn(args);
				if (args.size()>0)
					varIntOk.setDepends(true);
			}
			
			if (rhsName.equals("length") || rhsName.equals("ones") || rhsName.equals("zeros")){
				varIntOk.setIsInt(true);
			}
		}
		
		return varIntOk;
	}

	public static IntOk handleCallStmt(TIRCallStmt defNode) {
IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		Expr rhs = defNode.getRHS();
		
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
		
		else if (rhs instanceof NameExpr){
			String name = rhs.getVarName();
			varIntOk.getDependsOn().add(name);
			varIntOk.setDepends(true);
		}
		
		else if (rhs instanceof ParameterizedExpr){
			
			
			String rhsName = ((ParameterizedExpr)rhs).getVarName();
			
			ArrayList<String> args = new ArrayList<String>();
			if (rhsName.equals("plus") || rhsName.equals("minus") || rhsName.equals("mtimes") || rhsName.equals("colon")){
				for (Expr arg : ((ParameterizedExpr)rhs).getArgs()){
				args.add(	((NameExpr)arg).getVarName());
				}
				
				if (args.size()>0){
					varIntOk.setIsInt(false);
					varIntOk.setDepends(true);
				}
				
				varIntOk.setDependsOn(args);
				if(defNode.getLHS().getNodeString().equals("[mc_t4]")){
					System.err.println(args);
					varIntOk.setIsInt(true);
					//System.exit(1);
				}
				
				if (args.size()>0){
					varIntOk.setIsInt(false);
					varIntOk.setDepends(true);
				}
					//varIntOk.setDepends(true);
			}
			
			if (rhsName.equals("length") || rhsName.equals("ones") || rhsName.equals("zeros")){
				
				if (debug)System.out.println(defNode.getLHS().getNodeString()+"#R6$&%^$&&%*%^*^%&**%^&");
				
				varIntOk.setIsInt(true);
			}
		}
		
		return varIntOk;
	}
	
	
	
	
}
