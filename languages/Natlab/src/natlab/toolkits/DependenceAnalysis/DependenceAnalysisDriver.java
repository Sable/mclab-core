package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import natlab.ast.Stmt;
import natlab.ast.ForStmt;
import natlab.ast.AssignStmt;
/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 */

public class DependenceAnalysisDriver {
	
	private ForStmt forNode;
	//private ConstraintsGraph cGraph;
	private ForStmt forStmtArray[];
	private SVPCTest svpcTest;
	private AcyclicTest acyclicTest;
	private GCDTest gcdTest;
	private int loopIndex;
	public DependenceAnalysisDriver(ForStmt fNode)	
	{	
		forNode=fNode;
		forStmtArray=new ForStmt[forNode.getNumChild()+1];
		loopIndex=0;
		forStmtArray[loopIndex]=forNode;
		System.out.println(forNode.getNumChild());
	}
	/*
	 * This function does the following 
	 * 1.Checks for tightly nested loops.
	 */	
    private void isTightlyNestedLoop(ForStmt forStmt)
	 {
			 
		  Stmt stmt=forStmt.getStmt(0);
		  if(stmt instanceof ForStmt)
		  {		  loopIndex++;			  
				  ForStmt tForStmt=(ForStmt)stmt;
				  forStmtArray[loopIndex]=tForStmt;
				  forNode=tForStmt;				  
				  isTightlyNestedLoop(tForStmt);				  
		  }//end of if
			
	}//end of function
    

	/*
	 * This function does the following.
	 * 1.This function traverses each statement of forLoop.
	 * 2.Compares the RHS and LHS of the statement,creates constraints.	   
	 * 3.Compares each statement of the loop with other statement for same array access. 
	 * 4.For each comparison(whether RHS and LHS of the same statement of different statement) it calls ApplyTests function.
	 * 5.Constraints are created per statement. 
	 * e.g. 1:a(i)=a(i)+c(i)
	 * 		2:d(i)=a(i)
	 */
	public void traverseLoopStatements()
	{
		isTightlyNestedLoop(forNode);
		int nStmts=forNode.getNumStmt();
		
		System.out.println("No of Statements"+nStmts);
		boolean aFlag=false;
		for(int i=0;i<nStmts;i++)
		{
			Stmt s=forNode.getStmt(i);
			if(s instanceof AssignStmt)
			{
				AssignStmt aStmt1=(AssignStmt)s;
				for(int j=i;j<nStmts;j++)
				  {	ConstraintsToolBox cToolBox=new ConstraintsToolBox(loopIndex+1,forStmtArray);		  
					  if(i==j)
					  {
						  System.out.println("i am in same statements block");
						  aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS());//compare LHS(sameStmt) with RHS(sameStmt)						  
						  if(aFlag){ApplyTests(cToolBox);}		  
					   }//end of if
					  else
					  {
						  Stmt bStmt=forNode.getStmt(j);
						  if(bStmt instanceof AssignStmt)
						  {
							  System.out.println("i am in different statements block");
							  AssignStmt aStmt2=(AssignStmt)bStmt;
							  aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS()); //compare LHS(previousStmt) with RHS(nextStmt)
							  if(aFlag){ApplyTests(cToolBox);}
							  aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS());//compare LHS(previousStmt) with LHS(nextStmt)
							  if(aFlag){ApplyTests(cToolBox);}
							  aFlag=cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS());//compare RHS(previousStmt) with LHS(nextStmt)
							  if(aFlag){ApplyTests(cToolBox);}							  
						  }//end of if
					  }//end of else
				  }//end of inner for loop				
			}//end of if
		}//end of for 
	}//end of traverseLoopStatements function
	
	
	
	
	/*
	 * ApplyTests function applies appropriate test on the graph for a statement and then prints the results.
	 */
public void ApplyTests(ConstraintsToolBox cToolBox)
{
		boolean isAcyclicApplicable,isSvpcApplicable=false;		
		if(cToolBox.getGraph()!=null)
		{
			ConstraintsGraph cGraph=cToolBox.getGraph();
			gcdTest=new GCDTest();
			gcdTest.calculateGcd(cGraph);
			isSvpcApplicable=gcdTest.getIsSolution();
			if(isSvpcApplicable)
			{
			   svpcTest=new SVPCTest();			 
			   isAcyclicApplicable= svpcTest.checkDependence(cGraph);			
			   System.out.println("i am in SVPC test");			
			   if (!isAcyclicApplicable)
			   {
				System.out.println("Apply Acyclic test");
				acyclicTest=new AcyclicTest();
				cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
			    isAcyclicApplicable=acyclicTest.getisApplicable();
				if(isAcyclicApplicable)
				{   System.out.println("now apply SVPC Test");
					svpcTest.checkDependence(cGraph);
				}//end of 4th if					
			}//end of 3rd if
		  }//end of 2nd if
	 }//end of if 1st if
 }//end of ApplyTests function


}
