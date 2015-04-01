package natlab.tame.tamerplus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValueFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import ast.ASTNode;
import ast.Expr;
import ast.ForStmt;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

public class IntOkAnalysis extends TIRAbstractNodeCaseHandler {

	private static Boolean debug = false;

	public static ValueAnalysis<AggrValue<BasicMatrixValue>> analyzeForIntOk(
			SimpleFunctionCollection callgraph,
			List<AggrValue<BasicMatrixValue>> inputValues) {

		if (debug)
			System.out.println("analyzing for IntOk");

		ValueFactory<AggrValue<BasicMatrixValue>> factory = new BasicMatrixValueFactory();
		ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = new ValueAnalysis<AggrValue<BasicMatrixValue>>(
				callgraph, Args.newInstance(inputValues), factory);

		int size = analysis.getNodeList().size();

		// for every function node
		for (int i = 0; i < size; i++) {
			StaticFunction function = analysis.getNodeList().get(i)
					.getFunction();
			TransformationEngine transformationEngine = TransformationEngine
					.forAST(function.getAst());
			AnalysisEngine analysisEngine = transformationEngine
					.getAnalysisEngine();
			// Get UDDUWeb
			UDDUWeb web = analysisEngine.getUDDUWebAnalysis();
			// Get list of all the variables
			ArrayList<String> varList = getVariableList(web, function);

			if (debug) {
				for (String v : varList) {
					System.out.println("," + v);
				}
				System.out.println("\n");
			}

			Map<String, VarIsIntOk> intOkMap = new HashMap<String, VarIsIntOk>();
			// A list by graph-colour for lists of VarIsIntOk for each colour.
			ArrayList<ArrayList<VarIsIntOk>> colourList = new ArrayList<ArrayList<VarIsIntOk>>();

			for (String var : varList) {
				if (debug) {

					System.out.println("--" + var);
				}

				// Map of node and color for each var's definitions
				Map<TIRNode, Integer> varDefs = web
						.getNodeAndColorForDefinition(var);
				if (debug) {
					System.out.println("defs: " + var + " "
							+ varDefs.toString());
				}

				// Map of node and color for each var's uses
				Map<TIRNode, Integer> varUses = web.getNodeAndColorForUse(var);
				if (debug) {
					System.out.println("uses: " + var + " "
							+ varUses.toString());
				}

				colourList = (analysisFirstPass(var, varDefs, varUses,
						colourList, web));

				// int i1=0;
				// if(debug){
				// System.out.println("colourList size = "+colourList.size());
				// }
				// for(ArrayList<VarIsIntOk> x : colourList){
				// System.out.println("colour "+i1+":\n");
				// for (VarIsIntOk y : x){
				// System.out.println(y.getVarName()+" - "+y.getIsIntOk().getIsInt()+"\n"+y.getIsIntOk().getDependsOn().toString());
				// }
				// i1++;
				// }

				// colourList = analysisCleanupPass(var,colourList);

			}
			colourList = analysisCleanupPass(colourList);
			int i1 = 0;
			if (debug) {
				System.out.println("colourList size = " + colourList.size());
			}
			for (ArrayList<VarIsIntOk> x : colourList) {
				System.out.println("colour " + i1 + ":\n");
				for (VarIsIntOk y : x) {
					System.out.println(y.getVarName() + " - "
							+ y.getIsIntOk().getIsInt() + "\n"
							+ y.getIsIntOk().getDependsOn().toString());
				}
				i1++;
			}

			DUChain vDUChain = web.getDUChain();
			LinkedList<TIRNode> allStatements = web.getVisitedStmtsLinkedList();
			for (TIRNode statement : allStatements) {
				if (null != vDUChain.getUsesMapForDefinitionStmt(statement)) {
					// This def has uses, check if we need to change its mclass.
					changeMclass(analysis, statement, web, i, colourList);

				}

			}

		}
		if (debug) {
			System.out.println("#################################");
			for (int i = 0; i < analysis.getNodeList().size(); i++) {
				System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
						.getNodeList().get(i).getAnalysis()));
			}
		}
		return analysis;

	}

	private static void changeMclass(
			ValueAnalysis<AggrValue<BasicMatrixValue>> analysis,
			TIRNode statement, UDDUWeb web, int graphIndex,
			ArrayList<ArrayList<VarIsIntOk>> colourList) {

		// int i=0;
		// for(ArrayList<VarIsIntOk> x : colourList){
		// System.out.println("colour "+i+":\n");
		// for (VarIsIntOk y : x){
		// System.out.println(y.getVarName()+" - "+y.getIsIntOk().getIsInt()+"\n");
		// }
		// i++;
		// }

		String var;
		Integer col;
		ArrayList<VarIsIntOk> varList;
		ArrayList<String> vars = new ArrayList<String>();
		if (statement instanceof TIRAbstractAssignStmt) {
			if (statement instanceof TIRAbstractAssignToListStmt) {
				for (ast.Name name : ((TIRAbstractAssignToListStmt) statement)
						.getTargets().asNameList()) {
					vars.add(name.getID());
				}
			} else {
				var = ((TIRAbstractAssignStmt) statement).getVarName();
				vars.add(var);
			}
		}

		if (statement instanceof TIRForStmt) {
			var = ((TIRForStmt) statement).getAssignStmt().getVarName();
			vars.add(var);
		}

		for (String var1 : vars) {
			if (debug)
				System.out.println("var is " + var1);
			BasicMatrixValue temp;
			if (analysis.getNodeList().get(graphIndex).getAnalysis()
					.getOutFlowSets().get(statement).isViable()) {

				if (null != ((analysis.getNodeList().get(graphIndex)
						.getAnalysis().getOutFlowSets().get(statement)
						.get(var1)))) {

					temp = ((BasicMatrixValue) (analysis.getNodeList()
							.get(graphIndex).getAnalysis().getOutFlowSets()
							.get(statement).get(var1).getSingleton()));
					BasicMatrixValue temp1 = ((BasicMatrixValue) (analysis
							.getNodeList().get(graphIndex).getAnalysis()
							.getCurrentOutSet().get(var1).getSingleton()));

					col = web.getNodeAndColorForDefinition(var1).get(statement);
					// if(debug) System.out.println("col is "+col);
					varList = colourList.get(col);
					// if(debug)
					// System.out.println("varlist is "+varList.get(0));
					if (/* varList.contains(var) && */isVarIntOkinList(varList,
							var1)) {
						temp1.setMatlabClass(PrimitiveClassReference.INT64);
						temp.setMatlabClass(PrimitiveClassReference.INT64);
					}
				} else
					System.out
							.println("72634785634785635867486238954639847569138561562958635===="
									+ var1.toString());
			}

			// System.out.println(temp.getMatlabClass().getName());
		}

	}

	private static boolean isVarIntOkinList(ArrayList<VarIsIntOk> varList,
			String var) {
		for (VarIsIntOk v : varList) {
			if (v.getVarName().equals(var) && v.getIsIntOk().getIsInt() == true)
				return true;
		}
		return false;
	}

	private static ArrayList<ArrayList<VarIsIntOk>> analysisCleanupPass(
			ArrayList<ArrayList<VarIsIntOk>> colourList) {
		ArrayList<ArrayList<VarIsIntOk>> colourList1 = new ArrayList<ArrayList<VarIsIntOk>>(
				colourList);
		// for(int i=0; i<colourList1.size();i++){
		// loop over each colour
		// colourList1.set(i,
		// cleanupSameColourList(colourList1.get(i),colourList1));
		// }
		colourList1 = cleanupSameColourList(colourList1);
		return colourList1;
	}

	private static ArrayList<ArrayList<VarIsIntOk>> cleanupSameColourList(
			ArrayList<ArrayList<VarIsIntOk>> colourList1) {

		// ArrayList<VarIsIntOk> varList1 = new ArrayList<VarIsIntOk>(varList);
		boolean change = false;

		do {
			change = false;
			for (ArrayList<VarIsIntOk> vl1 : colourList1) {
				for (VarIsIntOk v1 : vl1) {
					if (v1.getIsIntOk().getIsInt() == true) {
						for (ArrayList<VarIsIntOk> vl2 : colourList1) {
							for (VarIsIntOk v2 : vl2) {
								if (v2.getIsIntOk().getDependsOn()
										.contains(v1.getVarName())) {
									if (trueEveryWhere(v1, colourList1)) {
										v2.getIsIntOk().getDependsOn()
												.remove(v1.getVarName());
										change = true;
									}
									if (v2.getIsIntOk().getDependsOn().size() == 0
											&& v2.getIsIntOk().getDepends() == true) {
										v2.getIsIntOk().setIsInt(true);
									}
								}

							}
						}
					}
				}
			}
		} while (change == true);
		return colourList1;
	}

	private static boolean trueEveryWhere(VarIsIntOk v1,
			ArrayList<ArrayList<VarIsIntOk>> colourList1) {
		boolean ret = true;
		for (ArrayList<VarIsIntOk> vl1 : colourList1) {
			for (VarIsIntOk vv : vl1) {
				if (vv.getVarName().equals(v1.getVarName())
						&& vv.getIsIntOk().getIsInt() == false) {
					ret = false;
					return ret;
				}
			}
		}
		return ret;
	}

	public static ArrayList<ArrayList<VarIsIntOk>> analysisFirstPass(
			String var, Map<TIRNode, Integer> varDefs,
			Map<TIRNode, Integer> varUses,
			ArrayList<ArrayList<VarIsIntOk>> ColourList, UDDUWeb web) {

		if (debug)
			System.out
					.println("\n@@@@@@@@@@@@@@@@@@@@@analyzing for IntOk first pass@@@@@@@@@@@@@@@@@@@@@@@@\n");
		ArrayList<ArrayList<VarIsIntOk>> ColourList1 = new ArrayList<ArrayList<VarIsIntOk>>(
				ColourList);
		// ArrayList<VarIsIntOk> varIntOkColList = new ArrayList<VarIsIntOk>();
		VarIsIntOk temp;

		for (TIRNode defNode : varDefs.keySet()) {
			// check if var can be an int in this def
			// if not add a list of dependency vars
			temp = getDefVarIntOkColListNode(defNode, var,
					varDefs.get(defNode), web);
			if (debug)
				System.out.println("~~~~~~~~~~~~~~~~~" + temp);

			ColourList1 = updateColourList(temp, ColourList1);

		}

		for (TIRNode useNode : varUses.keySet()) {

			temp = getUseVarIntOkColListNode(useNode, var, varUses.get(useNode));
			if (debug)
				System.out.println("@@@@@@@@@@@@@@@@" + temp);
			ColourList1 = updateColourList(temp, ColourList1);

		}
		return ColourList1;
	}

	private static ArrayList<ArrayList<VarIsIntOk>> updateColourList(
			VarIsIntOk temp, ArrayList<ArrayList<VarIsIntOk>> colourList) {
		/*
		 * check if the color for temp exists in ColourList (basically check
		 * against the size of List). if not, add it and create a new
		 * varIntOkColList. add to this list.
		 */
		int listSize = colourList.size();
		if (debug)
			System.out.println("!!!!!!!!!!!" + listSize);
		// if (temp.getColour() < listSize){
		// //Colour exists in the list
		// }
		// if (temp.getColour() == listSize){
		// //next colour, add it
		// colourList.add(new ArrayList<VarIsIntOk>());
		// }
		if (temp.getColour() >= listSize) {
			while (temp.getColour() >= colourList.size()) {
				colourList.add(new ArrayList<VarIsIntOk>());
			}
		}
		int index = temp.getColour();
		ArrayList<VarIsIntOk> listAtColour = colourList.get(index);
		boolean exists = false;
		VarIsIntOk updated;
		for (VarIsIntOk i : listAtColour) {
			if (i.getVarName().equals(temp.getVarName())) {
				exists = true;
				// update information
				updated = updateVarInfo(i, temp);
				i.setIsIntOk(updated.getIsIntOk());
			}

		}
		colourList.set(index, listAtColour);
		if (!exists) {
			listAtColour.add(temp);
			colourList.set(index, listAtColour);
		}

		return colourList;
	}

	private static VarIsIntOk updateVarInfo(VarIsIntOk i, VarIsIntOk temp) {

		VarIsIntOk update = new VarIsIntOk();
		update.setIsIntOk(new IntOk(false, false, new ArrayList<String>()));
		if (i.getIsIntOk().getIsInt() == true
				&& temp.getIsIntOk().getIsInt() == true) {
			update.getIsIntOk().setIsInt(true);
		}

		for (String deps : i.getIsIntOk().getDependsOn()) {
			update.getIsIntOk().getDependsOn().add(deps);
		}
		for (String deps1 : temp.getIsIntOk().getDependsOn()) {
			if (!update.getIsIntOk().getDependsOn().contains(deps1)) {
				update.getIsIntOk().getDependsOn().add(deps1);
			}
		}

		if (update.getIsIntOk().getDependsOn().size() > 0) {
			update.getIsIntOk().setDepends(true);
		}

		update.setColour(temp.getColour());
		update.setVarName(temp.getVarName());
		return update;
	}

	private static VarIsIntOk getUseVarIntOkColListNode(TIRNode useNode,
			String var, Integer colour) {
		VarIsIntOk varInfo = new VarIsIntOk();
		varInfo.setColour(colour);
		varInfo.setVarName(var);
		varInfo.setNode(useNode);
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());

		// if (useNode instanceof TIRFunction){
		// for (Name v: ((TIRFunction) useNode).getInputParams()){
		// if(v.getVarName().equals(var)){
		// varInfo.setIsIntOk(varIntOk);
		// }
		// }
		// System.err.println(((TIRFunction) useNode).getNodeString());
		// System.exit(1);
		// return varInfo;
		// }

		if (useNode instanceof TIRCallStmt) {
			varIntOk = CheckRHS.handleUseRHSExpr(
					((TIRCallStmt) useNode).getRHS(), var);

			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		else if (useNode instanceof TIRArraySetStmt) {

			if (((TIRArraySetStmt) useNode).getRHS().getVarName().equals(var)) {
				varIntOk.setDepends(true);
				varIntOk.getDependsOn().add(
						((TIRArraySetStmt) useNode).getLHS().getVarName());
				varInfo.setIsIntOk(varIntOk);

				return varInfo;
			}
			ParameterizedExpr lhs = (ParameterizedExpr) ((TIRArraySetStmt) useNode)
					.getLHS();
			ArrayList<String> args = new ArrayList<String>();

			// varIntOk = CheckRHS.handleUseRHSExpr(((TIRArraySetStmt)
			// useNode).getRHS(), var);

			for (Expr arg : ((ParameterizedExpr) lhs).getArgs()) {
				if (arg instanceof NameExpr)
					args.add(((NameExpr) arg).getVarName());
			}
			if (args.contains(var)) {
				varIntOk.setIsInt(true);

			}

			varInfo.setIsIntOk(varIntOk);

			return varInfo;

		}

		else if (useNode instanceof TIRArrayGetStmt) {
			ParameterizedExpr rhs = (ParameterizedExpr) ((TIRArrayGetStmt) useNode)
					.getRHS();
			ArrayList<String> args = new ArrayList<String>();

			for (Expr arg : ((ParameterizedExpr) rhs).getArgs()) {
				if (arg instanceof NameExpr)
					args.add(((NameExpr) arg).getVarName());
			}
			if (args.contains(var)) {
				varIntOk.setIsInt(true);

			}
			if (rhs.getVarName().equals(var)) {
				varIntOk.setIsInt(true); // CHECK THIS
			}

			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		else if (useNode instanceof TIRCopyStmt) {
			if (debug)
				System.out.println("++++++" + varInfo);
			varIntOk = CheckRHS.handleCopyStmtUse((TIRCopyStmt) useNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		else if (useNode instanceof TIRAbstractAssignStmt) {

			varIntOk = CheckRHS.handleUseRHSExpr(
					((TIRAbstractAssignStmt) useNode).getRHS(), var);

			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		else if (useNode instanceof TIRForStmt) {

			System.err.println("VARIABLE IN FOR LOOP IS "
					+ ((ForStmt) useNode).getAssignStmt().toString() + " "
					+ var);
			varIntOk = CheckRHS.handleUseRHSExpr(((ForStmt) useNode)
					.getAssignStmt().getRHS(), var);

			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		varInfo.setIsIntOk(varIntOk);
		return varInfo;

	}

	private static VarIsIntOk getDefVarIntOkColListNode(TIRNode defNode,
			String var, Integer colour, UDDUWeb web) {
		VarIsIntOk varInfo = new VarIsIntOk();
		varInfo.setColour(colour);
		varInfo.setVarName(var);
		varInfo.setNode(defNode);
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());

		if (defNode instanceof TIRCallStmt) {

			// if (var.equals("mc_t4")){
			// System.err.println(((TIRCallStmt) defNode).getNodeString());
			// System.exit(0);
			// }
			varIntOk = CheckRHS.handleCallStmt((TIRCallStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		if (defNode instanceof TIRFunction) {
			for (Name v : ((TIRFunction) defNode).getInputParams()) {
				if (v.getVarName().equals(var)) {
					varInfo.setIsIntOk(varIntOk);

				}
			}

			return varInfo;
		}

		if (defNode instanceof TIRAssignLiteralStmt) {

			varIntOk = CheckRHS
					.handleAssignLiteralStmt((TIRAssignLiteralStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		else if (defNode instanceof TIRCopyStmt) {

			if (debug)
				System.out.println("&&&&&&&&&&&" + varInfo);
			varIntOk = CheckRHS.handleCopyStmt((TIRCopyStmt) defNode, web);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		else if (defNode instanceof TIRArraySetStmt) {
			varIntOk = CheckRHS.handleArraySetStmt((TIRArraySetStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		else if (defNode instanceof TIRArrayGetStmt) {
			varIntOk = CheckRHS.handleArrayGetStmt((TIRArrayGetStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		else if (defNode instanceof TIRAbstractAssignToVarStmt) {

			varIntOk = CheckRHS
					.handleAssignToVarStmt((TIRAbstractAssignToVarStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;

		}

		else if (defNode instanceof TIRAbstractAssignToListStmt) {

			varIntOk = CheckRHS
					.handleAssignToListStmt((TIRAbstractAssignToListStmt) defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		else if (defNode instanceof TIRForStmt) {

			varIntOk = CheckRHS.handleForStmt((TIRForStmt) defNode);
			/**/
			// varIntOk.setIsInt(true);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		}

		varInfo.setIsIntOk(varIntOk);
		return varInfo;

	}

	public static ArrayList<String> getVariableList(UDDUWeb web,
			StaticFunction function) {
		/*
		 * This is definitely an unoptimized way of doing it. Replace it with a
		 * better way. Other things: 1. Considers only those variables which are
		 * used somewhere.
		 */
		DUChain vDUChain = web.getDUChain();
		Map<String, HashSet<TIRNode>> varUses;
		ArrayList<String> varList = new ArrayList<String>();
		// Make a list of variables
		LinkedList<TIRNode> allStatements = web.getVisitedStmtsLinkedList();
		for (TIRNode statement : allStatements) {

			if (debug) {
				System.out.println("~~" + statement + "\n");
			}
			varUses = vDUChain.getUsesMapForDefinitionStmt(statement);

			if (null != varUses) {
				for (String var : varUses.keySet()) {
					// if (!function.getAst().getOutParamSet().contains(var))
					{
						// Do not rename return variable
						// if (debug)
						// System.out.println("==" + statement.toString()
						// + " defines " + var + "==");
						if (!varList.contains(var)) {
							varList.add(var);
						}
					}
				}
			}
		}

		return varList;

	}

	@Override
	public void caseASTNode(ASTNode node) {
		// TODO Auto-generated method stub

	}

}
