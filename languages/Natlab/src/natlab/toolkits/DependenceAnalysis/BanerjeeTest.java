// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.DependenceAnalysis;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ast.ForStmt;
import ast.MTimesExpr;
import ast.IntLiteralExpr;
import ast.Expr;
import ast.RangeExpr;
import ast.NameExpr;
/*
 * Author:Amina Aslam
 * Date:10 Aug,2009
 * BanerjeeTest class calculates the direction vector for a dependence relation. 
 * TODO:I have to handle case where N<0(loop is decrementing).Currently,assumption is that N>0.
 * Currently,this test works for array subscript expressions involving two loop variables.
 */
public class BanerjeeTest {
	
	private ForStmt forStmtArray[];
	
	private int LBArray1[]; // LB for * direction
	private int UBArray1[];// UB for * direction
	private int LBArray2[]; // LB for < direction
	private int UBArray2[];// UB for < direction
	private int LBArray3[]; // LB for = direction
	private int UBArray3[];// UB for = direction
	private int LBArray4[]; // LB for > direction
	private int UBArray4[];// UB for > direction
	
	private int A,B,U,L,N,nLoopVariables;
	
	public BanerjeeTest(ForStmt fArray[])
	{
		forStmtArray=new ForStmt[fArray.length];
		forStmtArray=fArray;
	}
	
	/*
	 * TODO:If any of the constraint is independent then complete array access is independent.
	 * Replace LBArray and UBArray in appropriate functions to their respective LBArray and UBArray
	 *   
	 */
	public void directionVectorHierarchyDriver(ConstraintsGraph cGraph)
	{
	  int gSize=cGraph.getGraphSize();
	  LBArray1=new int[gSize*2];
	  UBArray1=new int[gSize*2];
	  //int counter=0;
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
          { 
        	aExpr1=cList1.getListNode().getData();    
            tExpr=aExpr1; 	
            if(cList1.getListNode().getNext()!=null)
             { 
            	aExpr2=cList1.getListNode().getNext().getData();
                dependenceFlag=testDependenceInAllDirections(aExpr1,aExpr2);
                if(dependenceFlag)
                 {
                    boolean lFlag=testDependenceInLessAndAllDirections(aExpr1,aExpr2);
                	if(lFlag)
                	{
                	  testDependenceInLessAndLessDirections(aExpr1,aExpr2);
                	  testDependenceInLessAndEqualDirections(aExpr1,aExpr2);
                	  testDependenceInLessAndGreaterDirections(aExpr1,aExpr2);
                	}//end of if
                    boolean gFlag=testDependenceInGreaterAndAllDirections(aExpr1,aExpr2);
                	if(gFlag)
                	{
                	   testDependenceInGreaterAndGreaterDirections(aExpr1,aExpr2);
                	   testDependenceInGreaterAndLessDirections(aExpr1,aExpr2);
                	   testDependenceInGreaterAndEqualDirections(aExpr1,aExpr2);
                     }//end of if                 			 
                     boolean eFlag=testDependenceInEqualAndAllDirections(aExpr1,aExpr2);
                	 if(eFlag)
                	 {//then expand the hierarchy}
                	    testDependenceInEqualAndEqualDirections(aExpr1,aExpr2);
                		testDependenceInEqualAndLessDirections(aExpr1,aExpr2);
                		testDependenceInEqualAndGreaterDirections(aExpr1,aExpr2);                		 		 
                	 }//end of  if
                 }//end of 3rd if
            }//end of 2nd if
          }//end of 1st if
        }//end of while
      }//end of function directionVectorHierarchyDriver.
	
	/*
	 * This function calculates the direction vector in (*,*) and uses following equation
	 *  LB< = (A- - B+)*(U-L)+(A-B)L
	 *  UB< = (A+ - B-)*(U-L)+(A-B)L
	 */
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
		                   LBArray1[i]=A;
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=A;
		                   determineLoopBounds(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=A;
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray1[i]-=A;
		                   UBArray1[i]=UBArray1[i]*(U-L);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
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
		                   LBArray1[i]=0;
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=A;
		                   determineLoopBounds(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L);
		                   int temp=(1- ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=1;
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray1[i]-=A;
		                   UBArray1[i]=UBArray1[i]*(U-L);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
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
		                   LBArray1[i]=0;
		                   //determineRealNumSign(true,1); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=1;
		                   determineLoopBounds(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L);
		                   //int temp=0;
		                   //temp=temp*L;
		                   //LBArray1[i]=LBArray1[i];//+temp;
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=1;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   //UBArray1[i]-=0;
		                   UBArray1[i]=UBArray1[i]*(U-L);                    	 
		                   //UBArray1[i]=UBArray1[i];//+temp;
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
		                   LBArray1[i]=A;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=1;
		                   determineLoopBounds(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - 1);
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=A;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   //UBArray1[i]-=0;
		                   UBArray1[i]=UBArray1[i]*(U-L);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
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
		{   lSum+=LBArray1[i];
			uSum+=UBArray1[i];			
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
	}//end of function
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
		calculateDependenceInLessDirection(aExpr1,aExpr2);
	    int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
	    lSum=LBArray2[0];
	    uSum=LBArray2[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray1[i];
	    	uSum+=UBArray1[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}//end of function.
	
	/*
	 * This function calculates dependence in less Direction.It uses following equations for LB and UB.
	 * LB< = (A- - B)-(U-L-N)+(A-B)L-BN
	 * UB< = (A+ - B)+(U-L-N)+(A-B)L-BN 
	 */
	private void calculateDependenceInLessDirection(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		AffineExpression tExpr=aExpr1;
		//boolean dependenceFlag=false;
		determineLoopVariablesInAConstraint(aExpr1,aExpr2);
	    if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr && mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	            	 for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray1[i]=A;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                   if(LBArray1[i] < 0){determineRealNumSign(true,LBArray1[i]);LBArray1[i]=A*-1;}
		                   else if(LBArray1[i] > 0) {determineRealNumSign(false,LBArray1[i]);LBArray1[i]=A;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L-N);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                   LBArray1[i]=LBArray1[i]-(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*N);
		                   
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=A;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray1[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                   if(UBArray1[i] < 0){
		                	   determineRealNumSign(false,UBArray1[i]);
		                	   UBArray1[i]=A*-1;
		                   }
		                   else if(UBArray1[i] > 0){determineRealNumSign(true,UBArray1[i]);UBArray1[i]=A;}
		                   UBArray1[i]=UBArray1[i]*(U-L-N);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
		                   UBArray1[i]=UBArray1[i]-(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*N);
		                   tExpr=aExpr2;
	                    }//end of for loop
	                   	//dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	//if(dependenceFlag)return true;
	                   	//else return dependenceFlag;
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
		                   LBArray1[i]=0;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                   if(LBArray1[i] < 0){determineRealNumSign(true,LBArray1[i]);LBArray1[i]=A*-1;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L-N);
		                   int temp=(1 - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                   LBArray1[i]=LBArray1[i]-(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*N);
		                   
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   UBArray1[i]=1;
		                  // determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray1[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                   UBArray1[i]=UBArray1[i]*(U-L-N);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
		                   UBArray1[i]=UBArray1[i]-(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*N);
		                   tExpr=aExpr2;
	                    }//end of for loop
	                   	//dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	//if(dependenceFlag)return true;
	                   	//else return dependenceFlag;
	                }//end of 2nd if                 	
	         }//end of else if                  
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();	           
	          for(int i=0;i<nLoopVariables;i++)
	            {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.	                    
	            		   //for NameExpr A=1,since A- > 0  is 0;
		                   LBArray1[i]=0;
		                   //determineRealNumSign(true,1); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=1;
		                   determineLoopIncrement(tExpr);
		                   LBArray1[i]=LBArray1[i]-(1*N);
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.		                   
		                   UBArray1[i]=0;                               	 
		                   UBArray1[i]=UBArray1[i]-(1*N);
		                   tExpr=aExpr2;
	               }//end of for loop
	            //dependenceFlag=testDependence(aExpr1,aExpr2);
	            //if(dependenceFlag)return true;
	            //else return dependenceFlag;
	        }//end of else if
	    else if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr)
	            {   for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray1[i]=A;
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray1[i]-=1;
		                   if(LBArray1[i] < 0){determineRealNumSign(true,LBArray1[i]);LBArray1[i]=A*-1;}
		                   else if(LBArray1[i] > 0) {determineRealNumSign(false,LBArray1[i]);LBArray1[i]=A;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray1[i]=LBArray1[i]*(U-L-N);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - 1);
		                   temp=temp*L;
		                   LBArray1[i]=LBArray1[i]+temp;
		                   LBArray1[i]=LBArray1[i]-(1*N);
		                   
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray1[i]=A;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray1[i]-=1;
		                   if(UBArray1[i] < 0){
		                	   determineRealNumSign(false,UBArray1[i]);
		                	   UBArray1[i]=A*-1;
		                   }
		                   else if(UBArray1[i] > 0){determineRealNumSign(true,UBArray1[i]);UBArray1[i]=A;}
		                   UBArray1[i]=UBArray1[i]*(U-L-N);                    	 
		                   UBArray1[i]=UBArray1[i]+temp;
		                   UBArray1[i]=UBArray1[i]-(1*N);
		                   tExpr=aExpr2;
	                    }//end of for loop
	              //     	dependenceFlag=testDependence(aExpr1,aExpr2);
	                //   	if(dependenceFlag)return true;
	                  // 	else return dependenceFlag;
	               }//end of if                 	
	         }//end of else if
	//    return false;	
	}
	
	/*
	 * This function determines the increment in loop and assigns it to variable N.
	 */
	private void determineLoopIncrement(AffineExpression aExpr)
	{
	  for(int i=0;i<forStmtArray.length;i++)
	   {
	     if(forStmtArray[i].getVarName().equals(aExpr.getLoopVariable()))
	     {
	    	if(forStmtArray[i].getAssignStmt().getRHS() instanceof RangeExpr)
	    	{
	    		RangeExpr rExpr=(RangeExpr) forStmtArray[i].getAssignStmt().getRHS();			
				if(rExpr.hasIncr()){N=((IntLiteralExpr)rExpr.getIncr()).getValue().getValue().intValue();}
				else N=1;
	    	}//end of 2nd if
	    }//end of 1st if 
	  }//end of for loop	
		
   }//end of function call.
	public boolean testDependenceInEqualAndAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		calculateDependenceInEqualDirection(aExpr1,aExpr2);
	    int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
	    lSum=LBArray3[0];
	    uSum=LBArray3[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray1[i];
	    	uSum+=UBArray1[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}//end of function call.
	
	/*
	 * This function calculates dependence in less Direction.It uses following equations for LB and UB.
	 * LB= = (A- B)-(U-L)+(A-B)L
	 * UB= = (A- B)+(U-L)+(A-B)L 
	 */
	private void calculateDependenceInEqualDirection(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		AffineExpression tExpr=aExpr1;		
		determineLoopVariablesInAConstraint(aExpr1,aExpr2);
	    if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr && mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	        	  for(int i=0;i<nLoopVariables;i++)	            
	               {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                   //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		               LBArray3[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		               //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		               LBArray3[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		               if(LBArray3[i] < 0){determineRealNumSign(true,LBArray3[i]);LBArray3[i]=A*-1;}
	                   else if(LBArray3[i] > 0) {determineRealNumSign(false,LBArray3[i]);LBArray3[i]=A;}
		               determineLoopBounds(tExpr);
		               LBArray3[i]=LBArray3[i]*(U-L);
		               int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		               temp=temp*L;
		               LBArray3[i]=LBArray3[i]+temp;
		               //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		               UBArray3[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		               //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		               UBArray3[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		               if(LBArray3[i] < 0){determineRealNumSign(true,LBArray3[i]);LBArray3[i]=A*-1;}
	                   else if(LBArray3[i] > 0) {determineRealNumSign(false,LBArray3[i]);LBArray3[i]=A;}

		               UBArray3[i]=UBArray3[i]*(U-L);                    	 
		               UBArray3[i]=UBArray3[i]+temp;
		               tExpr=aExpr2;
	                  }//end of for loop
	                   	//dependenceFlag=testDependence(aExpr1,aExpr2);
	                   	//if(dependenceFlag)return true;
	                   	//else return dependenceFlag;
	               }//end of 2nd if                 	
	         }//end of 1st if
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	        	  for(int i=0;i<nLoopVariables;i++)	            
	               {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                    LBArray3[i]=1;
		                //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                LBArray3[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                if(LBArray3[i] < 0){determineRealNumSign(true,LBArray3[i]);LBArray3[i]=A*-1;}
		                else if(LBArray3[i] > 0) {determineRealNumSign(false,LBArray3[i]);LBArray3[i]=A;}

		                determineLoopBounds(tExpr);
		                LBArray3[i]=LBArray3[i]*(U-L);
		                int temp=(1- ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                temp=temp*L;
		                LBArray3[i]=LBArray3[i]+temp;
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                UBArray3[i]=1;
		                //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                UBArray3[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                if(UBArray3[i] < 0){determineRealNumSign(true,UBArray3[i]);UBArray3[i]=A*-1;}
		                else if(UBArray3[i] > 0) {determineRealNumSign(false,UBArray3[i]);UBArray3[i]=A;}

		                UBArray3[i]=UBArray3[i]*(U-L);                    	 
		                UBArray3[i]=UBArray3[i]+temp;
		                tExpr=aExpr2;
	                    }//end of for loop	                   	
	                }//end of 2nd if                 	
	         }//end of else if                  
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();	           
	          for(int i=0;i<nLoopVariables;i++)
	            {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
	            	//for NameExpr A=1,since A- > 0  is 0;
		                LBArray3[i]=0;		                
		                UBArray3[i]=0;          
		                tExpr=aExpr2;
	            }//end of for loop	           
	        }//end of else if
	    else if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr)
	            {   for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	            		//determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray3[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray3[i]-=1;
		                   if(LBArray3[i] < 0){determineRealNumSign(true,LBArray3[i]);LBArray3[i]=A*-1;}
		                   else if(LBArray3[i] > 0) {determineRealNumSign(false,LBArray3[i]);LBArray3[i]=A;}
		                   determineLoopBounds(tExpr);
		                   LBArray3[i]=LBArray3[i]*(U-L);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - 1);
		                   temp=temp*L;
		                   LBArray3[i]=LBArray3[i]+temp;
		                   
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.		                   
		                   UBArray3[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray3[i]-=1;
		                   if(UBArray3[i] < 0){determineRealNumSign(true,UBArray3[i]);UBArray3[i]=A*-1;}
		                   else if(UBArray3[i] > 0) {determineRealNumSign(false,UBArray3[i]);UBArray3[i]=A;}
		                   UBArray3[i]=UBArray3[i]*(U-L);                    	 
		                   UBArray3[i]=UBArray3[i]+temp;
		                   tExpr=aExpr2;
	                    }//end of for loop
	            }//end of if                 	
	       }//end of else if    		
	}//end of function call.

	public boolean testDependenceInGreaterAndAllDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{		
		calculateDependenceInGreaterDirection(aExpr1,aExpr2);
	    int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
	    lSum=LBArray4[0];
	    uSum=LBArray4[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray1[i];
	    	uSum+=UBArray1[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}//end of function call.
	
	/*
	 * This function calculates dependence in less Direction.It uses following equations for LB and UB.
	 * LB< = (A - B+)-(U-L-N)+(A-B)L-AN
	 * UB< = (A - B-)+(U-L-N)+(A-B)L-AN
	 *  
	 */
	private void calculateDependenceInGreaterDirection(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		AffineExpression tExpr=aExpr1;
		//boolean dependenceFlag=false;
		determineLoopVariablesInAConstraint(aExpr1,aExpr2);
	    if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof MTimesExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          MTimesExpr mExpr2=(MTimesExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr && mExpr2.getLHS() instanceof IntLiteralExpr)
	            {   
	            	 for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray4[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray4[i]-=A;
		                   if(LBArray4[i] < 0){determineRealNumSign(true,LBArray4[i]);LBArray4[i]=A*-1;}
		                   else if(LBArray4[i] > 0) {determineRealNumSign(false,LBArray4[i]);LBArray4[i]=A;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray4[i]=LBArray4[i]*(U-L-N);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray4[i]=LBArray4[i]+temp;
		                   LBArray4[i]=LBArray4[i]+(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*N);
		                   
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray4[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray4[i]-=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
		                   if(UBArray4[i] < 0){
		                	   determineRealNumSign(false,UBArray4[i]);
		                	   UBArray4[i]=A*-1;
		                   }
		                   else if(UBArray4[i] > 0){determineRealNumSign(true,UBArray4[i]);UBArray4[i]=A;}
		                   UBArray4[i]=UBArray4[i]*(U-L-N);                    	 
		                   UBArray4[i]=UBArray4[i]+temp;
		                   UBArray4[i]=UBArray4[i]+(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*N);
		                   tExpr=aExpr2;
	                    }//end of for loop
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
		                   LBArray4[i]=1;
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray4[i]-=A;		                   
		                   if(LBArray4[i] < 0){determineRealNumSign(true,LBArray4[i]);LBArray4[i]=A*-1;}
		                   else if(LBArray4[i] > 0) {determineRealNumSign(false,LBArray4[i]);LBArray4[i]=A;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray4[i]=LBArray4[i]*(U-L-N);
		                   int temp=(1 - ((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
		                   temp=temp*L;
		                   LBArray4[i]=LBArray4[i]+temp;
		                   LBArray4[i]=LBArray4[i]+N;
		                   
		                   //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   UBArray4[i]=1;
		                   determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray4[i]-=A;
		                   if(UBArray4[i] < 0){determineRealNumSign(true,UBArray4[i]);UBArray4[i]=A*-1;}
		                   else if(UBArray4[i] > 0) {determineRealNumSign(false,UBArray4[i]);UBArray4[i]=A;}
		                   UBArray4[i]=UBArray4[i]*(U-L-N);                    	 
		                   UBArray4[i]=UBArray4[i]+temp;
		                   UBArray4[i]=UBArray4[i]+N;
		                   tExpr=aExpr2;
	                    }//end of for loop	                   	
	                }//end of 2nd if                 	
	            }//end of else if                 
	    else if((aExpr1.getIndexExpr() instanceof NameExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   NameExpr mExpr1=(NameExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();	           
	          for(int i=0;i<nLoopVariables;i++)
	            {   
	        	    //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.	                    
	            	 //for NameExpr A=1,since A- > 0  is 0;
		             determineLoopIncrement(tExpr);
		             LBArray4[i]=N;
		             //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.		                   
		             UBArray4[i]=N;
		             tExpr=aExpr2;       	        
	             }//end of for loop
	         }//end of else if
	    else if((aExpr1.getIndexExpr() instanceof MTimesExpr) && (aExpr2.getIndexExpr() instanceof NameExpr))
	      {   MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
	          NameExpr mExpr2=(NameExpr)aExpr2.getIndexExpr();
	          if(mExpr1.getLHS() instanceof IntLiteralExpr)
	            {   for(int i=0;i<nLoopVariables;i++)
	                  {   //This calculates the Lower Bound of direction vector for the no of loop variables involved in a constraint.
	                      //determineRealNumSign(false,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   LBArray4[i]=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
		                   //determineRealNumSign(true,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   LBArray4[i]-=1;
		                   if(LBArray4[i] < 0){determineRealNumSign(true,LBArray4[i]);LBArray4[i]=A*-1;}
		                   else if(LBArray4[i] > 0) {determineRealNumSign(false,LBArray4[i]);LBArray4[i]=A;}
		                   determineLoopBounds(tExpr);
		                   determineLoopIncrement(tExpr);
		                   LBArray4[i]=LBArray4[i]*(U-L-N);
		                   int temp=(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue() - 1);
		                   temp=temp*L;
		                   LBArray4[i]=LBArray4[i]+temp;
		                   LBArray4[i]=LBArray4[i]+(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*N);
		                   
		                  //This calculates the Upper Bound of direction vector for the no of loop variables involved in a constraint.
		                   determineRealNumSign(true,((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of negative part of real no. A-
		                   UBArray4[i]=A;
		                   //determineRealNumSign(false,((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()); //flag is set to false to determine the value of positive part of real no. B+
		                   UBArray4[i]-=1;
		                   if(UBArray4[i] < 0){
		                	   determineRealNumSign(false,UBArray4[i]);
		                	   UBArray4[i]=A*-1;
		                   }
		                   else if(UBArray4[i] > 0){determineRealNumSign(true,UBArray4[i]);UBArray4[i]=A;}
		                   UBArray4[i]=UBArray4[i]*(U-L-N);                    	 
		                   UBArray4[i]=UBArray4[i]+temp;
		                   UBArray4[i]=UBArray4[i]+(A*N);
		                   tExpr=aExpr2;
	                    }//end of for loop	             
	               }//end of if                 	
	         }//end of else if
 }
	/*
	 * This function calculates (<,<) direction vector for an array access.
	 */
	public boolean testDependenceInLessAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;	    
	    for(int i=0;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray2[i];
	    	uSum+=UBArray2[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	
	/*
	 * This function calculates (<,=) direction vector for an array access.
	 */
	public boolean testDependenceInLessAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		lSum=LBArray2[0];
		uSum=UBArray2[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray3[i];
	    	uSum+=UBArray3[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	
	/*
	 * This function calculates (<,>) direction vector for an array access.
	 */
	public boolean testDependenceInLessAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		lSum=LBArray2[0];
		uSum=UBArray2[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray4[i];
	    	uSum+=UBArray4[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	
	/*
	 * This function calculates (=,<) direction vector for an array access.
	 */
	public boolean testDependenceInEqualAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		lSum=LBArray3[0];
		uSum=UBArray3[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray2[i];
	    	uSum+=UBArray2[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	/*
	 * This function calculates (=,=) direction vector for an array access.
	 */
	public boolean testDependenceInEqualAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		//lSum=LBArray2[0];
		//uSum=UBArray2[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray3[i];
	    	uSum+=UBArray3[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	/*
	 * This function calculates (=,>) direction vector for an array access.
	 */
	public boolean testDependenceInEqualAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;
		lSum=LBArray2[0];
		uSum=UBArray2[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray4[i];
	    	uSum+=UBArray4[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	/*
	 * This function calculates (>,>) direction vector for an array access.
	 */
	public boolean testDependenceInGreaterAndGreaterDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;	
	    for(int i=0;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray4[i];
	    	uSum+=UBArray4[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;		
	}
	/*
	 * This function calculates (>,=) direction vector for an array access.
	 */
	public boolean testDependenceInGreaterAndEqualDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;	
		lSum=LBArray4[0];
		uSum=UBArray4[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray3[i];
	    	uSum+=UBArray3[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	
	/*
	 * This function calculates (>,<) direction vector for an array access.
	 */
	public boolean testDependenceInGreaterAndLessDirections(AffineExpression aExpr1,AffineExpression aExpr2)
	{
		int lSum=0,uSum=0;
	    int b,a;
		b=aExpr2.getC();
		a=aExpr1.getC();
		int result=b-a;	
		lSum=LBArray4[0];
		uSum=UBArray4[0];
	    for(int i=1;i<nLoopVariables;i++)
	    {
	    	lSum+=LBArray2[i];
	    	uSum+=UBArray2[i];
	    }
	    if(lSum > result || result > uSum)return false;
		else if ((lSum < result) ||(lSum== result)  && (result < uSum) || (result==uSum))return true;
		return false;
	}
	
}//end of class.
