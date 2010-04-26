package natlab.toolkits.DependenceAnalysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import ast.NameExpr;
import ast.MTimesExpr;
import ast.PlusExpr;
import ast.MinusExpr;
import ast.IntLiteralExpr;
import java.io.*;
import ast.Expr;

/*
 * Author:Amina Aslam
 * Date:24 Jul,2009
 * GCD Test class calculates the GCD of the Constraints' Equations.
 */

public class GCDTest {
	private long u;
	private long v;
	private long gcd;
	private long c=0;
	private boolean isSolution;
	private File file;
	
	public GCDTest()
	{   u=0;
		v=0;
		gcd=0;
		c=0;
		isSolution=false;
		//file=f;
		//try{raf.writeBytes("Start of GCD Test" + '\n');}catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
	}
	
	public void calculateGcd (ConstraintsGraph cGraph) 
	{
		
		AffineExpression aExpr1,aExpr2;		
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
            LinkedList<AffineExpression> cList=(LinkedList<AffineExpression>)m.getValue();
            /*if(cList1.getListNode()!=null)
            {
            	 aExpr1=cList1.getListNode().getData();
            }
            if(cList1.getListNode().getNext()!=null)
            {
            	 aExpr2=cList1.getListNode().getNext().getData();
            }*/
            
		//long gcd = 0;
	    long r = 0;
	    System.out.println("Size of the Array:::::"+cList.size());
	    AffineExpression aExpression[]=new AffineExpression[cList.size()];//(AffineExpression)cList.get(0);
	    cList.toArray(aExpression);
	    
	    assignValues(aExpression);
	    while (true) {
	      if (v == 0) {
	        gcd = u;
	        break;
	      }//end of if 
	      else {
	        r = u % v;
	        System.out.println ("u " + u + ", v " + v + ", u % v " + r);
	        u = v;
	        v = r;
	      }//end of else 
	    }//end of 2nd while
	    isSolution();  
      }//end of 1st while
	 

	}//end of calculateGCD function.
	
	/*
	 * This function calculates whether there is a solution to the system of equations or not.
	 * If the constant is divided by gcd then there is a solution to the system of equations.
	 * 
	 */
	private void isSolution()
	{
		if(c%gcd==0)
		{   isSolution=true;
			System.out.println("Gcd Result" + c%gcd);
			//try{raf.writeBytes("GCD result for the system of equations is:" + c%gcd +'\n');
			//}catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
		}
		else 
		{isSolution=false;
		 System.out.println("Gcd Result" + c%gcd);
		 System.out.println("There is no integer solution for the system of equations");
		 //try{raf.writeBytes("GCD result for the system of equations is:" + c%gcd +'\n');
		  //   raf.writeBytes("There is no integer solution for the system of equations." +'\n');
		 //  }catch (IOException e) {System.out.println("IOException:Couldnot write to file");}//end of catch
		}
	}//end of isSolution function
	
	public boolean getIsSolution()
	{
	    return isSolution;	
	}
	
	/*
	 * Assign values to u and v.
	 */
	private void assignValues(AffineExpression[] aExprArray)
	{    
		if(aExprArray[0].getIndexExpr() instanceof NameExpr)//e.g.i=j
	    {  	u = 1;
	    	if(aExprArray[1].getIndexExpr() instanceof NameExpr)
	    		{ v = 1;
	    		  if(aExprArray[0].getC() > 0) c=Math.abs(aExprArray[1].getC()-aExprArray[0].getC());
	    		  else if(aExprArray[0].getC() < 0) c=Math.abs(aExprArray[1].getC()+aExprArray[0].getC());
	    		  else if(aExprArray[0].getC()==0) c=Math.abs(aExprArray[1].getC());
	    		}	    	
	    }//end of if
		else if(aExprArray[0].getIndexExpr() instanceof MTimesExpr) //e.g. 2i=2j+10  2i=2j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExprArray[0].getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			
			   if(aExprArray[1].getIndexExpr() instanceof MTimesExpr) //this check is to handle these types of equations 2j+10
			   {		  
				    MTimesExpr mExpr2=(MTimesExpr)aExprArray[1].getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExprArray[0].getC() > 0) c=Math.abs(aExprArray[1].getC()-aExprArray[0].getC());
	    		    else if(aExprArray[0].getC() < 0) c=Math.abs(aExprArray[1].getC()+aExprArray[0].getC());
	    		    else if(aExprArray[0].getC()==0) c=Math.abs(aExprArray[1].getC());
	    				    
	    	   }//end of 1st if			   		
		}//end of else if
		
		else if(aExprArray[0].getIndexExpr() instanceof NameExpr) //e.g. i=2j+10  i=2j-10
	    {  	u = 1;
	    	if(aExprArray[1].getIndexExpr() instanceof MTimesExpr)//aExprArray[1].getIndexExpr() returns 2j part of the equation.
	    		{			  
	    		    MTimesExpr mExpr2=(MTimesExpr)aExprArray[1].getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExprArray[0].getC() > 0) c=Math.abs(aExprArray[1].getC()-aExprArray[0].getC());
	    		    else if(aExprArray[0].getC() < 0) c=Math.abs(aExprArray[1].getC()+aExprArray[0].getC());
	    		    else if(aExprArray[0].getC()==0) c=Math.abs(aExprArray[1].getC());	    		  
			   }//end of 2nd if			  	    	
	    }//end of else if
		
		else if(aExprArray[0].getIndexExpr() instanceof MTimesExpr) //e.g. 2i=j+10 or 2i=j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExprArray[0].getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
     		if(aExprArray[1].getIndexExpr() instanceof NameExpr) //this check is to handle these types of equations 2j+10
			   {    v = 1;
				    NameExpr mExpr2=(NameExpr)aExprArray[1].getIndexExpr();				    
	    		    if(aExprArray[0].getC() > 0) c=Math.abs(aExprArray[1].getC()-aExprArray[0].getC());
	    		    else if(aExprArray[0].getC() < 0) c=Math.abs(aExprArray[1].getC()+aExprArray[0].getC());
	    		    else if(aExprArray[0].getC()==0) c=Math.abs(aExprArray[1].getC());				  
	    		}//end of 1st if
		}//end of else if
}//end of assign values function 

	
	public void calculateGcd (AffineExpression aExpr,AffineExpression bExpr) 
	{
		
		//AffineExpression aExpr1,aExpr2;		
		//Map cMap=cGraph.getGraphData();
		//Get Map in Set interface to get key and value
		//Set s=cMap.entrySet();
		
	      //Move next key and value of Map by iterator
        //Iterator it=s.iterator();      
        //while(it.hasNext())
        //{
            // key=value separator this by Map.Entry to get key and value
        	//Map.Entry m =(Map.Entry)it.next();        	
        	//String key=(String)m.getKey();      
            //LinkedList<AffineExpression> cList=(LinkedList<AffineExpression>)m.getValue();
            /*if(cList1.getListNode()!=null)
            {
            	 aExpr1=cList1.getListNode().getData();
            }
            if(cList1.getListNode().getNext()!=null)
            {
            	 aExpr2=cList1.getListNode().getNext().getData();
            }*/
            
		//long gcd = 0;
	    long r = 0;
	    //System.out.println("Size of the Array:::::"+cList.size());
	    //AffineExpression aExpression[]=new AffineExpression[cList.size()];//(AffineExpression)cList.get(0);
	    //cList.toArray(aExpression);
	    
	    assignValues(aExpr,bExpr);
	    while (true) {
	      if (v == 0) {
	        gcd = u;
	        break;
	      }//end of if 
	      else {
	        r = u % v;
	        System.out.println ("u " + u + ", v " + v + ", u % v " + r);
	        u = v;
	        v = r;
	      }//end of else 
	    }//end of 2nd while
	    isSolution();  
     
	 
}//end of calculateGCD function.
	
	/*
	 * Assign values to u and v.
	 */
	private void assignValues(AffineExpression aExpr,AffineExpression bExpr)
	{    
		if(aExpr.getIndexExpr() instanceof NameExpr)//e.g.i=j
	    {  	u = 1;
	    	if(bExpr.getIndexExpr() instanceof NameExpr)
	    		{ v = 1;
	    		  if(aExpr.getC() > 0) c=Math.abs(bExpr.getC()-aExpr.getC());
	    		  else if(aExpr.getC() < 0) c=Math.abs(bExpr.getC()+aExpr.getC());
	    		  else if(aExpr.getC()==0) c=Math.abs(bExpr.getC());
	    		}	    	
	    }//end of if
		else if(aExpr.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=2j+10  2i=2j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			
			   if(bExpr.getIndexExpr() instanceof MTimesExpr) //this check is to handle these types of equations 2j+10
			   {		  
				    MTimesExpr mExpr2=(MTimesExpr)bExpr.getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExpr.getC() > 0) c=Math.abs(bExpr.getC()-aExpr.getC());
	    		    else if(aExpr.getC() < 0) c=Math.abs(bExpr.getC()+aExpr.getC());
	    		    else if(aExpr.getC()==0) c=Math.abs(bExpr.getC());
	    				    
	    	   }//end of 1st if			   		
		}//end of else if
		
		else if(aExpr.getIndexExpr() instanceof NameExpr) //e.g. i=2j+10  i=2j-10
	    {  	u = 1;
	    	if(bExpr.getIndexExpr() instanceof MTimesExpr)//bExpr.getIndexExpr() returns 2j part of the equation.
	    		{			  
	    		    MTimesExpr mExpr2=(MTimesExpr)bExpr.getIndexExpr();
				    v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
	    		    if(aExpr.getC() > 0) c=Math.abs(bExpr.getC()-aExpr.getC());
	    		    else if(aExpr.getC() < 0) c=Math.abs(bExpr.getC()+aExpr.getC());
	    		    else if(aExpr.getC()==0) c=Math.abs(bExpr.getC());	    		  
			   }//end of 2nd if			  	    	
	    }//end of else if
		
		else if(aExpr.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=j+10 or 2i=j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
     		if(bExpr.getIndexExpr() instanceof NameExpr) //this check is to handle these types of equations 2j+10
			   {    v = 1;
				    NameExpr mExpr2=(NameExpr)bExpr.getIndexExpr();				    
	    		    if(aExpr.getC() > 0) c=Math.abs(bExpr.getC()-aExpr.getC());
	    		    else if(aExpr.getC() < 0) c=Math.abs(bExpr.getC()+aExpr.getC());
	    		    else if(aExpr.getC()==0) c=Math.abs(bExpr.getC());				  
	    		}//end of 1st if
		}//end of else if

		
		/*else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g.2i=2j
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			if(aExpr2.getIndexExpr() instanceof MTimesExpr)
			{			   
				  MTimesExpr mExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
				  c=0;
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MTimesExpr) //e.g. 2i=2j-10
		{
			MTimesExpr mExpr1=(MTimesExpr)aExpr1.getIndexExpr();
			u=Math.abs(((IntLiteralExpr)mExpr1.getLHS()).getValue().getValue().intValue());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC());
			   }//end of 2nd if
			   else v=1;c=Math.abs(aExpr2.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof PlusExpr) //e.g. i+10=j+11
		{
			PlusExpr mExpr1=(PlusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i+10=2j+11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()-aExpr1.getC());
				
			}
			else u=1;c=Math.abs(aExpr2.getC()-aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof PlusExpr)
			{
			   if(((PlusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC()-aExpr1.getC());
			   }//end of 2nd if
			   else v=1;		c=Math.abs(aExpr2.getC()-aExpr1.getC());		
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MinusExpr) //e.g. i-10=j-11
		{
			MinusExpr mExpr1=(MinusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i-10=2j-11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()+aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-10
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
				  c=Math.abs(aExpr2.getC()+aExpr1.getC());
			   }//end of 2nd if
			   else v=1;c=Math.abs(aExpr2.getC()+aExpr1.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof MinusExpr) //e.g. i-10=j+11
		{
			MinusExpr mExpr1=(MinusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i-10=2j+11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()+aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof PlusExpr)
			{
			   if(((PlusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j+11
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((PlusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
					c=Math.abs(aExpr2.getC()+aExpr1.getC());
			   }//end of 2nd if
			   else v=1;	c=Math.abs(aExpr2.getC()+aExpr1.getC());				
			}//end of 1st if
		}//end of else if
		
		else if(aExpr1.getIndexExpr() instanceof PlusExpr) //e.g. i+10=j-11
		{
			PlusExpr mExpr1=(PlusExpr)aExpr1.getIndexExpr();
			if(mExpr1.getLHS() instanceof MTimesExpr)//2i+10=2j-11
			{
				MTimesExpr mulExpr=(MTimesExpr)mExpr1.getLHS();
				u=Math.abs(((IntLiteralExpr)mulExpr.getLHS()).getValue().getValue().intValue());
				c=Math.abs(aExpr2.getC()-aExpr1.getC());
			}
			else u=1;	c=Math.abs(aExpr2.getC()-aExpr1.getC());
			if(aExpr2.getIndexExpr() instanceof MinusExpr)
			{
			   if(((MinusExpr)aExpr2.getIndexExpr()).getLHS() instanceof MTimesExpr) //this check is to handle these types of equations 2j-11
			   {
				  MTimesExpr mulExpr2=(MTimesExpr)((MinusExpr)aExpr2.getIndexExpr()).getLHS();
				  v=Math.abs(((IntLiteralExpr)mulExpr2.getLHS()).getValue().getValue().intValue());
					c=Math.abs(aExpr2.getC()-aExpr1.getC());
			   }//end of 2nd if
			   else v=1;	c=Math.abs(aExpr2.getC()-aExpr1.getC());				
			}//end of 1st if
		}//end of else if*/

}//end of assign values function 



}
