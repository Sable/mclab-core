package natlab.toolkits.DependenceAnalysis;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import natlab.ast.ForStmt;
import natlab.ast.MTimesExpr;
import natlab.ast.IntLiteralExpr;
import natlab.ast.Expr;
import natlab.ast.RangeExpr;
import natlab.ast.NameExpr;
/*
 * Author:Amina Aslam
 * Date:10 Aug,2009
 * BanerjeeTest class calculates the direction vector for a dependence relation. 
 */
public class BanerjeeTest {
	
	private ForStmt forStmtArray[];
	private int LBArray[];
	private int UBArray[];
	private int A,B,U,L,N,nLoopVariables;
	public BanerjeeTest(ForStmt fArray[])
	{
		forStmtArray=new ForStmt[fArray.length];
		forStmtArray=fArray;
	}
	public void directionVectorHierarchyDriver(ConstraintsGraph cGraph)
	{
		int gSize=cGraph.getGraphSize();
		LBArray=new int[gSize*2];
		UBArray=new int[gSize*2];
		int counter=0;
		boolean dependenceFlag=false;
		AffineExpression aExpr1=null,aExpr2=null,tExpr=null;		
		Map cMap=cGraph.getGraphData();
		//Get Map in Set interface to get key and value
		Set s=cMap.entrySet();		
	    //Move next key and value of Map by iterator
        Iterator it=s.iterator();      
        while(it.hasNext())
        {   // key=value separator this by Map.Entry to get key and value
        	Map.Entry m =(Map.Entry)it.next();        	
        	String key=(String)m.getKey();      
            ConstraintsList cList1=(ConstraintsList)m.getValue();
            if(cList1.getListNode()!=null)
            {    aExpr1=cList1.getListNode().getData();    
            	 tExpr=aExpr1; 	
                 if(cList1.getListNode().getNext()!=null)
                 {   aExpr2=cList1.getListNode().getNext().getData();
                	 dependenceFlag=testDependenceInAllDirections(aExpr1,aExpr2);
                	 if(dependenceFlag)
                	 {
                		 boolean lFlag=testDependenceInLessAndAllDirections(aExpr1,aExpr2);
                		 if(lFlag){}//then expand the hierarchy}
                		 boolean gFlag=testDependenceInLessAndAllDirections(aExpr1,aExpr2);
                		 if(gFlag){}//then expand the hierarchy}                			 
                		 boolean eFlag=testDependenceInLessAndAllDirections(aExpr1,aExpr2);
                		 if(eFlag){}//then expand the hierarchy} 		 
                	 }//end of 3rd if
                 }//end of 2nd if
            }//end of 1st if
        }//end of while
	}//end of function directionVectorHierarchyDriver.
	
	public boolean testDependenceInAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		AffineExpression tExpr=aExpr1;
		boolean dependenceFlag=false;
		determineLoopVariablesInAConstraint(aExpr1,aExpr2);
	    if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr && mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	            	 for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray[i]=A;
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray[i]-=A;
		                   determineLoopBounds(tExpr);
		                   LBArray[i]=LBArray[i]*(U-L);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray[i]=LBArray[i]+temp;
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray[i]=A;
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray[i]-=A;
		                   UBArray[i]=UBArray[i]*(U-L);                    	 
		                   UBArray[i]=UBArray[i]+temp;
		                   tExpr=aExpr2;
	                    }//end of for loop
	                   	dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	if(dependenceFlag)return true;
	                   	else return dependenceFlag;
	                  }//end of 2nd if                 	
	         }//end of 1st if
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	            	 for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
	            		   //for NameExpr A=1,since A- > 0  is 0;
		                   LBArray[i]=0;
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray[i]-=A;
		                   determineLoopBounds(tExpr);
		                   LBArray[i]=LBArray[i]*(U-L);
		                   int temp=(1- ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray[i]=LBArray[i]+temp;
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray[i]=1;
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray[i]-=A;
		                   UBArray[i]=UBArray[i]*(U-L);                    	 
		                   UBArray[i]=UBArray[i]+temp;
		                   tExpr=aExpr2;
	                    }//end of for loop
	                   	dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	if(dependenceFlag)return true;
	                   	else return dependenceFlag;
	                }//end of 2nd if                 	
	         }//end of else if                  
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();	           
	          for(int i=0;i<nLoopVariables;i++)
	            {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
	            		   //for NameExpr A=1,since A- > 0  is 0;
		                   LBArray[i]=0;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray[i]-=A;
		                   determineLoopBounds(tExpr);
		                   LBArray[i]=LBArray[i]*(U-L);
		                   //int temp=0;
		                   //temp=temp*L;
		                   LBArray[i]=LBArray[i];//+temp;
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray[i]=1;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   //UBArray[i]-=0;
		                   UBArray[i]=UBArray[i]*(U-L);                    	 
		                   UBArray[i]=UBArray[i];//+temp;
		                   tExpr=aExpr2;
	               }//end of for loop
	            dependenceFlag=testDependence(aExpr1,aExpr2);
	            if(dependenceFlag)return true;
	            else return dependenceFlag;
	          }//end of else if
	    else if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr)
	            {   for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray[i]=A;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray[i]-=1;
		                   determineLoopBounds(tExpr);
		                   LBArray[i]=LBArray[i]*(U-L);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - 1);
		                   temp=temp*L;
		                   LBArray[i]=LBArray[i]+temp;
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray[i]=A;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   //UBArray[i]-=0;
		                   UBArray[i]=UBArray[i]*(U-L);                    	 
		                   UBArray[i]=UBArray[i]+temp;
		                   tExpr=aExpr2;
	                    }//end of for loop
	                   	dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	if(dependenceFlag)return true;
	                   	else return dependenceFlag;
	               }//end of if                 	
	         }//end of else if
	    return false;		
	 }	
	/*
	 * This function tests the dependence of the constraints and returns true or false
	 */
	private boolean testDependence(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		int lSum=0,uSum=0;
		for(int i=0;i<nLoopVariables;i++)
		{   lSum+=LBArray[i];
			uSum+=UBArray[i];			
		}
		if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}	
	/*
	 * This function determines the loop bounds for the constraints.Based on the number of 
	 * loops involved in the constraint equation,the UB and LB are calculated accordingly. 
	 */
	private void determineLoopVariablesInAConstraint(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		for(int i=0;i<forStmtArray.length;i++)
		 {
		    if(forStmtArray[i].getVarName().equals(aExpr1.getLoopVariable()) && forStmtArray[i].getVarName().equals(aExpr2.getLoopVariable()))
		    {
		    	nLoopVariables=1;
		    }//end of if 
		    else
		    {
		    	nLoopVariables=2;
		    }//end of else
		 }//end of for
	}//end of determineLoopVariablesInAConstraint.	
	/*
	 * This function determines the Upper and lower bounds of the loop and places them in U(upper bound) and L(lower bound) variables.
	 */
	private void determineLoopBounds(AffineExpression aExpr)
	{
		for(int i=0;i<forStmtArray.length;i++)
		 {
		    if(forStmtArray[i].getVarName().equals(aExpr.getLoopVariable()))
		    {
		    	if(forStmtArray[i].getAssignStmt().getRHS() instanceof RangeExpr)
		    	{
		    		RangeExpr rExpr=(RangeExpr) forStmtArray[i].getAssignStmt().getRHS();						
					if(rExpr.getUpper() instanceof IntLiteralExpr && rExpr.getLower() instanceof IntLiteralExpr)
					{
						U=((IntLiteralExpr)rExpr.getUpper()).getValue().getValue().intValue();
						L=((IntLiteralExpr)rExpr.getLower()).getValue().getValue().intValue();
					}//end of 3rd if 
		    	}//end of 2nd if
		    }//end of 1st if 
		 }//end of for loop
	}
	/*
	 * This function does the following computation.
	 * r+=  [0, r<0 ]
	 * 		[r, r>=0]
	 * r-=  [0, r>0 ]
	 * 		[r, r<=0]
	 */
	private void determineRealNumSign(boolean sFlag,int a)
	{ 
		if(!sFlag)//this handles the negative part of real number.
		{	if(a>0)A=0;			
			else if(a<0 || a==0)A=a;
		}
		//this handles the negative part of real number.
		if(a>0 || a==0)A=a;			
		else if(a<0)A=0;		
	}
	
	public boolean testDependenceInLessAndAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInEqualAndAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInGreaterAndAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInLessAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInLessAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInLessAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInEqualAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInEqualAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInEqualAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInGreaterAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInGreaterAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	public boolean testDependenceInGreaterAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		return true;
	}
	
}
