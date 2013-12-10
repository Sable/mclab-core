package natlab.tame.tamerplus.utils;

import ast.ASTNode;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.LiteralExpr;
import ast.Name;
import ast.NameExpr;

import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tamerplus.analysis.AnalysisEngine;
import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.UDChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.tamerplus.utils.TamerPlusUtils;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import natlab.tame.valueanalysis.*;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValue;
import natlab.tame.valueanalysis.advancedMatrix.AdvancedMatrixValueFactory;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValueFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.ValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class IntOkAnalysis extends TIRAbstractNodeCaseHandler{
	
	private static Boolean debug =true;
	
	
		public static ValueAnalysis<AggrValue<AdvancedMatrixValue>> analyzeForIntOk(
			SimpleFunctionCollection callgraph,
			List<AggrValue<AdvancedMatrixValue>> inputValues) {
			
			
			if (debug) System.out.println("analyzing for IntOk");
			
			ValueFactory<AggrValue<AdvancedMatrixValue>> factory = new AdvancedMatrixValueFactory();	
			ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis = new ValueAnalysis<AggrValue<AdvancedMatrixValue>>(
					callgraph, Args.newInstance(inputValues), factory);
			
			int size = analysis.getNodeList().size();
			
			
			// for every function node
			for (int i=0; i<size; i++){
				StaticFunction function = analysis.getNodeList().get(i)
						.getFunction();
				TransformationEngine transformationEngine = TransformationEngine
						.forAST(function.getAst());
				AnalysisEngine analysisEngine = transformationEngine
						.getAnalysisEngine();

				UDDUWeb web = analysisEngine.getUDDUWebAnalysis();
				
				ArrayList<String> varList = getVariableList(web, function);
				
				if (debug){
					for(String v: varList){
						System.out.println(","+v);
					}
					System.out.println("\n");
				}
				
				Map<String,VarIsIntOk> intOkMap = new HashMap<String, VarIsIntOk>();
				ArrayList<ArrayList<VarIsIntOk>> colourList = new ArrayList<ArrayList<VarIsIntOk>>();
				
				
				for (String var : varList){
					if (debug){
						
							System.out.println("--"+var);
					}
					Map<TIRNode, Integer> varDefs = web.getNodeAndColorForDefinition(var);
					if(debug){
						System.out.println("defs: "+var+" "+varDefs.toString());
					}
					Map<TIRNode, Integer> varUses = web.getNodeAndColorForUse(var);
					if(debug){
						System.out.println("uses: "+var+" "+varUses.toString());
					}
					
					colourList = (analysisFirstPass(var, varDefs, varUses, colourList, web));
					
//					int i1=0;
//					if(debug){
//						System.out.println("colourList size = "+colourList.size());
//					}
//					for(ArrayList<VarIsIntOk> x : colourList){
//						System.out.println("colour "+i1+":\n");
//						for (VarIsIntOk y : x){
//							System.out.println(y.getVarName()+" - "+y.getIsIntOk().getIsInt()+"\n"+y.getIsIntOk().getDependsOn().toString());
//						}
//						i1++;
//					}
					
					
					//colourList = analysisCleanupPass(var,colourList);
					
				}
				colourList = analysisCleanupPass(colourList);
				int i1=0;
				if(debug){
					System.out.println("colourList size = "+colourList.size());
				}
				for(ArrayList<VarIsIntOk> x : colourList){
					System.out.println("colour "+i1+":\n");
					for (VarIsIntOk y : x){
						System.out.println(y.getVarName()+" - "+y.getIsIntOk().getIsInt()+"\n"+y.getIsIntOk().getDependsOn().toString());
					}
					i1++;
				}
				
				DUChain vDUChain = web.getDUChain();
				LinkedList<TIRNode> allStatements = web.getVisitedStmtsLinkedList();
				for (TIRNode statement : allStatements) {
					if (null != vDUChain.getUsesMapForDefinitionStmt(statement)){
						//This def has uses, check if we need to change its mclass.
						changeMclass(analysis, statement, web, i, colourList);
						
					}
						
				}

				
			}
			if (debug){
			System.out.println("#################################");
			for (int i = 0; i < analysis.getNodeList().size(); i++) {
				System.out.println(ValueAnalysisPrinter.prettyPrint(analysis
						.getNodeList().get(i).getAnalysis()));
			}
			}
			return analysis;
			
		}
	
	
	private static void changeMclass(
				ValueAnalysis<AggrValue<AdvancedMatrixValue>> analysis,
				TIRNode statement, UDDUWeb web, int graphIndex, ArrayList<ArrayList<VarIsIntOk>> colourList) {
				
//				int i=0;
//				for(ArrayList<VarIsIntOk> x : colourList){
//					System.out.println("colour "+i+":\n");
//					for (VarIsIntOk y : x){
//						System.out.println(y.getVarName()+" - "+y.getIsIntOk().getIsInt()+"\n");
//					}
//					i++;
//				}
				
				String var;
				Integer col;
				ArrayList<VarIsIntOk> varList;
				if (statement instanceof TIRAbstractAssignStmt){
					var = ((TIRAbstractAssignStmt)statement).getVarName();
					
					if(debug) System.out.println("var is "+var);
					
					AdvancedMatrixValue temp = ((AdvancedMatrixValue) (analysis
							.getNodeList().get(graphIndex).getAnalysis()
							.getOutFlowSets().get(statement).get(var).getSingleton()));
					
					col = web.getNodeAndColorForDefinition(var).get(statement);
					if(debug) System.out.println("col is "+col);
					varList = colourList.get(col);
					if(debug) System.out.println("varlist is "+varList.get(0));
					if (/*varList.contains(var) &&*/ isVarIntOkinList(varList,var)){
					temp.setMatlabClass(PrimitiveClassReference.INT64);
					}
					System.out.println(temp.getMatlabClass().getName());
				}
				
				
			
		}


	private static boolean isVarIntOkinList(ArrayList<VarIsIntOk> varList,
			String var) {
		for (VarIsIntOk v : varList){
			if (v.getVarName().equals(var) && v.getIsIntOk().getIsInt() == true)
				return true;
		}
		return false;
	}


	private static ArrayList<ArrayList<VarIsIntOk>> analysisCleanupPass(
				ArrayList<ArrayList<VarIsIntOk>> colourList) {
		ArrayList<ArrayList<VarIsIntOk>> colourList1 = new ArrayList<ArrayList<VarIsIntOk>>(colourList);
		//for(int i=0; i<colourList1.size();i++){
			//loop over each colour
			//colourList1.set(i, cleanupSameColourList(colourList1.get(i),colourList1));
		//}
			colourList1 = cleanupSameColourList(colourList1);
			return colourList1;
		}


	private static ArrayList<ArrayList<VarIsIntOk>> cleanupSameColourList(
			ArrayList<ArrayList<VarIsIntOk>> colourList1) {
		
		//ArrayList<VarIsIntOk> varList1 = new ArrayList<VarIsIntOk>(varList);
		boolean change=false;
		
		do{
			change = false;
			for (ArrayList<VarIsIntOk> vl1 : colourList1){
			for (VarIsIntOk v1 : vl1){
				if (v1.getIsIntOk().getIsInt() == true){
					for (ArrayList<VarIsIntOk> vl2 : colourList1){
					for (VarIsIntOk v2 : vl2){
						if(v2.getIsIntOk().getDependsOn().contains(v1.getVarName())){
							v2.getIsIntOk().getDependsOn().remove(v1.getVarName());
							change=true;
							if (v2.getIsIntOk().getDependsOn().size() == 0 && v2.getIsIntOk().getDepends() == true){
								v2.getIsIntOk().setIsInt(true);
							}
						}
						
					}
					}
				}
			}
			}
		} while (change ==true);
		return colourList1;
	}


	public static ArrayList<ArrayList<VarIsIntOk>> analysisFirstPass(String var,
				Map<TIRNode, Integer> varDefs, Map<TIRNode, Integer> varUses, ArrayList<ArrayList<VarIsIntOk>> ColourList, UDDUWeb web) {
		
		if (debug) System.out.println("\n@@@@@@@@@@@@@@@@@@@@@analyzing for IntOk first pass@@@@@@@@@@@@@@@@@@@@@@@@\n");
		ArrayList<ArrayList<VarIsIntOk>> ColourList1 = new ArrayList<ArrayList<VarIsIntOk>>(ColourList);
		//ArrayList<VarIsIntOk> varIntOkColList = new ArrayList<VarIsIntOk>();
		VarIsIntOk temp;
		
		for (TIRNode defNode: varDefs.keySet()){
			//check if var can be an int in this def
			//if not add a list of dependency vars
			temp = getDefVarIntOkColListNode(defNode, var, varDefs.get(defNode), web);
			if (debug) System.out.println("~~~~~~~~~~~~~~~~~"+temp);
			
			ColourList1 = updateColourList(temp, ColourList1);	
			
			
		}
		
		for (TIRNode useNode: varUses.keySet()){
			
			temp = getUseVarIntOkColListNode(useNode, var, varUses.get(useNode));
			if (debug) System.out.println("@@@@@@@@@@@@@@@@"+temp);
			ColourList1 = updateColourList(temp, ColourList1);	
			
		}
			return ColourList1;
		}


	private static ArrayList<ArrayList<VarIsIntOk>> updateColourList(
			VarIsIntOk temp, ArrayList<ArrayList<VarIsIntOk>> colourList) {
		/*
		 * check if the color for temp exists in ColourList (basically check against the size of List).
		 * if not, add it and create a new varIntOkColList.
		 * add to this list.
		 */
		int listSize = colourList.size();
		if (debug) System.out.println("!!!!!!!!!!!"+listSize);
//		if (temp.getColour() < listSize){
//			//Colour exists in the list
//		}
//		if (temp.getColour() == listSize){
//			//next colour, add it
//			colourList.add(new ArrayList<VarIsIntOk>());
//		}
		if (temp.getColour() >= listSize){
			while (temp.getColour() >= colourList.size()){
				colourList.add(new ArrayList<VarIsIntOk>());
			}
		}
		int index = temp.getColour();
		ArrayList<VarIsIntOk> listAtColour = colourList.get(index);
		boolean exists = false;
		VarIsIntOk updated ;
		for (VarIsIntOk i : listAtColour){
			if(i.getVarName().equals(temp.getVarName())){
				exists = true;
				//update information
				 updated = updateVarInfo(i,temp);
				 i.setIsIntOk(updated.getIsIntOk());
			}
			
				
		}
		colourList.set(index, listAtColour);
		if (!exists){
			listAtColour.add(temp);
			colourList.set(index, listAtColour);
		}
		
		return colourList;
	}


	private static VarIsIntOk updateVarInfo(VarIsIntOk i, VarIsIntOk temp) {
		
		VarIsIntOk update = new VarIsIntOk();
		update.setIsIntOk(new IntOk(false, false, new ArrayList<String>()));
		if (i.getIsIntOk().getIsInt() == true && temp.getIsIntOk().getIsInt() == true){
			update.getIsIntOk().setIsInt(true);
		}
		
		for (String deps : i.getIsIntOk().getDependsOn()){
			update.getIsIntOk().getDependsOn().add(deps);
		}
		for (String deps1 : temp.getIsIntOk().getDependsOn()){
			if (! update.getIsIntOk().getDependsOn().contains(deps1)){
				update.getIsIntOk().getDependsOn().add(deps1);
			}
		}
		
		if(update.getIsIntOk().getDependsOn().size()>0){
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
		
		if(useNode instanceof TIRAbstractAssignToListStmt){
			
		}
		
		else if (useNode instanceof TIRCopyStmt){
			if (debug) System.out.println("++++++"+varInfo);
			varIntOk = CheckRHS.handleCopyStmtUse((TIRCopyStmt)useNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
			
		}
		
//		else if (useNode instanceof TIRAbstractAssignToVarStmt){
//			if (debug) 
//			{
//				System.out.println("--------"+varInfo);
//			
//			varIntOk.setIsInt(true);
//			varInfo.setIsIntOk(varIntOk);
//			return varInfo;
//			}
//			return varInfo;
//		}
		return null;
	}


	private static VarIsIntOk getDefVarIntOkColListNode(TIRNode defNode, String var, Integer colour, UDDUWeb web) {
		VarIsIntOk varInfo = new VarIsIntOk();
		varInfo.setColour(colour);
		varInfo.setVarName(var);
		varInfo.setNode(defNode);
		IntOk varIntOk = new IntOk(false, false, new ArrayList<String>());
		
		if(defNode instanceof TIRAbstractAssignToListStmt){
			
		}
		
//		else if (defNode instanceof TIRAbstractAssignToVarStmt){
//			if (debug) 
//			{
//				System.out.println("--------"+varInfo);
//			
//			varIntOk.setIsInt(true);
//			varInfo.setIsIntOk(varIntOk);
//			return varInfo;
//			}
//			return varInfo;
//		}
//		
//		else if (defNode instanceof TIRArraySetStmt){
//			
//		}
		
		else if (defNode instanceof TIRAssignLiteralStmt){
			
			varIntOk = CheckRHS.handleAssignLiteralStmt((TIRAssignLiteralStmt)defNode);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
		
		}
		
		else if (defNode instanceof TIRCopyStmt){
			if (debug) System.out.println("&&&&&&&&&&&"+varInfo);
			varIntOk = CheckRHS.handleCopyStmt((TIRCopyStmt)defNode, web);
			varInfo.setIsIntOk(varIntOk);
			return varInfo;
			
		}
		if (debug) System.out.println("#########"+varInfo);
		return null;
	}


	


	public static ArrayList<String> getVariableList(UDDUWeb web, StaticFunction function){
		/*
		 * This is definitely an unoptimized way of doing it.
		 * Replace it with a better way.
		 * Other things:
		 * 1. Considers only those variables which are used somewhere.
		 */
		DUChain vDUChain = web.getDUChain();
		Map<String, HashSet<TIRNode>> varUses;
		ArrayList<String> varList = new ArrayList<String>();
		//Make a list of variables
		LinkedList<TIRNode> allStatements = web.getVisitedStmtsLinkedList();
		for (TIRNode statement : allStatements) {
			
			if(debug){
				System.out.println("~~"+statement+"\n");
			}
			varUses = vDUChain.getUsesMapForDefinitionStmt(statement);

			if (null != varUses) {
				for (String var : varUses.keySet()) {
					//if (!function.getAst().getOutParamSet().contains(var)) 
					{
									// Do not rename return variable
//						if (debug)
//						System.out.println("==" + statement.toString()
//								+ " defines " + var + "==");
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
