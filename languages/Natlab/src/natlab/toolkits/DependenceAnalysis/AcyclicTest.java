package natlab.toolkits.DependenceAnalysis;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ast.MTimesExpr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.NameExpr;
import ast.PlusExpr;
import ast.MinusExpr;
import ast.Expr;
import java.io.*;

/*
 * Author:Amina Aslam test file 
 * Date:2 Jul,2009
 * When there is a constraint that is bound on both sides by a variable then Acyclic Test used to eliminate that constraint and transform 
 * it in a form on which SVPC Test can be applied.
 * 
 */

public class AcyclicTest {
	private boolean isApplicable;
	private AffineExpression tExpr1;
	private AffineExpression tExpr2;
	private File file;	
	public AcyclicTest()
	{
		isApplicable=false;
		tExpr1=null;
		tExpr2=null;
		//file=f;
	
	}
	public boolean getisApplicable()
	{   
		return isApplicable;
	}
	
	/*
	 * This function writes to file the results of Acylic test.
	 */
	/*private void writeToFile()
	{
		if(isApplicable){
			  /*try{raf.writeBytes("Applying Acylic Test:" +'\n');
			      raf.writeBytes("Acylic Test is valid for the set of constraints" +'\n');
	 	      }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
	 	    }//end of if  
		    else
		    {try{raf.writeBytes("Applying Acylic Test:" +'\n');
		      raf.writeBytes("Acylic Test is not valid for the set of constraints" +'\n');
		      }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch	    	
		    }
	}*/
	
	/* 
	 *  This function does the following
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
        {   // key=value separator this by Map.Entry to get key and value
        	Map.Entry m =(Map.Entry)it.next();        	
        	String key=(String)m.getKey();      
            ConstraintsList cList1=(ConstraintsList)m.getValue();
            if(cList1.getListNode()!=null)
            {   aExpr1=cList1.getListNode().getData();                           	             	
	           	if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof PlusExpr)
	           	 {            		 
	           		 setVariables((NameExpr)aExpr1.getLowerBound(),(PlusExpr)aExpr1.getUpperBound(),it, cList1);
	           	 }//end of if            		                            
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
		          else if(aExpr1.getLowerBound() instanceof NameExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
		          {
		           	setVariables((NameExpr)aExpr1.getLowerBound(),(MTimesExpr)aExpr1.getUpperBound(),it, cList1);	            	
		          } 
		          else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
		          {
		           	setVariables((MTimesExpr)aExpr1.getLowerBound(),(MTimesExpr)aExpr1.getUpperBound(),it, cList1);	            	
		          }  
		         else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof PlusExpr)
		          {
		           	setVariables((MTimesExpr)aExpr1.getLowerBound(),(PlusExpr)aExpr1.getUpperBound(),it, cList1);	            	
		          }
		         else if(aExpr1.getLowerBound() instanceof MTimesExpr && aExpr1.getUpperBound() instanceof MinusExpr)
		          {
		            	setVariables((MTimesExpr)aExpr1.getLowerBound(),(MinusExpr)aExpr1.getUpperBound(),it, cList1);	            	
		          }
		         else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
		         {
		            	setVariables((MinusExpr)aExpr1.getLowerBound(),(MTimesExpr)aExpr1.getUpperBound(),it, cList1);	            	
		         }
		         else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof MTimesExpr)
		         {
		            	setVariables((PlusExpr)aExpr1.getLowerBound(),(MTimesExpr)aExpr1.getUpperBound(),it, cList1);	            	
		         }
		         else if(aExpr1.getLowerBound() instanceof PlusExpr && aExpr1.getUpperBound() instanceof MinusExpr)
		         {
		            	setVariables((PlusExpr)aExpr1.getLowerBound(),(MinusExpr)aExpr1.getUpperBound(),it, cList1);	            	
		         }
		         else if(aExpr1.getLowerBound() instanceof MinusExpr && aExpr1.getUpperBound() instanceof PlusExpr)
		         {
		           	setVariables((MinusExpr)aExpr1.getLowerBound(),(PlusExpr)aExpr1.getUpperBound(),it, cList1);	            	
		         } 
           //  }//end of else
         }//end of 1st if statement
        }//end of while
        //writeToFile();
		return cGraph;		
  }//end of makeSusbtituitionForVariable function.
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by PlusExpr and UpperEnd by MinusExpr
	 */	
	private void setVariables(MinusExpr pExpr1,PlusExpr pExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=pExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=pExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && ((MinusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {      
	        	int value=((IntLiteralExpr)((MinusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
         }//end of 1st if
         if(((PlusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((PlusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue() +value;                        		
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
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()+value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if           	  	           		            	
	}//end of function

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by PlusExpr and UpperEnd by MinusExpr
	 */	
	private void setVariables(PlusExpr pExpr1,MinusExpr pExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=pExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=pExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && ((PlusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {      
	        	int value=((IntLiteralExpr)((PlusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =value + ((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
         }//end of 1st if
         if(((MinusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue() - value;                        		
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
        		 int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()-value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if           	  	           		            	
	}//end of function

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by PlusExpr and UpperEnd by MTimesExpr
	 */		
	private void setVariables(PlusExpr mExpr1,MTimesExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getRHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
     	if(tExpr1!=null && ((PlusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
        {      
	        	int value=((IntLiteralExpr)((PlusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()+value;                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
        }//end of 1st if
         if(((MTimesExpr)aExpr.getUpperBound()).getLHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()*value;                        		
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
        		int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()*value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by MinusExpr and UpperEnd by MTimesExpr
	 */		
	private void setVariables(MinusExpr mExpr1,MTimesExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getRHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
     	if(tExpr1!=null && ((MinusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
        {      
	        	int value=((IntLiteralExpr)((MinusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
        }//end of 1st if
         if(((MTimesExpr)aExpr.getUpperBound()).getLHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()*value;                        		
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
        		int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()*value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by MTimesExpr and UpperEnd by MinusExpr
	 */		
	private void setVariables(MTimesExpr mExpr1,MinusExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getRHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
     	if(tExpr1!=null && ((MTimesExpr)aExpr.getLowerBound()).getLHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
        {      
	        	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getLowerBound()).getLHS()).getValue().getValue().intValue();
	        	value =value*((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
        }//end of 1st if
         if(((MinusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
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
        		int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()-value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by MTimesExpr and UpperEnd by PlusExpr
	 */		
	private void setVariables(MTimesExpr mExpr1,PlusExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getRHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
     	if(tExpr1!=null && ((MTimesExpr)aExpr.getLowerBound()).getLHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
        {      
	        	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getLowerBound()).getLHS()).getValue().getValue().intValue();
	        	value =value*((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
        }//end of 1st if
         if(((PlusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((PlusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()+value;                        		
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
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()+value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	
	
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by MTimesExpr and UpperEnd by MTimesExpr
	 */		
	private void setVariables(MTimesExpr mExpr1,MTimesExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getRHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getRHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
     	if(tExpr1!=null && ((MTimesExpr)aExpr.getLowerBound()).getLHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
        {      
	        	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getLowerBound()).getLHS()).getValue().getValue().intValue();
	        	value =value*((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
        }//end of 1st if
         if(((MTimesExpr)aExpr.getUpperBound()).getLHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()*value;                        		
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
        		int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()*value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	
	/*
	 * This function sets the variables of a constraint bounded on lower end by NameExpr and UpperEnd by MTimesExpr
	 */		
	private void setVariables(NameExpr nExpr,MTimesExpr mExpr,Iterator it,ConstraintsList cList)
	{
		String LKey=nExpr.getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr.getRHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {        	
          		aExpr.setLowerBound(tExpr1.getLowerBound());//setting the lower bound for first constraint.
           		cList.getListNode().getNext().getData().setLowerBound(tExpr1.getLowerBound());//setting the lower bound for sub constraint of the first constraint.
         }//end of 1st if
         if(((MTimesExpr)aExpr.getUpperBound()).getLHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getLHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()*value;                        		
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
        		int value=((IntLiteralExpr)((MTimesExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()*value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if          		            	
	}//end of function

	
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
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by PlusExpr and UpperEnd by PlusExpr
	 */	
	private void setVariables(PlusExpr pExpr1,PlusExpr pExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=pExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=pExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && ((PlusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {      
	        	int value=((IntLiteralExpr)((PlusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =value + ((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue();                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
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
	}//end of function
	
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by MinusExpr and UpperEnd by MinusExpr
	 */	
	private void setVariables(MinusExpr mExpr1,MinusExpr mExpr2,Iterator it,ConstraintsList cList)
	{
		String LKey=mExpr1.getLHS().getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr2.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && ((MinusExpr)aExpr.getLowerBound()).getRHS() instanceof IntLiteralExpr )//tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {      
	        	int value=((IntLiteralExpr)((MinusExpr)aExpr.getLowerBound()).getRHS()).getValue().getValue().intValue();
	        	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
	        	IntLiteralExpr intExpr=new IntLiteralExpr();
	        	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
	        	aExpr.setLowerBound(intExpr);
	        	cList.getListNode().getNext().getData().setLowerBound(intExpr);
	        	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());	        	
         }//end of 1st if
         if(((MinusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
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
        		 int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()-value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if  	  	           		            	

	}//end of function
	
	/*
	 * This function sets the variables of a constraint bounded on lower end by NameExpr and UpperEnd by MinusExpr
	 */		
	private void setVariables(NameExpr nExpr,MinusExpr mExpr,Iterator it,ConstraintsList cList)
	{
		String LKey=nExpr.getVarName();
     	System.out.println("Key is "+LKey);
     	String UKey=mExpr.getLHS().getVarName();
     	System.out.println("Upperkey is " + UKey);
     	AffineExpression aExpr=cList.getListNode().getData();
     	searchGraph(LKey,UKey,it);
        if(tExpr1!=null && tExpr1.getLowerBound() instanceof IntLiteralExpr)
         {        	
          		aExpr.setLowerBound(tExpr1.getLowerBound());//setting the lower bound for first constraint.
           		cList.getListNode().getNext().getData().setLowerBound(tExpr1.getLowerBound());//setting the lower bound for sub constraint of the first constraint.
         }//end of 1st if
         if(((MinusExpr)aExpr.getUpperBound()).getRHS() instanceof IntLiteralExpr)
          {
        	 if(tExpr1!=null && tExpr2==null) //LKey and UKey same.
        	 {
            	int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
            	value =((IntLiteralExpr)tExpr1.getLowerBound()).getValue().getValue().intValue()-value;                        		
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
        		 int value=((IntLiteralExpr)((MinusExpr)aExpr.getUpperBound()).getRHS()).getValue().getValue().intValue();
             	value =((IntLiteralExpr)tExpr2.getLowerBound()).getValue().getValue().intValue()-value;                        		
             	IntLiteralExpr intExpr=new IntLiteralExpr();
             	Integer iObj=new Integer(value);        											
				intExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
             	aExpr.setUpperBound(intExpr);
             	cList.getListNode().getNext().getData().setUpperBound(intExpr);
             	System.out.println("New Expression value is " +intExpr.getValue().getValue().intValue());
             	isApplicable=true;                    		 
              }//end of else if
        	}//end of 2nd if  	  	           		            	
	
	}//end of function
	
	
	/*
	 * This function is used to search Constraints Graph to set values of variables.It returns the affine Expression 
	 * which is bounded by the Keys given as input to this function.
	 * 
	 */
	private void searchGraph(String LKey,String UKey, Iterator it)
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

}//end of class
