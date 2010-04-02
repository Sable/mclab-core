package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.RandomAccessFile;

import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.MinusExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PlusExpr;
import ast.RangeExpr;
import ast.Stmt;
import ast.MTimesExpr;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ast.MDivExpr;
import ast.BinaryExpr;

/*
 * Author:Amina Aslam
 * Date:06 Jul,2009
 * ConstraintsToolBox class creates constraints from the equations and puts them in ConstraintsGraph.
 */


public class ConstraintsToolBox {
	
	//private ForStmt forNode;
	//private Stmt fStmt;
	//private String dependencyFlag="No";
	private ForStmt forStmtArray[];
	private static int loopIndex=0;
	private static boolean resultArray[];
	private ConstraintsGraph cGraph;
	
	//private Expr newExpr;
	//private Expr teExpr;
	
	
public ConstraintsToolBox(int index,ForStmt fStmtArray[])
{
		cGraph=new ConstraintsGraph();
		loopIndex=index;
		forStmtArray=new ForStmt[index];
		forStmtArray=fStmtArray;
		
}
private void writeToXMLFile(Expr aExpr,Expr bExpr,File file,int loopNumber){
	
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;
    Document document;
	try {
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.parse(file);
	
	
    Node rootNode = document.getDocumentElement();
    NodeList nList=rootNode.getChildNodes();
    for(int i=0;i<nList.getLength();i++){
    	Node tNode=(Node)nList.item(i);
    	NamedNodeMap nMap=tNode.getAttributes();
    	Node node=(Node)nMap.getNamedItem("Number");
    	if(node.getNodeValue().equals(Integer.toString(loopNumber))){           	             
            Element newNode = document.createElement("ArrayAccess");
            newNode.setAttribute(new String("access"),aExpr.getPrettyPrinted()+"	"+bExpr.getPrettyPrinted());
            newNode.setAttribute(new String("DistanceValue"),aExpr.getPrettyPrinted()+"	"+bExpr.getPrettyPrinted());
            tNode.appendChild(newNode);
            document.appendChild(rootNode); // add the rootElement to the document
    	}//end of if
     }//end of for  
	}catch (Exception e) {		
		e.printStackTrace();
	}
    
	
}
	
		
	/*
	 * This function checks whether accessed arrays are the same or not.
	 * e.g. for int i=1:1:10
	 *  		a(i,j)=a(i+10,j-9)
	 *  	end
	 */	
public boolean checkSameArrayAccess(Expr aExpr,Expr bExpr,DependenceData dData)
 {		 
	
	//System.out.println(params.toString()+ " size:: "+params.size());
	boolean aFlag=false;	
	if(aExpr instanceof ParameterizedExpr)
	 { 	
		Vector<ParameterizedExpr> params=new Vector<ParameterizedExpr>();
		tokenizeExpression(bExpr,params);
		Iterator it =params.iterator();
   	    while(it.hasNext())	    		 
   	    {   
   	    	Expr tExpr=(Expr)it.next();   	    				
   	    	if(aExpr.getVarName().equals(tExpr.getVarName()))
			   {      	    		   
   	    		   System.out.println("I am in parameterized expression");
			       System.out.println(((ParameterizedExpr)tExpr).getPrettyPrinted());
			 	   makeEquationsForSubscriptExprs(aExpr,tExpr,dData);
			 	   dData.setArrayAccess(aExpr.getPrettyPrinted()+"	"+tExpr.getPrettyPrinted());
			 	   //writeToXMLFile(aExpr,bExpr,f,loopNumber);
			 	   if(ApplyTests()){
			 		 dData.setDependence('y'); 
				     aFlag=true;
			 	   }
			 	   else dData.setDependence('n');
			   }//end of if
   	    }//end of while
	 }//end of if
	
		/*  if(bExpr instanceof ParameterizedExpr)
	 }
	       {  if(aExpr.getVarName().equals(bExpr.getVarName()))
			   {   System.out.println("I am in parameterized expression");								  
			 	   makeEquationsForSubscriptExprs(aExpr,bExpr);
				   aFlag=true;
			   }//end of 3rd if
			}//end of 2nd if 		
	      else 
	       { newExpr=bExpr;
	         //teExpr=bExpr;
	    	 Iterator it =newExpr.iterator();
	    	 while(it.hasNext())	    		 
	    	   { Expr tExpr=tokenizeExpression(newExpr);    		  
 	    		 if(tExpr!=null)
	    	      {System.out.println(tExpr.getPrettyPrinted());	    	   
     	           if(aExpr.getVarName().equals(tExpr.getVarName()))
		            {   System.out.println("I am in parameterized expression");								  
		 	            makeEquationsForSubscriptExprs(aExpr,tExpr);
			            aFlag=true;
		            }//end of 2nd if
 	    		  }//end of 1st if
	    	     else break; 
	    	   }//end of while
     	   }//end of else
		  /*else if (bExpr instanceof PlusExpr)
		  	{PlusExpr pExpr=(PlusExpr)bExpr;					  
	         if(aExpr.getVarName().equals(pExpr.getLHS().getVarName()))
	    	  {   System.out.println("I am in plus expression for LHS");
	    	      makeEquationsForSubscriptExprs(aExpr,pExpr.getLHS());
	    	      aFlag=true;							    		  
			  }						    
	         if(aExpr.getVarName().equals(pExpr.getRHS().getVarName()))
		  	  {   System.out.println("I am in plus expression RHS");
		    	  makeEquationsForSubscriptExprs(aExpr,pExpr.getRHS());
		    	  aFlag=true;						    	
			  }					
		}//end of PlusExpr else if*/						    
   // }//end of ParameterizedExpr if
  	return aFlag;						
}//end of function checkSameArrayAccess.

/*
 * ApplyTests function applies appropriate test on the graph for a statement and then prints the results.
 */
public boolean ApplyTests(){	
boolean issvpcApplicable,isApplicable,isAcyclicApplicable=false;	
boolean dFlag=false;
if(cGraph.getGraphSize()>0)
{
	//ConstraintsGraph cGraph=cToolBox.getGraph();
	GCDTest gcdTest=new GCDTest();
	gcdTest.calculateGcd(cGraph);
	isApplicable=gcdTest.getIsSolution();
	dFlag=isApplicable;
	if(isApplicable)
	{
	   //BanerjeeTest bTest=new BanerjeeTest(forStmtArray); //this should not be called here.Need to change its location. 
	   // bTest.directionVectorHierarchyDriver(cGraph); // same for this,need to change it.
		SVPCTest svpcTest=new SVPCTest();			 
		issvpcApplicable= svpcTest.checkDependence(cGraph);
		dFlag=issvpcApplicable;
		System.out.println("i am in SVPC test");			
		if (!issvpcApplicable)
		 {
		    System.out.println("Apply Acyclic test");
			AcyclicTest acyclicTest=new AcyclicTest();
			cGraph=acyclicTest.makeSubstituitionForVariable(cGraph);
		    isAcyclicApplicable=acyclicTest.getisApplicable();
		    dFlag=isAcyclicApplicable;
			if(isAcyclicApplicable)
			{   System.out.println("now apply SVPC Test");
				dFlag=svpcTest.checkDependence(cGraph);
			}//end of 4th if				
			//else{approximateRanges(cGraph);}
		}//end of 3rd if
	  }//end of 2nd if
 }//end of if 1st if
return dFlag;
}//end of ApplyTests function

/*
 * This function tokenizes the loop statement and returns a parameterized expression.
 * e.g.f(ii, jj) = f(ii, jj)+mask(ii, jj)*(0.25*(f(ii-1, jj)+f(ii+1, jj)+f(ii, jj-1)+f(ii, jj+1))-f(ii, jj));
 * The function will return f(ii,jj) in first iteration.Then this f(ii, jj+1)) token and continues until end of expression. 
 * 
 */
/*private Expr tokenizeExpression(Expr expr)
{	
 if(newExpr==teExpr)
 {
	 return null;
 }//end of if
 else if(expr instanceof ParameterizedExpr && newExpr!=teExpr)
  {
	  teExpr=expr;
	  return teExpr;
  }//end of else if 
 else if(expr instanceof MDivExpr)
 {
	 //System.out.println(expr.getPrettyPrinted());
	 MDivExpr mdExpr=(MDivExpr)expr;
	 System.out.println(mdExpr.getLHS().getPrettyPrinted());
	 //System.out.println(pExpr.getLHS().getClass().toString());
	 System.out.println(mdExpr.getRHS().getPrettyPrinted());
	 //System.out.println(pExpr.getRHS().getClass().toString());	 
	 if(mdExpr.getLHS() instanceof ParameterizedExpr){ newExpr=mdExpr.getRHS();return mdExpr.getLHS();}
	 else
	 { if(!(mdExpr.getLHS().getClass().toString().equals("class ast.IntLiteralExpr")) || !(mdExpr.getLHS().getClass().toString().equals("class ast.FPLiteralExpr")))
	   { newExpr=mdExpr.getLHS();
	     if(mdExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=mdExpr.getRHS();
	 	    return teExpr;
	      }
	   }//end of if
   }//end of else
	 if(mdExpr.getRHS() instanceof ParameterizedExpr) return mdExpr.getRHS();
	 else 
	 { if(!(mdExpr.getLHS().getClass().equals("class ast.IntLiteralExpr")) || !(mdExpr.getLHS().getClass().equals("class ast.FPLiteralExpr")))
		{tokenizeExpression(mdExpr.getRHS());}		 
	 }
 }//end of else if
 else if(expr instanceof PlusExpr)
   {
	 PlusExpr pExpr=(PlusExpr)expr;
	 System.out.println(pExpr.getLHS().getPrettyPrinted());
	 System.out.println(pExpr.getLHS().getClass().toString());
	 System.out.println(pExpr.getRHS().getPrettyPrinted());
	 System.out.println(pExpr.getRHS().getClass().toString());	 
	 if(pExpr.getLHS() instanceof ParameterizedExpr){ newExpr=pExpr.getRHS();return pExpr.getLHS();}
	 else
	 { if(!(pExpr.getLHS().getClass().toString().equals("class ast.IntLiteralExpr")) || !(pExpr.getLHS().getClass().toString().equals("class ast.FPLiteralExpr")))
	   { newExpr=pExpr.getLHS();
	     if(pExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=pExpr.getRHS();
	 	    return teExpr;
	      }
	   }//end of if
     }//end of else
	 if(pExpr.getRHS() instanceof ParameterizedExpr) return pExpr.getRHS();
	 else 
	 { if(!(pExpr.getLHS().getClass().equals("class ast.IntLiteralExpr")) || !(pExpr.getLHS().getClass().equals("class ast.FPLiteralExpr")))
		{tokenizeExpression(pExpr.getRHS());}		 
	 }
   }//end of else if 
  else if (expr instanceof MTimesExpr)
  {
	 MTimesExpr mExpr=(MTimesExpr)expr;
	 System.out.println(mExpr.getLHS().getPrettyPrinted());
	 System.out.println(mExpr.getLHS().getClass().toString());
	 System.out.println(mExpr.getRHS().getPrettyPrinted());
	 System.out.println(mExpr.getRHS().getClass().toString());
 	 if(mExpr.getLHS() instanceof ParameterizedExpr){newExpr=mExpr.getRHS(); return mExpr.getLHS();}
	 else
	  {if(!(mExpr.getLHS().getClass().toString().equals("class ast.IntLiteralExpr")) && !(mExpr.getLHS().getClass().toString().equals("class ast.FPLiteralExpr")))
		 {newExpr=mExpr.getLHS();
	     if(mExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=mExpr.getRHS();
	 	    return teExpr;
	      }
	     }		 
       }//end of else  	 
	 if(mExpr.getRHS() instanceof ParameterizedExpr) return mExpr.getRHS();
	 else 
	  {if(!(mExpr.getRHS().getClass().toString().equals("class ast.IntLiteralExpr")) && !(mExpr.getRHS().getClass().toString().equals("class ast.FPLiteralExpr")))
		 {tokenizeExpression(mExpr.getRHS());return teExpr;}		 
	  }
  }//end of else if 
 else if (expr instanceof MinusExpr)
  {
	 MinusExpr miExpr=(MinusExpr)expr;
	 System.out.println("LHS:"+miExpr.getLHS().getPrettyPrinted());
	 System.out.println("RHS:"+miExpr.getRHS().getPrettyPrinted());
 	 if(miExpr.getLHS() instanceof ParameterizedExpr){newExpr=miExpr.getRHS(); return miExpr.getLHS();}
	 else
	  {if(!(miExpr.getLHS().getClass().toString().equals("class ast.IntLiteralExpr")) && !(miExpr.getLHS().getClass().toString().equals("class ast.FPLiteralExpr")))
		{newExpr=miExpr.getLHS();
	     if(miExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=miExpr.getRHS();
	 	    return teExpr;
	      }
	     }
	  }
	 if(miExpr.getRHS() instanceof ParameterizedExpr) return miExpr.getRHS();
	 else
	  {if(!(miExpr.getRHS().getClass().toString().equals("class ast.IntLiteralExpr")) && !(miExpr.getRHS().getClass().toString().equals("class ast.FPLiteralExpr")))
		 {tokenizeExpression(miExpr.getRHS());}
	 }
  }//end of else if
    return null;	
}//end of function*/


/*
 * This function tokenizes the loop statement and returns a parameterized expression.
 * e.g.f(ii, jj) = f(ii, jj)+mask(ii, jj)*(0.25*(f(ii-1, jj)+f(ii+1, jj)+f(ii, jj-1)+f(ii, jj+1))-f(ii, jj));
 * The function will put the tokens in a vector and will return the vector.
 * 
 */
private void tokenizeExpression(Expr expr, Vector<ParameterizedExpr> result)
{	
 
 if(expr instanceof NameExpr)
 {
	 return;
 }
 else if(expr instanceof ParameterizedExpr)
  {
	  result.add((ParameterizedExpr)expr);
  }//end of else if 
 
  else if (expr instanceof BinaryExpr)
  {
	 BinaryExpr mExpr=(BinaryExpr)expr;	 
	 tokenizeExpression(mExpr.getLHS(), result);
	 tokenizeExpression(mExpr.getRHS(), result);
  }//end of else if 
}//end of function



/*public ConstraintsGraph getGraph()
{
  if(cGraph!=null)
	{
	  return cGraph;
	}
	else return null;
}//end of function ConstraintsGraph
		
		
		
		

		
/*This function does following. 
		 * 
		 * 1.Makes equations from array subscript expression.		  
		 * TODO:Handle cases where LHS is not an instance of NameExpr.		 
		 * 
		 */
private void makeEquationsForSubscriptExprs(Expr LHSExpr,Expr RHSExpr,DependenceData dData)
{
			
  ParameterizedExpr paraLHSExpr=(ParameterizedExpr)LHSExpr;
  resultArray=new boolean[paraLHSExpr.getNumArg()];   //instantiate a boolean array based on dimensions of array under dependence testing.
  int size=paraLHSExpr.getNumArg();
  int[] array=new int[size];
  int count=0;
  for(int i=0; i<paraLHSExpr.getNumArg();i++)/*Only this one is handled where array indexes are not swapped. a(i,j)=a(i+10,j-11)*/   // To handle multidimensional arrays. //This case is not handled. e.g.a(i,j)=a(j-11,i+10)	  									        			
	{		
		AffineExpression aExpr1=new AffineExpression();
		AffineExpression aExpr2=new AffineExpression();					 
		if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof PlusExpr)
		 {
			 
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 aExpr2.setLoopVariable(pExpr.getLHS().getVarName());
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);
			 aExpr1.setIndexExpr(nExpr);
			 //PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setIndexExpr(pExpr.getLHS());
			 aExpr2.setKey("t"+i);
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(pExpr.getRHS() instanceof IntLiteralExpr)			
			 {
		    	 IntLiteralExpr iExpr=(IntLiteralExpr)pExpr.getRHS();				
				 aExpr2.setC(iExpr.getValue().getValue().intValue()*-1);
				 array[count]=aExpr1.getC()-aExpr2.getC();
				 count++;
				 System.out.println("PlusExpr "+ aExpr2.getC());
				 cGraph.addToGraph(aExpr1,aExpr2);							 
			 }//end of nested if	
		 }//end of main if			
		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MinusExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 MinusExpr mExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr.getLHS().getVarName());
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(nExpr);
			 //MinusExpr mExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr.getLHS());
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(mExpr.getRHS() instanceof IntLiteralExpr)			
			 {
				 IntLiteralExpr iExpr=(IntLiteralExpr)mExpr.getRHS();				
				 aExpr2.setC((iExpr.getValue().getValue().intValue()));
				 array[count]=aExpr1.getC()+aExpr2.getC();
				 count++;
				 System.out.println("MinusExpr  "+aExpr2.getC());
				 cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if	
		   }//end of main else if
		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof NameExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);						 
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 NameExpr nExpr1=(NameExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(nExpr1.getVarName());
			 aExpr1.setC(0);			
			 aExpr1.setKey("t"+i);
			 aExpr1.setIndexExpr(nExpr);
			 System.out.println(aExpr1.getKey());
			 aExpr2.setC(0);
			 array[count]=0;
			 count++;
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(((ParameterizedExpr)RHSExpr).getArg(i));
			 setUpperAndLowerBounds(aExpr1, aExpr2);
			 cGraph.addToGraph(aExpr1,aExpr2);						 
		   }//end of main else if	
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MTimesExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());						 
			 aExpr1.setC(0);			 
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 MTimesExpr mExpr1=(MTimesExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr1.getRHS().getVarName());
		     aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr1);
			 setUpperAndLowerBounds(aExpr1,aExpr2);						 				
			 aExpr2.setC(0);
			 array[count]=0;
			 count++;						
			 cGraph.addToGraph(aExpr1,aExpr2);											 
		 }//end of main else if
		
		/*
		 * TODO:Look into this case 2i=i+10
		 */			 
		/* else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof PlusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());						 
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 PlusExpr pExpr=(PlusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(pExpr.getLHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(pExpr.getLHS());
				 //if(pExpr.getLHS() instanceof MTimesExpr)aExpr2.setIndexExpr((MTimesExpr)pExpr.getLHS());//for these equations 2i=2j+10
						 //else if(pExpr.getLHS() instanceof NameExpr)aExpr2.setIndexExpr((NameExpr)pExpr.getLHS());//for these equations 2i=j+10
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(pExpr.getRHS() instanceof IntLiteralExpr)			
			 {
				IntLiteralExpr iExpr=(IntLiteralExpr)pExpr.getRHS();				
				aExpr2.setC(iExpr.getValue().getValue().intValue());
				array[count]=iExpr.getValue().getValue().intValue();
				count++;
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MinusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 MinusExpr miExpr=(MinusExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(miExpr.getLHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(miExpr.getLHS());
			 setUpperAndLowerBounds(aExpr1,aExpr2);
			 if(miExpr.getRHS() instanceof IntLiteralExpr)			
			 {
				IntLiteralExpr iExpr=(IntLiteralExpr)miExpr.getRHS();				
				aExpr2.setC((iExpr.getValue().getValue().intValue())*-1);
				array[count]=(iExpr.getValue().getValue().intValue())*-1;
				count++;
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof NameExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(mExpr);
			 NameExpr nExpr=(NameExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(nExpr.getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(nExpr);
			 setUpperAndLowerBounds(aExpr1,aExpr2);
				 //if(miExpr.getRHS() instanceof IntLiteralExpr)			
						 //{
							//IntLiteralExpr iExpr=(IntLiteralExpr)miExpr.getRHS();				
			aExpr2.setC(0);
			array[count]=0;
			count++;
			cGraph.addToGraph(aExpr1,aExpr2);
						 //}//end of nested if						 
		 }//end of main else if

		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MTimesExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 aExpr1.setC(0);
			 array[count]=0;
			 count++;
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(nExpr);
			 MTimesExpr mExpr=(MTimesExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr);
		     setUpperAndLowerBounds(aExpr1,aExpr2);						 				
			 aExpr2.setC(0);
			 array[count]=0;
			 count++;
			 cGraph.addToGraph(aExpr1,aExpr2);												 
		}//end of else if*/		
	}//end of for			
  dData.setDistanceArray(array);
}//end of function makeEquationsForSubscriptExprs
		
									
/*
 * 		 * This function sets the upper and lower bounds of the affine expression based on the loop which bounds those expressions.
		 * e.g. for int i=1:1:10
		 * 			for int j=1:1:20
		 *  			a(i,j)=a(i-10,j+11)
		 *  		end
		 *  	end
		 *  TODO:Look into this case a(i,j)=a(j-10,i+11).Consult notebook on which you have solved it.
*/							
private void setUpperAndLowerBounds(AffineExpression aExpr1 , AffineExpression aExpr2)
{	
	int upperBound=0;
	int lowerBound=0;
	for(int i=0;i<loopIndex;i++)
 	{
    	AssignStmt assStmt= forStmtArray[i].getAssignStmt();					
		if(assStmt.getVarName().equals(aExpr1.getLoopVariable())) //this is to compare array subscript with loop index variable.
		{
	      if(assStmt.getRHS() instanceof RangeExpr)		 		
		   {
			 RangeExpr rExpr=(RangeExpr) assStmt.getRHS();						
			 if(rExpr.getUpper() instanceof IntLiteralExpr && rExpr.getLower() instanceof IntLiteralExpr)
			  {
				System.out.println("I am a constraint bounded on both sides by variables of IntLiteralExpression");
				IntLiteralExpr iExprUpper=(IntLiteralExpr) rExpr.getUpper();
							//upperBound=iExprUpper.getValue().getValue().intValue();
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
							//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setUpperBound(iExprUpper);
				aExpr1.setLowerBound(iExprLower);
				aExpr2.setUpperBound(iExprUpper);
				aExpr2.setLowerBound(iExprLower);
			}//end of 3rd if
			else if(rExpr.getUpper() instanceof PlusExpr)
			 {
				System.out.println("I am a constraint bounded on both sides by variables of PlusExpr");
				PlusExpr pExpr=(PlusExpr)rExpr.getUpper();
				aExpr1.setUpperBound(pExpr);
				aExpr2.setUpperBound(pExpr);
				//aExpr1.setPUBound(pExpr);
				//aExpr2.setPUBound(pExpr);
				if(rExpr.getLower() instanceof NameExpr)
			 	{  NameExpr nExpr=(NameExpr)rExpr.getLower();
					//aExpr1.setNLBound(nExpr);
					//aExpr2.setNLBound(nExpr);
					aExpr1.setLowerBound(nExpr);
					aExpr2.setLowerBound(nExpr);
				}//end of if
				else if(rExpr.getLower() instanceof IntLiteralExpr)
				{	IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
					//lowerBound=iExprLower.getValue().getValue().intValue();
					aExpr1.setLowerBound(iExprLower);									
					aExpr2.setLowerBound(iExprLower);
				}
				else if(rExpr.getLower() instanceof PlusExpr)
				 {
					PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
					aExpr1.setLowerBound(pLExpr);
					aExpr2.setLowerBound(pLExpr);									
				}	
				else if(rExpr.getLower() instanceof MinusExpr)
				 {
					MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
					aExpr1.setLowerBound(mLExpr);
					aExpr2.setLowerBound(mLExpr);
				}	
				else if(rExpr.getLower() instanceof MTimesExpr)
				{
					MTimesExpr mExpr=(MTimesExpr)rExpr.getLower();
					aExpr1.setLowerBound(mExpr);
					aExpr2.setLowerBound(mExpr);
				}
			}//end of else if
			else if(rExpr.getUpper() instanceof MinusExpr)
			 {
				System.out.println("I am a constraint bounded on both sides by variables");
				MinusExpr mExpr=(MinusExpr)rExpr.getUpper();
				aExpr1.setUpperBound(mExpr);
				aExpr2.setUpperBound(mExpr);
				if(rExpr.getLower() instanceof NameExpr)
				{
					NameExpr nExpr=(NameExpr)rExpr.getLower();
					aExpr1.setLowerBound(nExpr);
					aExpr2.setLowerBound(nExpr);
				}
				else if(rExpr.getLower() instanceof IntLiteralExpr)
				 {
					IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
								//lowerBound=iExprLower.getValue().getValue().intValue();
					aExpr1.setLowerBound(iExprLower);									
					aExpr2.setLowerBound(iExprLower);
				}
				else if(rExpr.getLower() instanceof PlusExpr)
				{
					PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
					aExpr1.setLowerBound(pLExpr);
					aExpr2.setLowerBound(pLExpr);									
				}	
				else if(rExpr.getLower() instanceof MinusExpr)
				{
					MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
					aExpr1.setLowerBound(mLExpr);
					aExpr2.setLowerBound(mLExpr);
				}
				else if(rExpr.getLower() instanceof MTimesExpr)
				{
					MTimesExpr muExpr=(MTimesExpr)rExpr.getLower();
					aExpr1.setLowerBound(muExpr);
					aExpr2.setLowerBound(muExpr);
				}							
		    }//end of else if
		 else if(rExpr.getUpper() instanceof MTimesExpr)
		  {
			 System.out.println("I am a constraint bounded on both sides by variables");
			 MTimesExpr muExpr=(MTimesExpr)rExpr.getUpper();
			 aExpr1.setUpperBound(muExpr);
			 aExpr2.setUpperBound(muExpr);
			if(rExpr.getLower() instanceof NameExpr)
			{
			    NameExpr nExpr=(NameExpr)rExpr.getLower();
				aExpr1.setLowerBound(nExpr);
				aExpr2.setLowerBound(nExpr);
			}
			else if(rExpr.getLower() instanceof IntLiteralExpr)
			{
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
				//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setLowerBound(iExprLower);									
				aExpr2.setLowerBound(iExprLower);
			}
			else if(rExpr.getLower() instanceof PlusExpr)
			{
				PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
				aExpr1.setLowerBound(pLExpr);
				aExpr2.setLowerBound(pLExpr);									
			}	
			else if(rExpr.getLower() instanceof MinusExpr)
			{
				MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
				aExpr1.setLowerBound(mLExpr);
				aExpr2.setLowerBound(mLExpr);
			}
			else if(rExpr.getLower() instanceof MTimesExpr)
			{
				MTimesExpr mulExpr=(MTimesExpr)rExpr.getLower();
				aExpr1.setLowerBound(mulExpr);
				aExpr2.setLowerBound(mulExpr);
			}							
		}//end of else if						
		else if(rExpr.getUpper() instanceof NameExpr)
		  {
			System.out.println("I am a constraint bounded on both sides by variables");
			NameExpr nExpr1=(NameExpr)rExpr.getUpper();
			aExpr1.setUpperBound(nExpr1);
			aExpr2.setUpperBound(nExpr1);
			if(rExpr.getLower() instanceof NameExpr)
			{
				NameExpr nExpr2=(NameExpr)rExpr.getLower();
				aExpr1.setLowerBound(nExpr2);
				aExpr2.setLowerBound(nExpr2);
		    }
			else if(rExpr.getLower() instanceof IntLiteralExpr)
			{
				IntLiteralExpr iExprLower=(IntLiteralExpr) rExpr.getLower();
				//lowerBound=iExprLower.getValue().getValue().intValue();
				aExpr1.setLowerBound(iExprLower);									
				aExpr2.setLowerBound(iExprLower);
			}
			else if(rExpr.getLower() instanceof PlusExpr)
			{
				PlusExpr pLExpr=(PlusExpr)rExpr.getLower();
				aExpr1.setLowerBound(pLExpr);
			    aExpr2.setLowerBound(pLExpr);									
			}	
			else if(rExpr.getLower() instanceof MinusExpr)
			{
				MinusExpr mLExpr=(MinusExpr)rExpr.getLower();
				aExpr1.setLowerBound(mLExpr);
				aExpr2.setLowerBound(mLExpr);
			}
			else if(rExpr.getLower() instanceof MTimesExpr)
			{
				MTimesExpr mulExpr=(MTimesExpr)rExpr.getLower();
				aExpr1.setLowerBound(mulExpr);
				aExpr2.setLowerBound(mulExpr);
			}							
		}//end of else if
	  }//end of 2nd if	
	}//end of 1st if
  }//end of for loop		
}//end of setUpperAndLowerBounds function.
}//end of class
