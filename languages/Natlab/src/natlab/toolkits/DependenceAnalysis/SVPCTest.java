package natlab.toolkits.DependenceAnalysis;
import natlab.ast.ForStmt;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import natlab.ast.Expr;
import natlab.ast.Stmt;
import natlab.ast.ParameterizedExpr;
import natlab.ast.ExprStmt;
import natlab.ast.AssignStmt;
import natlab.ast.PlusExpr;
import natlab.ast.NameExpr;
import natlab.DecIntNumericLiteralValue;
import natlab.ast.IntLiteralExpr;
import natlab.ast.MinusExpr;
import natlab.ast.Name;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import natlab.ast.RangeExpr;
import natlab.ast.ColonExpr;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import natlab.ast.EQExpr;
/*
 * Author:Amina Aslam
 * Date:15 June,2009
 * SVPC Test class determines whether there is a dependency in system of equations or not.
 */


/*
 * BasicAssumption:For SingleVariablePerConstraint test the basic assumption is that loop bounds are integers.Nested loops should be tightly nested.
 * TO DO: 
 * 1.	  Make a DataStructure For Saving SVPC Test Results And Then Determining The Dependency For The System Of Equations.
 * 2.	  Correct display of output on the output file.
 * 3.	  Do testing with real benchmarks from McLab project and look for new benchmarks.
 * 		  For the display mention the type of dependency whether it is flow,anti or output dependency.
 * 4.	  Implement this in SVPC test if there is a nested loop with depth 2 and there is a constraint which is bounded on two sides by a variable
 * 		  in this case there is no need to call Acyclic test.If the loop depth is greater than 2 and there is a constraint bounded on both sides by
 * 		  variables then call Acyclic test.
 * 5.	  Take out unnecessary functions from SVPC and make a new class named utilityFunctions or utilities.
 * 		  	
 */

public class SVPCTest {
	
	//private ForStmt forNode;
	private static boolean resultArray[];
	private File file;
	private RandomAccessFile raf;	
	private String dependencyFlag="No";
	//private ForStmt forStmtArray[]=new ForStmt[3];
	//private static int loopIndex=0;
	
	public SVPCTest()	
	{	

        try {
       
            file = new File("GcdTest.txt");
            raf = new RandomAccessFile(file, "rw");
            raf.writeBytes("Dependency B/w Stmt(i) And Stmt(j):"+'\t' + '\t'+"Dependency Result:"+'\n');
            //raf.writeBytes(""+ '\n');            
           // raf.close();
        }catch (IOException e) 
        {
        	System.out.println("IOException:");
            e.printStackTrace();
         }
       
	}//end SVPC Test constructor.

	
	public boolean checkDependence(ConstraintsGraph cGraph)
	{
		boolean isApplicable=false;		
		AffineExpression aExpr1=null,aExpr2=null;		
		Map cMap=cGraph.getGraphData();
		//Get Map in Set interface to get key and value
		Set s=cMap.entrySet();
		
	      //Move next key and value of Map by iterator
        Iterator it=s.iterator();      
        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
        	Map.Entry m =(Map.Entry)it.next();        	
        	String key=(String)m.getKey();      
            ConstraintsList cList1=(ConstraintsList)m.getValue();
            if(cList1.getListNode()!=null)
            {
            	 aExpr1=cList1.getListNode().getData();
            }
            if(cList1.getListNode().getNext()!=null)
            {
            	 aExpr2=cList1.getListNode().getNext().getData();
            }          
            
          
         	if((aExpr1.getLowerBound() instanceof IntLiteralExpr && aExpr2.getLowerBound() instanceof IntLiteralExpr) && (aExpr1.getUpperBound() instanceof IntLiteralExpr && aExpr2.getUpperBound() instanceof IntLiteralExpr)) 
        	{
        		IntLiteralExpr iExprUpper=(IntLiteralExpr)aExpr1.getUpperBound();
				IntLiteralExpr iExprLower=(IntLiteralExpr)aExpr2.getLowerBound();            		
        		int lowerBound=0;        		
        		if(aExpr2.getC()>0)	lowerBound=iExprLower.getValue().getValue().intValue()+aExpr2.getC();     		
        	    else lowerBound=iExprLower.getValue().getValue().intValue()-aExpr2.getC();        		
        		System.out.println("lower bound is" + lowerBound+aExpr2.getLoopVariable());
        		if(lowerBound > iExprUpper.getValue().getValue().intValue())
        		 {       			
        			dependencyFlag="No";
        			System.out.println("There is no dependency for this system of Equations");
        		}//end of 3rd if
        		else 
        		{
        			System.out.println("There is dependency for this system of Equations");
        		}//end of 3rd else
        		isApplicable=true;
        	}//end of 2nd if
        	else 
        	{
        		isApplicable=false;
        		return isApplicable;
        	}//end of else.    
	

        }//end of while
       return isApplicable;
		
		
	}//end of checkDependence function. 
	
	private void reportTestResult(int index)
	{
			boolean temp=false;
			for(int i=0;i<index;i++)
			{
				if(resultArray[i]==false)
				{
					System.out.println("There is no dependence for this system of equations");
					temp=true;
					return;
				}
			}
			
			if(!temp)
			{
				System.out.println("There is dependence for this system of equations");
				
			}
			
			
	}//end of function reportTestResult

	private void writeResults(AssignStmt aStmt,AssignStmt bStmt,String dependencyFlag)
	{
		System.out.println("i am in writeResults");
		try {			 
			 //raf.write(loopNo);		
			 raf.writeBytes(aStmt.getPrettyPrinted());
			 raf.writeBytes(bStmt.getPrettyPrinted()+'\t'+'\t'+'\t');
			 raf.writeBytes(dependencyFlag+'\n');
			 //raf.writeBytes(""+ '\n');
			 //raf.close();
			
		}
		catch (IOException e) {

            System.out.println("IOException:");
            e.printStackTrace();

        }

	
	}
	

}
