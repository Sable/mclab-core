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
        //ConstraintsToolBox contraints=new ConstraintsToolBox(fNode);
        //forStmtArray[loopIndex]=forNode;
		//checkArrayAccessAcrossStmts();
		
	}//end SVPC Test constructor.
	

	
	/*
	 * 
	 * This function does the following.
	 * 1.In case of a nested loop compare the array subscript expression with loop index to get the upper and lower range of that loop e.g.
	 * for int i=1:1:10
	 *   for int j=1:1:9
	 *   	a(i,j)=a(j+10,i+9)
	 *   end
	 *  end 
	 * 2.Sets the index of resultArray to false if there is dependence for that particular equation.
	 * If there is no dependence for that equation then sets value true at the location of resultArray.
	 * Size of resultArray depends on the dimensions of the array under dependence testing.
	 */
	
	/*private void checkDependence(AffineExpression expr1, AffineExpression expr2,int index)
	{
		/*int upperBound=0;
		int lowerBound=0;
		
			for(int i=0;i<=loopIndex;i++)
			{
				AssignStmt assStmt= forStmtArray[i].getAssignStmt();  
				
				if(assStmt.getVarName().equals(expr1.getVariable())) //this is to compare array subscript with loop index.
				{
					if(assStmt.getRHS() instanceof RangeExpr)
					{
						RangeExpr rExpr=(RangeExpr) assStmt.getRHS();						
						if(rExpr.getUpper() instanceof IntLiteralExpr && rExpr.getLower() instanceof IntLiteralExpr)
						{
							System.out.println("i am a constraint not bounded on both sides by variables");
							IntLiteralExpr iExprUpper=(IntLiteralExpr) rExpr.getUpper();
							upperBound=iExprUpper.getValue().getValue().intValue();
							IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
							lowerBound=iExprLower.getValue().getValue().intValue();
							lowerBound=lowerBound-expr2.getC();							
						}//end of 3rd if
						else if(rExpr.getUpper() instanceof PlusExpr && (rExpr.getLower() instanceof NameExpr)|| rExpr.getLower() instanceof PlusExpr)
						{
							System.out.println("I am a constraint bounded on both sides by variables");														
						}
					}//end of 2nd if					
				}//end of 1st if
				
				/*if(assStmt.getVarName().equals(expr2.getVariable()))
				{
					if(assStmt.getRHS() instanceof RangeExpr)
					{
						RangeExpr rExpr=(RangeExpr) assStmt.getRHS();
						if(rExpr.getLower() instanceof IntLiteralExpr)
						{					
							
						}//end of 3rd if
						else if(rExpr.getLower() instanceof PlusExpr)
						{
							System.out.println("I am in equation");
							System.out.println(rExpr.getLower().getStructureString());
						}
					}//end of 2nd if					
				}//end of 1st if*/
				
		//	}//end of forLoop
			
			//AssignStmt assStmt= forNode.getAssignStmt();//This gives the assignment statement of the loop
			
		/*	if(assStmt.getRHS() instanceof RangeExpr)
			{
				RangeExpr rExpr=(RangeExpr) assStmt.getRHS();
				if(rExpr.getUpper() instanceof IntLiteralExpr)
				{					
					IntLiteralExpr iExprUpper=(IntLiteralExpr) rExpr.getUpper();*/
		/*int lowerBound=0;
		lowerBound=expr2.getLowerBound()-expr2.getC();
				
					if(lowerBound > expr1.getUpperBound())
					{				
						resultArray[index]=false;	
						dependencyFlag="No";
					}
					else
						{					
						 resultArray[index]=true;
						 dependencyFlag="Yes";
	
						}					
				//}				
				
			//}			
			/*else if(assStmt.getRHS() instanceof ColonExpr)
			{
				System.out.println("For first loop colon expr");
				
			}*/			
			
	//}//end of function checkDependence
	
	public boolean checkDependence(ConstraintsGraph cGraph)
	{
		boolean isApplicable=false;
		int graphSize=cGraph.getGraphSize();
		System.out.println("graph size is"+graphSize);
		Map cMap=cGraph.getGraphData();
		//Get Map in Set interface to get key and value
		Set s=cMap.entrySet();
		
	      //Move next key and value of Map by iterator
        Iterator it=s.iterator();
      // Map.Entry m =(Map.Entry)it.next();//get the first entry in the Graph
     // getKey is used to get key of Map
    /*   String key=(String)m.getKey();
        ConstraintsList cList1=(ConstraintsList)m.getValue();        
        AffineExpression aExpr3=(AffineExpression)cList1.getListData().getData();
        System.out.println("Affine Data"+aExpr3.getLoopVariable()+aExpr3.getVariable());*/

        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
        	Map.Entry m =(Map.Entry)it.next();
            String key1=(String)m.getKey();
            System.out.println("value for this key is"+key1);
            ConstraintsList cList=(ConstraintsList)cMap.get(key1);
            //ConstraintsList cList=(ConstraintsList)m.getValue();        return firstNode.data;
           // AffineExpression aExpr1=(AffineExpression)cList.getListData().getData();
           // System.out.println("Affine Data for expression 1 "+aExpr1.getLoopVariable()+ aExpr1.getVariable()+ aExpr1.getC());
          //  AffineExpression aExpr2=(AffineExpression)cList.getListData().getNext().getData();
         //   System.out.println("Affine Data for expression 2 "+ aExpr2.getLoopVariable()+ aExpr2.getVariable()+ aExpr2.getC());
            //m =(Map.Entry)it.next();
          //Map.Entry m =(Map.Entry)it.next();
           /*// if(key2.equals(key1))
            //{
            	AffineExpression aExpr2=(AffineExpression)m.getValue();
            	if((aExpr1.getLowerBound() instanceof IntLiteralExpr && aExpr2.getLowerBound() instanceof IntLiteralExpr) && (aExpr1.getUpperBound() instanceof IntLiteralExpr && aExpr2.getUpperBound() instanceof IntLiteralExpr)) 
            	{
            		IntLiteralExpr iExprUpper=aExpr1.getUpperBound();
					IntLiteralExpr iExprLower=aExpr2.getLowerBound();            		
            		int lowerBound=0;
            		lowerBound=iExprLower.getValue().getValue().intValue()-aExpr2.getC();
            		if(lowerBound > iExprUpper.getValue().getValue().intValue())
            		 {
            			//resultArray[index]=false;	
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
            	}
            }//end of 1st if
            key1=(String)m.getKey();
            System.out.println("key1"+key1);
            cList=(ConstraintsList)m.getValue();        
            aExpr1=(AffineExpression)cList.getListData().getData();
            System.out.println("Affine Data for the next node"+aExpr1.getLoopVariable()+aExpr1.getVariable());*/
            
          }//end of while

		return true;
		
		
	}
	
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
