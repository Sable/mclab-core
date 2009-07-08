/*.........................
 * Loop Transformations And DependenceAnalysis
 * Author:Amina Aslam.
 * Creation Date:Jun12,2009
 * This class implements AST visitor interface and whenever an instance of For loop is
 * encountered or visited this class instantiates an object of annotated loop transformation
 * class.
 */
package natlab.toolkits.analysis;

import natlab.ast.Opt;
import natlab.IntNumericLiteralValue;
import natlab.ast.ASTNode;
import natlab.ast.AssignStmt;
import natlab.ast.ColonExpr;
import natlab.ast.Expr;
import natlab.ast.FPLiteralExpr;
import natlab.ast.ForStmt;
import natlab.ast.IfStmt;
import natlab.ast.IntLiteralExpr;
import natlab.ast.RangeExpr;
import natlab.ast.Stmt;
import natlab.ast.SwitchStmt;
import natlab.ast.UPlusExpr;
//import natlab.toolkits.DependenceAnalysis.SVPCTest;
import natlab.toolkits.DependenceAnalysis.DependenceAnalysisDriver;

//import natlab.looptransformations.LoopFusion;
//import natlab.looptransformations.LoopInterchange;
//import natlab.looptransformations.LoopReversal;
//import natlab.looptransformations.LoopFission;
//import natlab.looptransformations.LoopUnrolling;
/*
 * TO DO:In ForVisitor class determine the depth of loop nest and then call the appropriate test.if depth is 2 or less than 2 then call GCD.
 * else if depth is 3 or greater than 3 call Acyclic test. 
 * 
 */

public class ForVisitor implements ASTVisitor {

  	 //private static int loopNo;
	 //private static ForStmt fStmt1;
	 //private static ForStmt fStmt2;
	  private String testType;
	public ForVisitor(String tType)
	{
	
		testType=tType;
	}

	/*public int getNodeID()
	{
		int id=fStmt2.getNodeID();
		return id;
	}*/
	public void caseLoopStmt(ASTNode node){
		
		if (node instanceof ForStmt) {

			ForStmt forNode=(ForStmt) node;
			
			
			if(testType.compareTo("gcd")==0) //this thing needs to be changed aswell according to interaction diagram.
			{
					
				DependenceAnalysisDriver dAnalysisDriver=new DependenceAnalysisDriver(forNode);
				dAnalysisDriver.createConstraints();
				
			}
			
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

			

		} 
		else {
			System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
		}
		
	}
	
	public void caseIfStmt(IfStmt node){}
	public void caseSwitchStmt(SwitchStmt node){}
	public void caseBranchingStmt(ASTNode node){}
	public void caseASTNode(ASTNode node) {}


}

