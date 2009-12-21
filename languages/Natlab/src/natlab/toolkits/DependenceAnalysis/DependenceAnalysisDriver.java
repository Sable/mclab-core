package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import ast.Stmt;
import ast.ForStmt;
import ast.AssignStmt;

/*
 * Author:Amina Aslam
 * Date:07 Jul,2009
 * DependenceAnalysisDriver class is the driver class for Dependence Analysis framework and drives the interaction between various tests that 
 * need to be performed on for loop statements. 
 */

public class DependenceAnalysisDriver
{
	private ForStmt forNode;
	//private ConstraintsGraph cGraph;
	private ForStmt forStmtArray[];
	private SVPCTest svpcTest;
	private AcyclicTest acyclicTest;
	private GCDTest gcdTest;
	private int loopIndex;
	private ConstraintsToolBox cToolBox;
	
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
	File file;
	RandomAccessFile raf=null;
	try {file = new File("Results.txt");
        raf = new RandomAccessFile(file, "rw");     
    }catch (IOException e) 
    {  System.out.println("IOException:Couldnot open the new file");
        e.printStackTrace();
     }//end of catch
    
	isTightlyNestedLoop(forNode);
	int nStmts=forNode.getNumStmt();
	cToolBox=new ConstraintsToolBox(loopIndex+1,forStmtArray);
	System.out.println("No of Statements"+nStmts);
	boolean aFlag=false;
	for(int i=0;i<nStmts;i++)
	{
	  Stmt s=forNode.getStmt(i);
	  if(s instanceof AssignStmt)
	  {
		AssignStmt aStmt1=(AssignStmt)s;		
		for(int j=i;j<nStmts;j++)
		{//ConstraintsToolBox cToolBox=new ConstraintsToolBox(loopIndex+1,forStmtArray);		  
	  	 if(i==j)
		 {  System.out.println("i am in same statements block");
    		try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getPrettyPrinted() +'\n');	     
	 	    }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
	 	    
			aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt1.getRHS());//compare LHS(sameStmt) with RHS(sameStmt)						  
			if(aFlag){ApplyTests(cToolBox,raf);}		  
		  }//end of if
		else
		 {Stmt bStmt=forNode.getStmt(j);
		  if(bStmt instanceof AssignStmt)
		   {  System.out.println("i am in different statements block");
			   AssignStmt aStmt2=(AssignStmt)bStmt;
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getRHS()); //compare LHS(previousStmt) with RHS(nextStmt)
			   try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getLHS().getPrettyPrinted()+ "  " + aStmt2.getRHS().getPrettyPrinted() +'\n');	     
		 	    }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch		 	    
			   if(aFlag){ApplyTests(cToolBox,raf);}
			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getLHS(),aStmt2.getLHS());//compare LHS(previousStmt) with LHS(nextStmt)
			   try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getLHS().getPrettyPrinted()+ "  " + aStmt2.getLHS().getPrettyPrinted() +'\n');	     
		 	    }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
			   if(aFlag){ApplyTests(cToolBox,raf);}
			   
			   aFlag=cToolBox.checkSameArrayAccess(aStmt1.getRHS(),aStmt2.getLHS());//compare RHS(previousStmt) with LHS(nextStmt)
			   try{raf.writeBytes("Dependency result for the statement(s): " + aStmt1.getRHS().getPrettyPrinted()+ "  " + aStmt2.getRHS().getPrettyPrinted() +'\n');	     
		 	    }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
		 	   if(aFlag){ApplyTests(cToolBox,raf);}							  
			 }//end of if
			}//end of else
		}//end of inner for loop				
	}//end of if
  }//end of for 
  try{raf.close();}catch(IOException e){System.out.println("IOException:Couldnot close the file");}	
}//end of traverseLoopStatements function	
	
	/*
	 * ApplyTests function applies appropriate test on the graph for a statement and then prints the results.
	 */
public void ApplyTests(ConstraintsToolBox cToolBox,RandomAccessFile raf)
{	boolean issvpcApplicable,isApplicable,isAcyclicApplicable=false;		
	if(cToolBox.getGraph()!=null)
	{
		ConstraintsGraph cGraph=cToolBox.getGraph();
		gcdTest=new GCDTest(raf);
		gcdTest.calculateGcd(cGraph);
		isApplicable=gcdTest.getIsSolution();
		if(isApplicable)
		{
		   //BanerjeeTest bTest=new BanerjeeTest(forStmtArray); //this should not be called here.Need to change its location. 
		   // bTest.directionVectorHierarchyDriver(cGraph); // same for this,need to change it.
			svpcTest=new SVPCTest(raf);			 
			issvpcApplicable= svpcTest.checkDependence(cGraph);			
			System.out.println("i am in SVPC test");			
			if (!issvpcApplicable)
			 {
			    System.out.println("Apply Acyclic test");
				acyclicTest=new AcyclicTest(raf);
				cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
			    isAcyclicApplicable=acyclicTest.getisApplicable();
				if(isAcyclicApplicable)
				{   System.out.println("now apply SVPC Test");
					svpcTest.checkDependence(cGraph);
				}//end of 4th if				
				//else{approximateRanges(cGraph);}
			}//end of 3rd if
		  }//end of 2nd if
	 }//end of if 1st if
 }//end of ApplyTests function


/*
 * This function does the following
 * 1.This function calculates the results for different ranges of variables involved in loop bounds.
 * 2.Creates mapping for the loopRanges and Result's file.
 * 3.Then applies the tests on the new Ranges. 
 */
public void approximateRanges(ConstraintsGraph cGraph)
 {
	RangeApproximation rApprox=new RangeApproximation();
	int lRange=0,uRange=0;
	File file;
	RandomAccessFile raf=null;
	for(int i=0;i<260;i+=10)
	{ lRange=i+1;
	  uRange=i+10;
	  rApprox.substituteVariablesWithConstantValues(cGraph,lRange,uRange);
	  try {
	       file = new File("Range"+lRange+"-"+uRange+".txt");
           raf = new RandomAccessFile(file, "rw");
           raf.writeBytes("Applying Tests for the following range"+ lRange + "-" + uRange +'\n');
      }catch (IOException e) 
      {
      	System.out.println("IOException:Couldnot open the new file");
          e.printStackTrace();
       }//end of catch
      LoopBounds lBounds=new LoopBounds();
      lBounds.setLowerBound(lRange);
      lBounds.setUpperBound(uRange);
      RangeMap rMap=new RangeMap();
      rMap.createMapping(lBounds, "Range"+lRange+"-"+uRange+".txt");
	  ApplyTests(cToolBox,raf);
	  try{
		  raf.close();
	  }catch(IOException e){System.out.println("IOException:Couldnot close the file");}
   }//end of for
 }//end of function call.

}
