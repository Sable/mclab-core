package natlab.toolkits.DependenceAnalysis;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import natlab.ast.ForStmt;
import natlab.ast.IntLiteralExpr;
import natlab.ast.NameExpr;
import natlab.ast.PlusExpr;
import natlab.ast.MinusExpr;
import natlab.ast.Expr;

/*
 * Author:Amina Aslam
 * Date:2 Jul,2009
 * When there is a constraint that is bound on both sides by a variable then Acyclic Test used to eliminate that constraint and transform 
 * it in a form on which SVPC Test can be applied.
 * 
 */

public class AcyclicTest {
	private boolean isApplicable;
	private AffineExpression tExpr1;
	private AffineExpression tExpr2;
	
	public AcyclicTest()
	{
		isApplicable=false;
		tExpr1=null;
		tExpr2=null;
	
	}
	public boolean getisApplicable()
	{
		return isApplicable;
	}
	
	
	/* TO DO:Handle all the cases Minus Expr,NameExpr.Following cases need to be handled.
	 * 			LowerBound--------------------UpperBound.	
	 * 		1.   NameExpr---------------------NameExpr.
	 * 		2.   NameExpr---------------------MinusExpr.
	 * 		3.   MinusExpr---------------------MinusExpr.
	 * 		4.	 PlusExr-----------------------PlusExpr.
	 * This function does the following
	 * 1.Accepts Constraints graph as an input.
	 * 2.Replaces variables with constants based on the mapping in the graph.
	 * 3.Modifies the Constraints graph.
	 */
	public ConstraintsGraph makeSubstituitionForVariable(ConstraintsGraph cGraph)
	{
		
		AffineExpression aExpr1=null;		
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
            	
            	 if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof PlusExpr)
            	 {
            		 setVariables((NameExpr)aExpr1.getLowerBound(),(PlusExpr)aExpr1.getUpperBound(),it, cList1);
            	 }
            		                            
	            else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof NameExpr)
	            {
	            	setVariables((NameExpr)aExpr1.getLowerBound(),(NameExpr)aExpr1.getUpperBound(),it,cList1);
      		    }//end of else if
	            else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof PlusExpr)
	            {
	            	setVariables((PlusExpr)aExpr1.getLowerBound(),(PlusExpr)aExpr1.getUpperBound(),it, cList1);
	            }
	            else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	            {
	            	setVariables((MinusExpr)aExpr1.getLowerBound(),(MinusExpr)aExpr1.getUpperBound(),it, cList1);
	            }
	            else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof MinusExpr)
	            {
	            	setVariables((NameExpr)aExpr1.getLowerBound(),(MinusExpr)aExpr1.getUpperBound(),it, cList1);
	            	
	            }
         }//end of 1st if statement
        }//end of while
		return cGraph;		
  }//end of makeSusbtituitionForVariable function.
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by NameExpr and UpperEnd by PlusExpr
	 */
	private void setVariables(NameExpr nExpr,PlusExpr pExpr,Iterator it,ConstraintsList cList)
	{
		String LKey=nExpr.getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=pExpr.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {        	
          		aExpr.setLowerBound(tExpr1.getLowerBound());//setting the lower bound for first constraint.
           		cList.getListNode().getNext().getData().setLowerBound(tExpr1.getLowerBound());//setting the lower bound for sub constraint of the first constraint.
         }//end of 1st if
         if(((PlusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((PlusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =value + ((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
            	IntLiteralExpr intExpr=new IntLiteralExpr();
            	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
            	aExpr.setUpperBound(intExpr);
            	cList.getListNode().getNext().getData().setUpperBound(intExpr);
            	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
            	isApplicable=true;
        	 }//end of 3rd if                    	 
        	 else if(tExpr2!=null)	 //LKey and UKey different.                   	  
              {
        		 int value=((IntLiteralExpr)((PlusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =value + ((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue();                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if           	  	           		            	
	}//end of function.
	
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by NameExpr and UpperEnd by NameExprExpr
	 */
	private void setVariables(NameExpr nExpr1,NameExpr nExpr2, Iterator it,ConstraintsList cList)
	{
		String LKey=nExpr1.getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=nExpr2.getVarName();
     	System.out.println("Upperkey is " + UKey);     	
     	searchGraph(LKey,UKey,it);
     	AffineExpression aExpr=cList.getListNode().getData();
        if(tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {
          		aExpr.setLowerBound(tExpr1.getLowerBound());//setting the lower bound for first constraint.
           		cList.getListNode().getNext().getData().setLowerBound(tExpr1.getLowerBound());//setting the lower bound for sub constraint of the first constraint.          
  
                 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
                 {                     	
                        	aExpr.setUpperBound(tExpr1.getLowerBound());
                        	cList.getListNode().getNext().getData().setUpperBound(tExpr1.getLowerBound());		                        	
                        	isApplicable=true;                        	
             	   }//end of 2nd if
                  else if(tExpr2!=null)	 //LKey and UKey different.                   	  
                  {
                    	aExpr.setUpperBound(tExpr2.getLowerBound());
                    	cList.getListNode().getNext().getData().setUpperBound(tExpr2.getLowerBound());		                        	
                    	isApplicable=true;                     	  
                   }
         }//end of 1st if       
     }//end of function
	
	
	private void setVariables(PlusExpr aExpr,PlusExpr bExpr,Iterator it,ConstraintsList cList)
	{
		
	}
	private void setVariables(MinusExpr aExpr,MinusExpr bExpr,Iterator it,ConstraintsList cList)
	{
		
	}
	private void setVariables(NameExpr aExpr,MinusExpr bExpr,Iterator it,ConstraintsList cList)
	{
		
	}
	
	
	/*
	 * This function is used to search Constraints Graph to set values of variables.
	 */
	private void searchGraph(String LKey,String UKey,Iterator it)
	{

		while(it.hasNext())
     	{             		
     	    Map.Entry m =(Map.Entry)it.next();        	
        	String tkey=(String)m.getKey();      
            ConstraintsList tList=(ConstraintsList)m.getValue();
            AffineExpression tExpr=tList.getListNode().getData();
            if(LKey.equals(UKey))
            {
            	if(tExpr.getLoopVariable().equals(LKey))
            	{
            		tExpr1=tExpr;    
            		return;
            	}//end of 2nd if
            }//end of 1st if
            else //if LKey not equal to UKey. A constraint is bounded on lower and upper side by different variables.
            {
            	if(tExpr.getLoopVariable().equals(LKey)) 
            	{
            		tExpr1=tExpr;  //tExpr1 will have the value for lower bound.
            	}//end of if
            	if(tExpr.getLoopVariable().equals(UKey))
            	{
            		tExpr2=tExpr; //tExpr1 will have the value for upper bound.
            	}//end of if          	
           }//end of else
     	}//end of while
		
	}//end of function searchGraph

}
