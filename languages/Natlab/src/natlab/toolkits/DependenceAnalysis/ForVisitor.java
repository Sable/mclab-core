/*.........................
 * Loop Transformations And DependenceAnalysis
 * Author:Amina Aslam.
 * Creation Date:Jun12,2009
 * This class implements AST visitor interface and whenever an instance of For loop is
 * encountered or visited this class instantiates an object of annotated loop transformation
 * class.
 */
package natlab.toolkits.DependenceAnalysis;

import ast.Opt;
import natlab.IntNumericLiteralValue;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ColonExpr;
import ast.Expr;
import ast.FPLiteralExpr;
import ast.ForStmt;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.RangeExpr;
import ast.Stmt;
import ast.SwitchStmt;
import ast.UPlusExpr;
//import natlab.toolkits.DependenceAnalysis.SVPCTest;
import natlab.toolkits.analysis.ASTVisitor;
import ast.WhileStmt;

//import natlab.looptransformations.LoopFusion;
//import natlab.looptransformations.LoopInterchange;
//import natlab.looptransformations.LoopReversal;
//import natlab.looptransformations.LoopFission;
//import natlab.looptransformations.LoopUnrolling;


//public class ForVisitor implements ASTVisitor {
public abstract class ForVisitor implements ASTVisitor {
	
      
  	 //private static int loopNo;
	 //private static ForStmt forNode;
	 //private String fileName;
	 //private static ProfilerDriver profDriver;
	 //private static ForStmt fStmt2;
	  //private String testType;
	/*public ForVisitor(String tType)
	{
	
		testType=tType;
	}

	/*public int getNodeID()
	{
		int id=fStmt2.getNodeID();
		return id;
	}*/
	//public ForVisitor()	
	//{		
		
	//}
	/*public ForVisitor()	
	{
		profDriver =new ProfilerDriver("amna");
	}
	/*public void getForStmt(ForStmt fStmt)
	{
		fStmt=forNode;
	}*/
	//public ProfilerDriver getProfDriver() {
		//return profDriver;
	//}
	//public void setProfDriver(ProfilerDriver profDriver) {
	//	this.profDriver = profDriver;
	//}
	//public ForStmt return 
	
	/*
	 *  This function does the following. 
	 *  1.Profiles the input program.
	 *  
	 * TODO:Need to fix the flow of the program.What is done is temporary.
	 */
	//public void caseLoopStmt(ASTNode node){
		
	/*	if (node instanceof ForStmt) {
			System.out.println("profiler + ++caseLoopStmt is called by "+ node.getClass().getName());			
			ForStmt forNode=(ForStmt) node;			
			profDriver.traverseForNode(forNode); 
			
			
			
			//HeuristicEngineDriver hDriver=new HeuristicEngineDriver(profDriver.getFileName());
			//hDriver.parseXmlFile();			
			//DependenceAnalysisDriver dDriver=new DependenceAnalysisDriver(forNode);
			//dDriver.traverseLoopStatements();			
		}
		else
		{
			ForVisitor fVisitor=new ForVisitor(); 
			node.applyAllChild(fVisitor);
		}
		/*else if(node instanceof WhileStmt) {
			System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
			WhileStmt whileNode=(WhileStmt) node;
			//for(int i=0;i<whileNode.getNumStmt();i++) //to look for "For Loops" inside while loop.
			//{
				//whileNode.
			//}
			//System.out.println("No of children"+whileNode.getNumChild());
			ForVisitor fVisitor=new ForVisitor(); 
			whileNode.applyAllChild(fVisitor);
			
		}

			//forNode=(ForStmt) node;
			//ProfilerDriver profDriver =new ProfilerDriver(forNode);
			//System.out.println("Number of children are in For Visitor:"+forNode.getNumChild());
	    	//profDriver.traverseProgram(fileName);
	    
			
			//TODO:Change this option also(for the test names).And give appropriate option names.//this thing needs to be changed as well according to interaction diagram.
			
		//	if(testType.compareTo("gcd")==0) 
		//	{
				//Profiler prof =new Profiler(forNode);
				//prof.changeAST();
				
				//TODO:Give user this provision to directly call the dependence analyzer.	
				//DependenceAnalysisDriver dAnalysisDriver=new DependenceAnalysisDriver(forNode);				
				//dAnalysisDriver.traverseLoopStatements();
				
				
		//	}
			
			/*if(forNode.isEligibleForLoopReversal())
	         { 
				
	         	LoopReversal lreversal=new LoopReversal(forNode);
	         	lreversal.ApplyLoopReversal();
	         }
			 else if(forNode.isEligibleForLoopInterchange())
	         { 

	         	LoopInterchange linterchange=new LoopInterchange(forNode);
	         	linterchange.ApplyLoopInterchange();
	         }
			 
			 else if(forNode.isEligibleForLoopFission())
	         {	
				 
				forNode.setStmt(new natlab.ast.Annotation(), 0);				
	         	LoopFission lfission=new LoopFission(forNode,1);
	         	
	        
	         }
			 else if(forNode.isEligibleForLoopFusion())
	         {
				 
				 if(loopNo==1)
				  {	         	
					 fStmt1=forNode;
				  }
	         	
				 else if(loopNo==2)
	         	  {
					 fStmt2=forNode; 
					 LoopFusion lfusion=new LoopFusion(fStmt1,fStmt2);
	         		 lfusion.ApplyLoopFusion();
	         		 
	         		 
	         	  }
	          loopNo++;
	         		
	         }
			 else if(forNode.isEligibleForLoopUnrolling())
			 {
		         	LoopUnrolling lunrolling=new LoopUnrolling(forNode);		
		         	lunrolling.ApplyLoopUnrolling(10);
			 }*/

			

		//} 
		
		
	//}
	
	//public void caseIfStmt(IfStmt node){
	/*	System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
		IfStmt ifNode=(IfStmt) node;
		ForVisitor fVisitor=new ForVisitor(); 
		ifNode.applyAllChild(fVisitor);*/
	//}
	//public void caseSwitchStmt(SwitchStmt node){
	/*	System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
		SwitchStmt sNode=(SwitchStmt)node;
		ForVisitor fVisitor=new ForVisitor(); 
		sNode.applyAllChild(fVisitor);*/
	//}
	//public void caseBranchingStmt(ASTNode node){}
//	public void caseASTNode(ASTNode node) {	
		
		
	//}
	


}

