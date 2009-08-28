package natlab.toolkits.DependenceAnalysis;

import java.io.File;
import java.io.RandomAccessFile;

import natlab.ast.AssignStmt;
import natlab.ast.Expr;
import natlab.ast.ForStmt;
import natlab.ast.IntLiteralExpr;
import natlab.ast.MinusExpr;
import natlab.ast.NameExpr;
import natlab.ast.ParameterizedExpr;
import natlab.ast.PlusExpr;
import natlab.ast.RangeExpr;
import natlab.ast.Stmt;
import natlab.ast.MTimesExpr;
import java.util.Iterator;
/*
 * Author:Amina Aslam
 * Date:06 Jul,2009
 * ConstraintsToolBox class creates constraints from the equations and puts them in ConstraintsGraph.
 */


public class ConstraintsToolBox {
	
	//private ForStmt forNode;
	private Stmt fStmt;
	private String dependencyFlag="No";
	private ForStmt forStmtArray[];
	private static int loopIndex=0;
	private static boolean resultArray[];
	private ConstraintsGraph cGraph;
	private Expr newExpr;
	private Expr teExpr;
	
	
	public ConstraintsToolBox(int index,ForStmt fStmtArray[])
	{
		cGraph=new ConstraintsGraph();
		loopIndex=index;
		forStmtArray=new ForStmt[index];
		forStmtArray=fStmtArray;		
	}
	
		
	/*This function checks whether accessed arrays are the same or not.
	 * e.g. for int i=1:1:10
	 *  		a(i,j)=a(i+10,j-9)
	 *  	end
	 */	
public boolean checkSameArrayAccess(Expr aExpr,Expr bExpr)
 {		 
	boolean aFlag=false;
	//System.out.println(bExpr.dumpTreeAll());
	if(aExpr instanceof ParameterizedExpr)
	 { 	  if(bExpr instanceof ParameterizedExpr)
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
    }//end of ParameterizedExpr if
  	return aFlag;						
}//end of function checkSameArrayAccess.

/*
 * This function tokenizes the loop statement and returns a parameterized expression.
 * e.g.f(ii, jj) = f(ii, jj)+mask(ii, jj)*(0.25*(f(ii-1, jj)+f(ii+1, jj)+f(ii, jj-1)+f(ii, jj+1))-f(ii, jj));
 * The function will return f(ii,jj) in first iteration.Then this f(ii, jj+1)) token and continues until end of expression. 
 * 
 */
private Expr tokenizeExpression(Expr expr)
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
 else if(expr instanceof PlusExpr)
   {
	 PlusExpr pExpr=(PlusExpr)expr;
	 System.out.println(pExpr.getLHS().getPrettyPrinted());
	 System.out.println(pExpr.getLHS().getClass().toString());
	 System.out.println(pExpr.getRHS().getPrettyPrinted());
	 System.out.println(pExpr.getRHS().getClass().toString());	 
	 if(pExpr.getLHS() instanceof ParameterizedExpr){ newExpr=pExpr.getRHS();return pExpr.getLHS();}
	 else
	 { if(!(pExpr.getLHS().getClass().toString().equals("class natlab.ast.IntLiteralExpr")) || !(pExpr.getLHS().getClass().toString().equals("class natlab.ast.FPLiteralExpr")))
	   { newExpr=pExpr.getLHS();
	     if(pExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=pExpr.getRHS();
	 	    return teExpr;
	      }
	   }//end of if
     }//end of else
	 if(pExpr.getRHS() instanceof ParameterizedExpr) return pExpr.getRHS();
	 else 
	 { if(!(pExpr.getLHS().getClass().equals("class natlab.ast.IntLiteralExpr")) || !(pExpr.getLHS().getClass().equals("class natlab.ast.FPLiteralExpr")))
		{tokenizeExpression(pExpr.getRHS());}		 
	 }
   }//end of else if 
  else if (expr instanceof MTimesExpr)
  {
	 MTimesExpr mExpr=(MTimesExpr)expr;
 	 if(mExpr.getLHS() instanceof ParameterizedExpr){newExpr=mExpr.getRHS(); return mExpr.getLHS();}
	 else
	  {if(!(mExpr.getLHS().getClass().toString().equals("class natlab.ast.IntLiteralExpr")) && !(mExpr.getLHS().getClass().toString().equals("class natlab.ast.FPLiteralExpr")))
		 {newExpr=mExpr.getLHS();
	     if(mExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=mExpr.getRHS();
	 	    return teExpr;
	      }
	     }		 
       }//end of else  	 
	 if(mExpr.getRHS() instanceof ParameterizedExpr) return mExpr.getRHS();
	 else 
	  {if(!(mExpr.getRHS().getClass().toString().equals("class natlab.ast.IntLiteralExpr")) && !(mExpr.getRHS().getClass().toString().equals("class natlab.ast.FPLiteralExpr")))
		 {tokenizeExpression(mExpr.getRHS());return teExpr;}		 
	  }
  }//end of else if 
 else if (expr instanceof MinusExpr)
  {
	 MinusExpr miExpr=(MinusExpr)expr;
 	 if(miExpr.getLHS() instanceof ParameterizedExpr){newExpr=miExpr.getRHS(); return miExpr.getLHS();}
	 else
	  {if(!(miExpr.getLHS().getClass().toString().equals("class natlab.ast.IntLiteralExpr")) && !(miExpr.getLHS().getClass().toString().equals("class natlab.ast.FPLiteralExpr")))
		{newExpr=miExpr.getLHS();
	     if(miExpr.getRHS() instanceof ParameterizedExpr)
	     { teExpr=miExpr.getRHS();
	 	    return teExpr;
	      }
	     }
	  }
	 if(miExpr.getRHS() instanceof ParameterizedExpr) return miExpr.getRHS();
	 else
	  {if(!(miExpr.getRHS().getClass().toString().equals("class natlab.ast.IntLiteralExpr")) && !(miExpr.getRHS().getClass().toString().equals("class natlab.ast.FPLiteralExpr")))
		 {tokenizeExpression(miExpr.getRHS());}
	 }
  }//end of else if
    return null;	
}//end of function


public ConstraintsGraph getGraph()
{
  if(cGraph!=null)
	{
	  return cGraph;
	}
	else return null;
}//end of function ConstraintsGraph
		
		
		
		

		
/*This function does following. 
		 * 1.Makes equations from array subscript expression.		  
		 * TO DO:Handle cases where LHS is not an instance of NameExpr.		 
		 * 
		 */
private void makeEquationsForSubscriptExprs(Expr LHSExpr,Expr RHSExpr)
{
			
  ParameterizedExpr paraLHSExpr=(ParameterizedExpr)LHSExpr;
  resultArray=new boolean[paraLHSExpr.getNumArg()];   //instantiate a boolean array based on dimensions of array under dependence testing.	
  for(int i=0; i<paraLHSExpr.getNumArg();i++)   // To handle multidimensional arrays. e.g.a(i,j)=a(j-11,i+10)
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
				 aExpr2.setC(iExpr.getValue().getValue().intValue());
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
				 aExpr2.setC((iExpr.getValue().getValue().intValue())*-1);
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
						 //if(mExpr.getRHS() instanceof IntLiteralExpr)			
						 //{
							// IntLiteralExpr iExpr=(IntLiteralExpr)mExpr.getRHS();				
			 aExpr2.setC(0);
						 //System.out.println("MinusExpr"+aExpr2.getC());
			 cGraph.addToGraph(aExpr1,aExpr2);
						 //}//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof PlusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());						 
			 aExpr1.setC(0);
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
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MinusExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
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
				cGraph.addToGraph(aExpr1,aExpr2);
			 }//end of nested if						 
		 }//end of main else if
					 
		 else if(paraLHSExpr.getArg(i) instanceof MTimesExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof NameExpr)
		 {
			 MTimesExpr mExpr=(MTimesExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr1.setC(0);
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
			cGraph.addToGraph(aExpr1,aExpr2);
						 //}//end of nested if						 
		 }//end of main else if

		else if(paraLHSExpr.getArg(i) instanceof NameExpr && ((ParameterizedExpr)RHSExpr).getArg(i) instanceof MTimesExpr)
		 {
			 NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
			 aExpr1.setLoopVariable(nExpr.getVarName());
			 aExpr1.setC(0);
			 aExpr1.setKey("t"+i);			
			 aExpr1.setIndexExpr(nExpr);
			 MTimesExpr mExpr=(MTimesExpr)((ParameterizedExpr)RHSExpr).getArg(i);
			 aExpr2.setLoopVariable(mExpr.getRHS().getVarName());
			 aExpr2.setKey("t"+i);
			 aExpr2.setIndexExpr(mExpr);
		     setUpperAndLowerBounds(aExpr1,aExpr2);						 				
			 aExpr2.setC(0);
			 cGraph.addToGraph(aExpr1,aExpr2);												 
		}//end of main else if
	}//end of for			
}//end of function makeEquationsForSubscriptExprs
		
									
/*
 * 		 * This function sets the upper and lower bounds of the affine expression based on the loop which bounds those expressions.
		 * e.g. for int i=1:1:10
		 * 			for int j=1:1:20
		 *  			a(i,j)=a(j-10,i+11)
		 *  		end
		 *  	end
		 *  Affine expressions i, j-10 would be bound by i loop
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
