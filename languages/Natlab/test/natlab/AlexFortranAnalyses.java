package natlab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import annotations.ast.MatrixType;
import annotations.ast.Size;
import annotations.ast.Type;

import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.Function;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.VariableDecl;
import natlab.toolkits.ValueBox;
import natlab.toolkits.scalar.FlowSet;
import natlab.toolkits.scalar.ReachingDefs;

public class AlexFortranAnalyses {
	public static boolean bSilence = false;
	public static HashSet<String> gLocalFuncList;
	public static final String TEMP_VAR_PREFIX = TypeInferenceEngine.TEMP_VAR_PREFIX;
	
	public static ASTNode Transformation1(ASTNode subtree, SymbolTableScope scope) {
		if(!bSilence) System.out.println(" --- START alex transformation 1");
		if(!bSilence) System.out.println(" --- GOAL: collapse subexpressions");		

		// System.out.println(subtree.dumpTreeAll(true));
		
		// reaching definitions analysis
		subtree.clearUseDefBoxes();
		subtree.generateUseBoxesList();	
		ReachingDefs defsAnalysis = new ReachingDefs(subtree);

		// retrieve flow sets
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();

	    // global definitions map
	    Map<String, Expr> gDefMap = new HashMap<String, Expr>();

	    for(ASTNode codeNode: codeNodeList) {
	    
	    	// skip root
	    	if (codeNode == subtree) continue;

	    	// get flow sets
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// get the set of definitions
			FlowSet defSet = defsAnalysis.getEmptySet();
			afterSet.difference(beforeSet, defSet);

			// get all use boxes in current node
			java.util.List<ValueBox> useBoxes = codeNode.getUseBoxes();
			
			// add new def to to global map, but only in the case
			// of assignment statements
			//    var name -> id of node
			Iterator<AssignStmt> iter = defSet.iterator();
			while (iter.hasNext()) {
																
				AssignStmt as = (AssignStmt)iter.next();
				
				// get expression on RHS
				Expr e = as.getRHS();
				
				// Check special case -- Just for McLAB
				boolean bSpecial = false;
				if (as.getLHS() instanceof NameExpr) {
					String varName = ((NameExpr)as.getLHS()).getVarName();
					SymbolTableEntry entry = scope.getSymbolById(varName);
					// System.out.println("   ---" + as.getRHS().getStructureString()+" " + varName+ entry);
					if(entry==null)
							continue;
					ASTNode varDeclNode = entry.getDeclLocation();
					Type varType = ((VariableDecl)varDeclNode).getType();
					if(varType instanceof MatrixType) {
						if (as.getRHS() instanceof LiteralExpr) { 
							// tmpvar1 = 1
							bSpecial = true;
						} else if (as.getRHS() instanceof ParameterizedExpr) {
							String fname = ((NameExpr)((ParameterizedExpr)as.getRHS()).getTarget()).getName().getID();
							// tmpvar2 = ones(n, 1)
							if(fname.equalsIgnoreCase("ones") 
									|| fname.equalsIgnoreCase("zeros")									
									|| fname.equalsIgnoreCase("randn")
									|| fname.equalsIgnoreCase("rand")
									|| gLocalFuncList.contains(fname)) 
								 bSpecial = true;
						}
					} 
					if(!bSpecial) {
						if (as.getRHS() instanceof ParameterizedExpr) {
							String fname = ((NameExpr)((ParameterizedExpr)as.getRHS()).getTarget()).getName().getID();
							// tmpvar1 = ones(n, 1)
							if(fname.equalsIgnoreCase("ones") 
									|| fname.equalsIgnoreCase("zeros")									
									|| fname.equalsIgnoreCase("num2str")
									|| fname.equalsIgnoreCase("randn")
									|| fname.equalsIgnoreCase("rand")) {
								 bSpecial = true;
							} else {						
								// Special rule: i.e.: tmpvar1=user-function(a,b)  
								// because user defined function need to be transform into subroutine call, so
								bSpecial = gLocalFuncList.contains(fname);
							}
						}
					}
				}

				// RHS must not contain the same variable, i.e. index=index+1
				// If so, remove it from gDefMap
				java.util.List<ValueBox> asuseBoxes = as.getUseBoxes();
			    for(ValueBox vb: asuseBoxes) {
			    	String vboxVal = vb.getValue();
			    	if (vb.getValue().equals(as.leftBox.getValue())) {
			    		gDefMap.remove(as.leftBox.getValue());
						// System.err.println("RHS has same variable: " + as.getStructureString());
						bSpecial = true;
						break;
			    	}			    	
			    }


				// set of condition when we are not interested in collapsing
                                boolean isRange = e instanceof ast.RangeExpr;
				
				//if (e.getNumChild() > 0) System.out.println("kk: " + e.getStructureString() + " -- " +  e.getChild(0).getStructureString());				
				// if(!isRange)
				
				// Only handle simple variable, (not array)
				// Currently only to collapse temporary variables...
				if (as.getLHS() instanceof NameExpr 
						&& !(as.getParent() instanceof ForStmt)		// not in the loop index variable
						&& as.leftBox.getValue().startsWith(TEMP_VAR_PREFIX)
						&& !bSpecial	// avoid special
					) 
				{
					// If there are two assignment, then we don't know which one should be used
					// so skip it
					if(gDefMap.get(as.leftBox.getValue())!=null) {
						gDefMap.remove(as.leftBox.getValue());
						// System.out.println("   --- Removing: " + as.leftBox.getValue() + " -- " );
					} else {
						gDefMap.put(as.leftBox.getValue(), e);
						// System.out.println("   --- Adding: " + as.leftBox.getValue() + " -- " );
					}
					
				}
				
				//System.out.println("   ---" + as.getRHS() + " -- " + as.getRHS().getPrettyPrinted());
			}
			
			// replace used variables with actual definitions
		    for(ValueBox vb: useBoxes) {
		    			    	
		    	String vboxVal = vb.getValue();
		    	if (gDefMap.containsKey(vboxVal)) {
		    	
		    		// we have the definition of this var in our map, so we 
		    		// can replace the var with the RHS of the declaration, 
		    		// if it is an assignment statement
		    		
		    		Expr e = gDefMap.get(vboxVal);

		    	/*  
		    	    // The collapsing subexpressions needs consider many cases, 
		    	    // and sometime needs flow-analysis 
		    	    // to decide whether is safe or not to do the replacement.
		    	    // However, the temporary variables are guaranteed to be save replaced back.    
		    	     
		    	    // workaround for BUG in AST
		    		//  - seems that all expression that involve parentheses cannot be collapsed
		    		//  - e.g. (a + b), (a * b), sqrt(), etc.
		    		boolean isRange = (e.getClass().getName().equals(natlab.ast.RangeExpr.class.getCanonicalName()));
		    		boolean isGT = (e.getClass().getName().equals(natlab.ast.GTExpr.class.getCanonicalName()));
		    		boolean isPar = (e.getClass().getName().equals(natlab.ast.ParameterizedExpr.class.getCanonicalName()));
		    		boolean isPlus = (e.getClass().getName().equals(natlab.ast.PlusExpr.class.getCanonicalName()));
		    		boolean isMinus = (e.getClass().getName().equals(natlab.ast.MinusExpr.class.getCanonicalName()));
		    		boolean isMDiv = (e.getClass().getName().equals(natlab.ast.MDivExpr.class.getCanonicalName()));
		    		boolean isMTimes = (e.getClass().getName().equals(natlab.ast.MTimesExpr.class.getCanonicalName()));
		    		boolean isMatrix = (e.getClass().getName().equals(natlab.ast.MatrixExpr.class.getCanonicalName()));
		    		boolean isMTranspose = (e.getClass().getName().equals(natlab.ast.MTransposeExpr.class.getCanonicalName()));
		    		boolean isMPow = (e.getClass().getName().equals(natlab.ast.MPowExpr.class.getCanonicalName()));
		    		boolean isEDiv = (e.getClass().getName().equals(natlab.ast.EDivExpr.class.getCanonicalName()));
		    				    		
		    		//System.out.println("ee> " + e.getClass().getName());
		    		//System.out.println("xx> " + natlab.ast.RangeExpr.class.getCanonicalName());
		    		
		    		//if (!isRange && !isGT && !isPar && !isPlus && !isMinus && !isMDiv)
	    			//if (!isRange && !isMinus && !isEDiv && !isPar &&	    					

		    		//if (!isRange && !isPlus && !isMinus && !isEDiv && !isPar &&
	    			//	!isMDiv && !isMTimes && !isMatrix && !isMTranspose && !isMPow)
	    			 
		    	*/
	    			{		    			
	    				// Don't replace the variable in assignments that contain 
	    				// implicit conversion between two type of data
	    				boolean bReplace = true;
	    				if((codeNode instanceof AssignStmt)) {
	    					Expr lhs = ((AssignStmt)codeNode).getLHS();
	    					Expr rhs = ((AssignStmt)codeNode).getRHS();
	    					if((lhs instanceof NameExpr) &&
	    							(rhs instanceof NameExpr) && ((NameExpr)rhs).getVarName().equals(vboxVal)) {
	    						Type lhsType = TypeInferenceEngine.getVariableType(scope, lhs);
	    						Type rhsType = TypeInferenceEngine.getVariableType(scope, rhs);
	    						
	    						bReplace = TypeInferenceEngine.isEqualType(lhsType, rhsType);
	    					}	    					
		    				// Don't replace the variable in a matrix construction expression, e.g., A=[B,C];
		    				// This is the bug of gfortran, it doesn't support complex expression in matrix construction.
	    					// Benchmark 'capr'
		    				if(rhs instanceof MatrixExpr) {
		    					bReplace = false;
		    				}
	    				}
		    			//System.out.println("[Alex] replace> ["+bReplace+"] "+vboxVal + " with " + e.getPrettyPrinted() 
		    			//		+ " (" + e.getClass().getName()+") on ["+codeNode.getNodeID()+"] "+codeNode);

	    				if(bReplace) 
	    					codeNode.renaming(vboxVal, e);		    	
		    		}
		    				    	
		    	}
		    			    			   
		    }
				    
		    
	    }	
	    
	    // at this stage, the global map contains all the unused variables		
	    if(!bSilence)  System.out.println(" ggg " + gDefMap);
	    if(!bSilence)  System.out.println(" --- END alex transformation 1");
		
		return subtree;
	}

	public static ASTNode Transformation2(ASTNode subtree, SymbolTableScope scope) {
		if(!bSilence) System.out.println(" --- START alex transformation 2");
		if(!bSilence) System.out.println(" --- GOAL: reuse variables when possible");		

		// 1. store ID of note of last use for each var (using a map)
		// 2. use the map at any point to see if current node ID < ID of used var,
		//    to if the var is dead or not
		
		
		// reaching definitions analysis
		subtree.clearUseDefBoxes();
		subtree.generateUseBoxesList();		
		ReachingDefs defsAnalysis = new ReachingDefs(subtree);

		// retrieve flow sets
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();
	    	    
	    // global definitions map
	    Map<String, Integer> gDefMap = new HashMap<String, Integer>();
	    Map<String, Expr> gExpMap = new HashMap<String, Expr>();
	    
	    for(ASTNode codeNode: codeNodeList) {
	    
	    	// skip root
	    	if (codeNode == subtree) continue;

	    	//System.out.println(" nn> " +  codeNode.getNodeID() + " " + codeNode.getStructureString());
	    	
	    	
	    	// get flow sets
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// get the set of definitions
			FlowSet defSet = defsAnalysis.getEmptySet();
			afterSet.difference(beforeSet, defSet);

			// add new def to to global map:
			//    var name -> id of node
			Iterator<AssignStmt> iter = defSet.iterator();
			while (iter.hasNext()) {
				AssignStmt as = iter.next();

				gDefMap.put(as.leftBox.getValue(), new Integer(as.getNodeID()));
				gExpMap.put(as.leftBox.getValue(), as.getLHS());

				//System.out.println("   ---" + as.leftBox.getValue() );
				//System.out.println("   ---" + as.getNodeID() );
			}
			
			// get all use boxes in current node
			java.util.List<ValueBox> useBoxes = codeNode.getUseBoxes();
			
			// remove used variables from the map, so that
			// it contains only unused vars in the end
		    for(ValueBox vb: useBoxes) {
		    	
		    	String vboxVal = vb.getValue();
		    	if (gDefMap.containsKey(vboxVal)) {		    		
		    		//gDefMap.remove(vboxVal);
		    		gDefMap.put(vboxVal, codeNode.getNodeID());
		    	}
		    }
			
	    	//System.out.println(" c> " +  codeNode.getStructureString());
	    	//System.out.println(" i> " +  codeNode.getNodeID());
	    	//System.out.println(" d> " +  defSet);
	    	//System.out.println(" d> " +  gDefMap);
	    	//System.out.println(" u> " +  useBoxes);
	    	//System.out.println();
	    }	
	    
	    // at this stage, the global map contains all the unused variables

	    // revisit nodes that contain unused definitions and remove these definitions
	    // System.out.println(" d> " +  gDefMap);
	    for(ASTNode codeNode: codeNodeList) {
	    	
	    	// System.out.println(" n> " +  codeNode.getNodeID() + " " + codeNode.getStructureString());
	    	
	    	// if we have a declaration, look in the map for a dead variable
	    	// get flow sets
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// get the set of definitions
			FlowSet defSet = defsAnalysis.getEmptySet();
			afterSet.difference(beforeSet, defSet);

			// add new def to to global map, but only in the case
			// of assignment statements
			//    var name -> id of node
			Iterator<AssignStmt> iter = defSet.iterator();
			while (iter.hasNext()) {

				// here we are sure we have a definition				
				AssignStmt as = (AssignStmt)iter.next();
				
                                /*I don't know why the commented
                                  statement was here but I replaced it
                                  with the instanceof check. I know
                                  this is probably ugly but I will
                                  leave it to anton or someone else to
                                  fix. 
                                  - Jesse Doherty 2010-01-05
                                */
                                //TODO Fix this!!?!
                                //boolean isForStmt = (as.getClass().getName().equals(ast.ForStmt.class.getCanonicalName()));
                                boolean isForStmt = (Object)as instanceof ast.ForStmt;
	    		
				
				// look into the map if we have a variable with lower ID
								
				boolean found = false;
				String foundVar = null;
			    Iterator it = gDefMap.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry p = (Map.Entry)it.next();
			        
			        if ((Integer)p.getValue() < codeNode.getNodeID()) {
			        	found = true;
			        	foundVar = (String)p.getKey();
			        	System.out.println(" found> " +  foundVar + " " + (Integer)p.getValue());
			        	break;
			        }
			        	
			        //System.out.println(p.getKey() + " = " + p.getValue());	        
			        
			    }
				
				
				if (!found) continue;
				
				
				//System.out.println("reuse candidate: " + foundVar);
				
				//perform the renaming
				//System.out.println();
				//System.out.println(" before> " +  codeNode.getStructureString() );
    			//System.out.println(" replace> " +  codeNode.getVarName() + " with " + gExpMap.get(foundVar).getStructureString() + " in " + codeNode.getStructureString());
    			
				// 1. update expression map for renaming of further occurences
				gExpMap.put(codeNode.getVarName(), gExpMap.get(foundVar));
			
				
				
				// 2. remove declaration of this node too
		    	// obtain declaration node (so we can remove it too)
		    	String varName = codeNode.getVarName();
		    	SymbolTableEntry entry = scope.getSymbolById(varName);
		    	
		    	if (entry == null) continue;
		    	
		    	ASTNode declNode = entry.getDeclLocation();
		    	
		    	// remove declaration statement from AST
		    	ASTNode declNodeParent = declNode.getParent();
		    	
		    	if (declNodeParent == null) continue;
		    	
		    	int loc = declNodeParent.getIndexOfChild(declNode);
		    	declNodeParent.removeChild(loc);
				
				// 3. do actual renaming in current node
				//System.out.println(" put> " +  codeNode.getVarName() + " " +  gExpMap.get(foundVar).getStructureString());    			
				((AssignStmt)codeNode).getLHS().renaming(codeNode.getVarName(), gExpMap.get(foundVar));
				gDefMap.put(foundVar, codeNode.getNodeID());				
				
				//System.out.println(" after> " +  codeNode.getStructureString() );
				//System.out.println(" after> " +  gExpMap.get("d").getStructureString() );
				//System.out.println();
			}
			
			//
			// replace all further use boxes
			//
			
			// get all use boxes in current node
			java.util.List<ValueBox> useBoxes = codeNode.getUseBoxes();
			
			// replace used variables with actual definitions
		    for(ValueBox vb: useBoxes) {
		    			    	
		    	String vboxVal = vb.getValue();
		    	if (gExpMap.containsKey(vboxVal)) {
		    	
		    		//System.out.println(" rrr> " +  vboxVal  + " " + gExpMap.get(vboxVal).getStructureString());
		    		
		    		// we have the definition of this var in our map, so we 
		    		// can replace the var with the RHS of the declaration, 
		    		// if it is an assignment statement
		    		
		    		codeNode.renaming(vboxVal, gExpMap.get(vboxVal));
		    	}
		    }
			
	    	
	    }		
		
		System.out.println(" --- END alex transformation 2");
		
		return subtree;
	}	
	
	/**
	 * 
	 * @param subtree
	 * @param scope
	 * @return
	 * 1. Counting the number of use of variable,  
	 */
	public static ASTNode Transformation4(ASTNode subtree, SymbolTableScope scope) {
		return Transformation4(subtree, scope, new ArrayList<String>());
	}
	
	public static ASTNode Transformation4(ASTNode subtree, SymbolTableScope scope,
			java.util.List<String> candidateList) {
		if(!bSilence)  System.out.println(" --- START alex transformation 4");
		if(!bSilence)  System.out.println(" --- GOAL: remove unused definitions");

		//System.out.println(subtree.dumpTreeAll(true));
		if(candidateList==null)
			candidateList = new ArrayList<String>();
		
		// Collect parameter list of this function,
		// all assignments on those variable should not be removed.
	    ArrayList<String> gParamList = new ArrayList<String>();
		if(subtree instanceof Function) {
			for(Name pName:((Function)subtree).getOutputParamList()) 
				gParamList.add(pName.getID());
		}

		// Collect uses from declaration
		java.util.List<String> usedVarList = collectUseFromDeclaration(subtree, scope);
		if(!bSilence)  System.out.println(" --- usedVarList="+usedVarList);

		// reaching definitions analysis
		subtree.clearUseDefBoxes();
		subtree.generateUseBoxesList();		
		ReachingDefs defsAnalysis = new ReachingDefs(subtree);

		// retrieve flow sets
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();
	    	    
	    // global definitions map
	    Map<String, Integer> gDefMap = new HashMap<String, Integer>();
	    
	    // global removed variable list 
	    ArrayList<String> gVarList = new ArrayList<String>();

	    for(ASTNode codeNode: codeNodeList) {
	    
	    	// skip root
	    	if (codeNode == subtree) continue;

	    	// get flow sets
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// get the set of definitions
			FlowSet defSet = defsAnalysis.getEmptySet();
			afterSet.difference(beforeSet, defSet);

			// add new def to to global map:
			//    var name -> id of node
			Iterator<AssignStmt> iter = defSet.iterator();
			while (iter.hasNext()) {
				AssignStmt as = iter.next();
				if(as.leftBox.getValue().startsWith(TEMP_VAR_PREFIX)) {
					if(!bSilence) System.out.println("   [" + as.leftBox.getValue() +"]<"
							+gParamList.contains(as.leftBox.getValue())+"><"
							+usedVarList.contains(as.leftBox.getValue())+"><"
							+(as.getParent() instanceof ForStmt)+"><"
							);
				}

				// Don't remove assignments on parameters
				if(! gParamList.contains(as.leftBox.getValue())
						&& !usedVarList.contains(as.leftBox.getValue())	// not used in declarations
						&& !(as.getParent() instanceof ForStmt)	// don't remove loop index variable
						) 
				{
					// TODO: only remove candidate variables and temporary variables
					if(as.leftBox.getValue().startsWith(TEMP_VAR_PREFIX)
							|| (candidateList.size()==0)
							|| (candidateList.size()!=0 && candidateList.contains(as.leftBox.getValue())) )
					{
						gDefMap.put(as.leftBox.getValue(), new Integer(as.getNodeID()));
					}
					
					if(as.leftBox.getValue().startsWith(TEMP_VAR_PREFIX))
						if(!bSilence) System.out.println("   ---" + as.leftBox.getValue() +"   ---" + as.getNodeID() );
				}
			}
			
			// get all use boxes in current node
			java.util.List<ValueBox> useBoxes = codeNode.getUseBoxes();
			
			// remove used variables from the map, so that
			// it contains only unused vars in the end
		    for(ValueBox vb: useBoxes) {
		    	
		    	String vboxVal = vb.getValue();
		    	if (gDefMap.containsKey(vboxVal)) {		    		
		    		gDefMap.remove(vboxVal);
		    	}
		    }
			
	    	//System.out.println(" c> " +  codeNode.getStructureString());
	    	//System.out.println(" i> " +  codeNode.getNodeID());
	    	//System.out.println(" d> " +  defSet);
	    	//System.out.println(" d> " +  gDefMap);
	    	//System.out.println(" u> " +  useBoxes);
	    	//System.out.println();
	    }	
	    
	    // at this stage, the global map contains all the unused variables

	    // revisit nodes that contain unused definitions and remove these definitions
	    // System.out.println(" d> " +  gDefMap);
	    for(ASTNode codeNode: codeNodeList) {
	 
	    	int nodeID = codeNode.getNodeID();	    	
	    	
	    	// skip nodes that are not in the map
	    	if (!gDefMap.containsValue(nodeID)) continue;
	    
	    	
	    	// remove node from AST
	    	ASTNode parent = codeNode.getParent();
	    	int loc = parent.getIndexOfChild(codeNode);
	    	parent.removeChild(loc);
	    	
	    	// obtain declaration node (so we can remove it too)
	    	String varName = codeNode.getVarName();
	    	
	    	// Save the removed variable 
	    	// then remove its declaration node later, if there is no use whatsoever
	    	gVarList.add(varName);
	    	/*
	    	SymbolTableEntry entry = scope.getSymbolById(varName);
	    	ASTNode declNode = entry.getDeclLocation();

	    	// remove declaration statement from AST
	    	ASTNode declNodeParent = declNode.getParent();
	    	loc = declNodeParent.getIndexOfChild(declNode);
	    	declNodeParent.removeChild(loc);
	    	*/
	    }
	    
    	// remove unused declaration statements from AST
	    removeDeclaration(subtree, scope, gVarList);
			
		return subtree;		
	}
	
	/**
	 * 
	 * @param subtree
	 * @param scope
	 * @param varList : list of variables that possible not used anymore
	 * 					because some use/def statement has been removed 
	 * @return
	 */
	public static ASTNode removeDeclaration(ASTNode subtree, 
			SymbolTableScope scope, ArrayList<String> varList) {
				
		// reaching definitions analysis
		subtree.clearUseDefBoxes();
		subtree.generateUseBoxesList();		
		ReachingDefs defsAnalysis = new ReachingDefs(subtree);

		// retrieve flow sets
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();
	    	    
	    // 
	    for(ASTNode codeNode: codeNodeList) {
		    
	    	// skip root
	    	if (codeNode == subtree) continue;

			// get all use boxes in current node
			// If a variables used here, then remove it from list 
			for(ValueBox vb: (java.util.List<ValueBox>)codeNode.getUseBoxes()) {
		    	String vboxVal = vb.getValue();
		    	if (varList.contains(vboxVal)) {		    		
		    		varList.remove(vboxVal);
		    	}
		    }

			// If a variables defined here, then remove it from list 
			for(ValueBox vb: (java.util.List<ValueBox>)codeNode.getDefBoxes()) {
		    	String vboxVal = vb.getValue();
		    	if (varList.contains(vboxVal)) {		    		
		    		varList.remove(vboxVal);
		    	}
		    }
	    }
	   
	    // Remove rest variable's declaration node
	    for(String varName: varList) {
	    	SymbolTableEntry entry = scope.getSymbolById(varName);
	    	ASTNode declNode = entry.getDeclLocation();

	    	// remove declaration statement from AST
	    	ASTNode declNodeParent = declNode.getParent();
	    	int loc = declNodeParent.getIndexOfChild(declNode);
	    	declNodeParent.removeChild(loc);
	    }
	    return subtree;
	}

	/**
	 * Collect variables used in the declarations
	 * @param subtree
	 * @param scope
	 * @return: a list of variable names that used in the declarations
	 * 			for dynamic allocation 
	 */
	public static java.util.List<String> collectUseFromDeclaration(
			ASTNode subtree, SymbolTableScope stScope) 
	{
		ArrayList<String> usedVarList = new ArrayList<String>();
		HashSet<String> dimStringSet = new HashSet<String>();
		
		// McFor.dumpSymbolTable(stScope, System.out, true);
		
		for(SymbolTableEntry stEntry: stScope.symTable.values()) {
	    	if(stEntry==null) { 
	    		continue;
	    	} else if(stEntry.getDeclLocation()==null) {
	    		System.err.println("[collectUseFromDeclaration] No decl node of entry "+stEntry.getSymbol());
	    		continue;
	    	}

    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();
			ASTNode declNode = stEntry.getDeclLocation();

    		// For those dynamic shape type
			if((varType instanceof MatrixType) 
					&& (((MatrixType) varType).getSize().getDims()==null)) {
		    	Size varSize = ((MatrixType)varType).getSize();
		    	// Old version
		    	if(varSize.getVariableDims()!=null) {
					// using variable for dynamic allocation
					for (String dim : varSize.getVariableDims()) {
						dimStringSet.add(dim);
						if(!bSilence)  System.out.println(" --"+stEntry.getSymbol()+"- getVariableDims()="+dim);
					}
		    	} else if(varSize.getDynamicDims()!=null) {
					// testingVariableDim(varType, curNode);
					for (String dim : varSize.getDynamicDims()) {
						dimStringSet.add(dim);
						if(!bSilence)  System.out.println(" --"+stEntry.getSymbol()+"- getDynamicDims()="+dim);
					}
				
				}
				/*		// New version	    	
				if(varSize.getDynamicDims()!=null) {
					// testingVariableDim(varType, curNode);
					for (String dim : varSize.getDynamicDims()) {
						dimStringSet.add(dim);
						if(!bSilence)  System.out.println(" --"+stEntry.getSymbol()+"- getDynamicDims()="+dim);
					}
				} else if(varSize.getVariableDims()!=null) {
					// using variable for dynamic allocation
					for (String dim : varSize.getVariableDims()) {
						dimStringSet.add(dim);
						if(!bSilence)  System.out.println(" --"+stEntry.getSymbol()+"- getVariableDims()="+dim);
					}
				}
				*/
			}
		}

		HashSet<String> usedVarSet = new HashSet<String>();
		for(String str : dimStringSet) {
			HashSet<String> varSet = McFor.getVariableListFromString(str); 
			if(str.contains(":")) {
				System.err.println("[getVariableListFromString]["+str+"]"+varSet);
			}
			usedVarSet.addAll(varSet);
		}
		usedVarList.addAll(usedVarSet);
		return usedVarList;
	}

}
