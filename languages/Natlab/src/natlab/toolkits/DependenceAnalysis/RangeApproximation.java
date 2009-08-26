package natlab.toolkits.DependenceAnalysis;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import natlab.ast.Expr;
import natlab.ast.MTimesExpr;
import natlab.ast.MinusExpr;
import natlab.ast.NameExpr;
import natlab.ast.PlusExpr;
import natlab.ast.IntLiteralExpr;

/*
 * Author:Amina Aslam
 * Date:25 Aug,2009
 * RangeApproximation class substitutes different ranges for variables and places the results in RangeMap.
 *  
 */
public class RangeApproximation {
 public RangeApproximation(){}
 
 public void substituteVariablesWithConstantValues(ConstraintsGraph cGraph,int lRange,int uRange)
 {
  int gSize=cGraph.getGraphSize();
  AffineExpression aExpr1,aExpr2=null;
  Map cMap=cGraph.getGraphData();
  Set s=cMap.entrySet();		
  Iterator it=s.iterator(); 
  while(it.hasNext())
  {   
  	Map.Entry m =(Map.Entry)it.next();        	
  	String key=(String)m.getKey();      
    ConstraintsList cList1=(ConstraintsList)m.getValue();
    if(cList1.getListNode()!=null)
     {   aExpr1=cList1.getListNode().getData();
         if(cList1.getListNode().getNext()!=null)
         {
           aExpr2=cList1.getListNode().getNext().getData();
           if(aExpr1.getLoopVariable().equals(aExpr2.getLoopVariable()))
           {	   	 
        	 if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof PlusExpr)
           	 {   NameExpr nExpr=(NameExpr)aExpr1.getLowerBound();
           	 	 PlusExpr pExpr=(PlusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(nExpr,pExpr,aExpr1,aExpr2,lRange,uRange);
           	 }//end of if            		                            
	         else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof NameExpr)
	          {	 NameExpr nExpr1=(NameExpr)aExpr1.getLowerBound();
           	 	 NameExpr nExpr2=(NameExpr)aExpr1.getUpperBound();
           		 setLoopBounds(nExpr1,nExpr2,aExpr1,aExpr2,lRange,uRange);
      		  }//end of else if
	         else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof PlusExpr)
	          {	 PlusExpr pExpr1=(PlusExpr)aExpr1.getLowerBound();
           	 	 PlusExpr pExpr2=(PlusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(pExpr1,pExpr2,aExpr1,aExpr2,lRange,uRange);
	          }
	          else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	          {	  MinusExpr mExpr1=(MinusExpr)aExpr1.getLowerBound();
	              MinusExpr mExpr2=(MinusExpr)aExpr1.getUpperBound();
	           	  setLoopBounds(mExpr1,mExpr2,aExpr1,aExpr2,lRange,uRange);
	          }
	          else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	          {	  NameExpr nExpr=(NameExpr)aExpr1.getLowerBound();
	              MinusExpr mExpr=(MinusExpr)aExpr1.getUpperBound();
	           	  setLoopBounds(nExpr,mExpr,aExpr1,aExpr2,lRange,uRange);	            	
	          }
	          else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
	          {
	        	  NameExpr nExpr=(NameExpr)aExpr1.getLowerBound();
	           	  MTimesExpr mExpr=(MTimesExpr)aExpr1.getUpperBound();
	           	  setLoopBounds(nExpr,mExpr,aExpr1,aExpr2,lRange,uRange);	            	
	          } 
	          else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
	          {
	        	  MTimesExpr mExpr1=(MTimesExpr)aExpr1.getLowerBound();
	           	  MTimesExpr mExpr2=(MTimesExpr)aExpr1.getUpperBound();
	           	  setLoopBounds(mExpr1,mExpr2,aExpr1,aExpr2,lRange,uRange);	            	
	          }  
	         else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof PlusExpr)
	          {
	        	 MTimesExpr mExpr=(MTimesExpr)aExpr1.getLowerBound();
           	 	 PlusExpr pExpr=(PlusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(mExpr,pExpr,aExpr1,aExpr2,lRange,uRange);	            	
	          }
	         else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	          {
	        	 MTimesExpr mExpr=(MTimesExpr)aExpr1.getLowerBound();
           	 	 MinusExpr miExpr=(MinusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(mExpr,miExpr,aExpr1,aExpr2,lRange,uRange);	            	
	          }
	         else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
	         {
	        	 MinusExpr miExpr=(MinusExpr)aExpr1.getLowerBound();
           	 	 MTimesExpr mExpr=(MTimesExpr)aExpr1.getUpperBound();
           		 setLoopBounds(miExpr,mExpr,aExpr1,aExpr2,lRange,uRange);	        	            	
	         }
	         else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
	         {
	        	 PlusExpr pExpr=(PlusExpr)aExpr1.getLowerBound();
           	 	 MTimesExpr mExpr=(MTimesExpr)aExpr1.getUpperBound();
           		 setLoopBounds(pExpr,mExpr,aExpr1,aExpr2,lRange,uRange);	        	            	
	         }
	         else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	         {
	        	 PlusExpr pExpr=(PlusExpr)aExpr1.getLowerBound();
           	 	 MinusExpr miExpr=(MinusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(pExpr,miExpr,aExpr1,aExpr2,lRange,uRange);	        	            	
	         }
	         else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof PlusExpr)
	         {
	        	 MinusExpr miExpr=(MinusExpr)aExpr1.getLowerBound();
           	 	 PlusExpr pExpr=(PlusExpr)aExpr1.getUpperBound();
           		 setLoopBounds(miExpr,pExpr,aExpr1,aExpr2,lRange,uRange);	        	            	
	         }
         }//end of 3rd if 
       else {System.out.println("Not supported in the system.");}  
      }//end of 2nd if         
     }//end of 1st if
  }//end of while

}//end of function
 
 
 /*
  * These function sets the upper and lower bounds of the loop.
  * Assumptions:1.Basic assumption is that there is an increment of 1 in the loop.
  * 			2.Loop bounds variables should not change within the loop body.
  * 				e.g.for ii=2:1:n
  * 					 for jj=2:1:m	m and n should not change within the loop boundary.
  * 			3.If lower and upper bound of a loop are plus or Minus or any expression other than
  * 			  IntLiteral Expr then 	same variable should be used in the expression.
  * 				e.g. for ii=2+m:1:m+10
  *  
  */
 
 private void setLoopBounds(NameExpr nExpr,PlusExpr pExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
 {
	 int value=lRange;         		 
	 //PlusExpr pExpr=(PlusExpr)aExpr1.getUpperBound();   		 
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.
	 if(pExpr.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getRHS()).getValue().getValue().intValue()+uRange;}
	 else if(pExpr.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getLHS()).getValue().getValue().intValue()+uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.	
 }//end of function
 
 private void setLoopBounds(NameExpr nExpr1,NameExpr nExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
 {
	 int value=lRange;         		 
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.
	 value=uRange;
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.
	
 }//end of function
 
 private void setLoopBounds(PlusExpr pExpr1,PlusExpr pExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange) 
 {  
	 int value=0;         		 
		 //PlusExpr pExpr1=(PlusExpr)aExpr1.getLowerBound();
		 //PlusExpr pExpr2=(PlusExpr)aExpr1.getUpperBound();
	 if(pExpr1.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr1.getRHS()).getValue().getValue().intValue()+lRange;}
	 else if(pExpr1.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr1.getLHS()).getValue().getValue().intValue()+lRange;}
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr.		 
	 if(pExpr2.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr2.getRHS()).getValue().getValue().intValue()+uRange;}
	 else if(pExpr2.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr2.getLHS()).getValue().getValue().intValue()+uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.  
 }//end of function
 
 private void setLoopBounds(MinusExpr mExpr1,MinusExpr mExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
 {
	 int value=0;         		 
	 //MinusExpr mExpr1=(MinusExpr)aExpr1.getLowerBound();
	 //MinusExpr mExpr2=(MinusExpr)aExpr1.getUpperBound();
	 if(mExpr1.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 IntLiteralExpr iExpr=new IntLiteralExpr();
    Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.		 
 	 if(mExpr2.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr2.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable

	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.
 }//end of function

 private void setLoopBounds(NameExpr nExpr,MinusExpr mExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange) 
 {
	 int value=lRange;    	 
	 //MinusExpr mExpr=(MinusExpr)aExpr1.getUpperBound();     	 
	 IntLiteralExpr iExpr=new IntLiteralExpr();
    Integer iObj=new Integer(value);        											
	iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.		 
	if(mExpr.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable

	iObj=new Integer(value);        											
	iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.           	
 }//end of function
 

private void setLoopBounds(NameExpr nExpr,MTimesExpr mExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=lRange;         		 
	 //MTimesExpr mExpr=(MTimesExpr)aExpr1.getUpperBound();   		 
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.
	 if(mExpr.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue()*uRange;}
	 else if(mExpr.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue()*uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.	            	
 }//end of function 

private void setLoopBounds(MTimesExpr mExpr1,MTimesExpr mExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=1;         		 
	 //MTimesExpr mExpr1=(MTimesExpr)aExpr1.getLowerBound();   		 
	 //MTimesExpr mExpr2=(MTimesExpr)aExpr1.getUpperBound();
	 if(mExpr1.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()*lRange;}
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*lRange;}
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr.
	 if(mExpr2.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue()*uRange;}
	 else if(mExpr2.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.	            	
}//end of function


private void setLoopBounds(MTimesExpr mExpr1,PlusExpr pExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	int value=1;         		 
	 //MTimesExpr mExpr1=(MTimesExpr)aExpr1.getLowerBound();   		 
	 //PlusExpr pExpr=(PlusExpr)aExpr1.getUpperBound();
	 if(mExpr1.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()*lRange;}
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*lRange;}
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); 
	 aExpr2.setLowerBound(iExpr); 
	 if(pExpr.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getRHS()).getValue().getValue().intValue()+uRange;}
	 else if(pExpr.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getLHS()).getValue().getValue().intValue()+uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr. 		            	
}//end of function


private void setLoopBounds(MTimesExpr mExpr1,MinusExpr mExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=0;         		 
     if(mExpr1.getRHS() instanceof IntLiteralExpr)
	 {value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()*lRange;}
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr)
	 {value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*lRange;}
	 IntLiteralExpr iExpr=new IntLiteralExpr();
     Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.		 
	 if(mExpr2.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr2.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable

	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.          	
}//end of function


private void setLoopBounds(MinusExpr mExpr1,MTimesExpr mExpr2,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=0;         		 
     if(mExpr1.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 IntLiteralExpr iExpr=new IntLiteralExpr();
    Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr.		 
 	 if(mExpr2.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr2.getRHS()).getValue().getValue().intValue()*uRange;}
	 else if(mExpr2.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue()*uRange;}
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.           	
}//end of function

private void setLoopBounds(PlusExpr pExpr,MTimesExpr mExpr1,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=1;         		 
	 //MTimesExpr mExpr1=(MTimesExpr)aExpr1.getUpperBound();   		 
	 //PlusExpr pExpr=(PlusExpr)aExpr1.getLowerBound();
 	 if(mExpr1.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getRHS()).getValue().getValue().intValue()*uRange;}
	 else if(mExpr1.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue()*uRange;}
	 IntLiteralExpr iExpr=new IntLiteralExpr();
	 Integer iObj=new Integer(value);        											
		 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
		 aExpr1.setUpperBound(iExpr); 
		 aExpr2.setUpperBound(iExpr); 
		 if(pExpr.getRHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getRHS()).getValue().getValue().intValue()+lRange;}
	 else if(pExpr.getLHS() instanceof IntLiteralExpr){value=((IntLiteralExpr)pExpr.getLHS()).getValue().getValue().intValue()+lRange;}
		 iObj=new Integer(value);        											
		 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
		 aExpr1.setLowerBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
		 aExpr2.setLowerBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.       		            	
}//end of function


private void setLoopBounds(PlusExpr pExpr,MinusExpr mExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
     int value=0;
     if(pExpr.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)pExpr.getRHS()).getValue().getValue().intValue()+lRange;    	    
	 } 
	 else if(pExpr.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)pExpr.getLHS()).getValue().getValue().intValue()+lRange;    	    
	 } 
	 IntLiteralExpr iExpr=new IntLiteralExpr();
    Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr.		 
 	 if(mExpr.getRHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr.getLHS() instanceof IntLiteralExpr)
	 {
		value=((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue()+uRange;
	    value=value-((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable

	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.	            	
}//end of function

private void setLoopBounds(MinusExpr mExpr,PlusExpr pExpr,AffineExpression aExpr1,AffineExpression aExpr2,int lRange,int uRange)
{
	 int value=0;
	 if(mExpr.getRHS() instanceof IntLiteralExpr)
	 {	value=((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr.getRHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 else if(mExpr.getLHS() instanceof IntLiteralExpr)
	 {	value=((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue()+lRange;
	    value=value-((IntLiteralExpr)mExpr.getLHS()).getValue().getValue().intValue();
	 } //this is to avoid negative value being assigned to loop variable
	 IntLiteralExpr iExpr=new IntLiteralExpr();
    Integer iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setLowerBound(iExpr); //setting the lower bound of aExpr1 to intLiteralExpr which has a value of 1.
	 aExpr2.setLowerBound(iExpr); //setting the lower bound of aExpr2 to intLiteralExpr which has a value of 1.		 
 	 if(pExpr.getRHS() instanceof IntLiteralExpr)
	 {value=((IntLiteralExpr)pExpr.getRHS()).getValue().getValue().intValue()+uRange;}
	 else if(pExpr.getLHS() instanceof IntLiteralExpr)
	 {value=((IntLiteralExpr)pExpr.getLHS()).getValue().getValue().intValue()+uRange;}   
	 iObj=new Integer(value);        											
	 iExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	 aExpr1.setUpperBound(iExpr); //setting the upper bound of aExpr1 to intLiteralExpr.
	 aExpr2.setUpperBound(iExpr); //setting the upper bound of aExpr2 to intLiteralExpr.	            	
}//end of function


 
}//end of class.
