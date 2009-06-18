package DependenceAnalysis;
import natlab.ast.ForStmt;
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
import natlab.ast.RangeExpr;
import natlab.ast.ColonExpr;


public class GcdTest {
	
	private ForStmt forNode;
	private static boolean resultArray[];
	
	public GcdTest(ForStmt fNode)	
	{
		forNode=fNode;
		checkSameArrayAccess();
		
	}
	private void isTightlyNestedLoop(ForStmt forStmt)
	{
		//int nStmts=forStmt.getNumStmt();
		//System.out.println("no of statements in for loop are " +nStmts);
		//for(int i=0;i<nStmts;i++)
	//	{
		  Stmt stmt=forStmt.getStmt(0);
		  if(stmt instanceof ForStmt)
		  {
			  ForStmt tForStmt=(ForStmt)stmt;
			  forNode=tForStmt;
			  //int no=tForStmt.getNumStmt();
			  //for(int j=0;j<no;j++)
			 // {
				  System.out.println("i am in for stmt");
				  isTightlyNestedLoop(tForStmt);
				  
			 // }
	//		  return true;
		  }
		//  else
			//  return false;
		//} 
		//return true;
	}
	
	
	private void checkSameArrayAccess()
	{
		
		isTightlyNestedLoop(forNode);
				
		int nStmts=forNode.getNumStmt();
		System.out.println("I am in Expression Statement");
		for(int i=0;i<nStmts;i++)
		{
		  Stmt stmt=forNode.getStmt(i);		  
		  
		  if(stmt instanceof ExprStmt)
		  {
			  System.out.println("I am in Expression Statement");
		  }
		  else if(stmt instanceof AssignStmt)
		  {
			  System.out.println(" i am in Assignment statement");
			  AssignStmt aStmt=(AssignStmt)stmt;  
			 
			 
			  if(aStmt.getLHS() instanceof ParameterizedExpr)
			  {
				  
				  if(aStmt.getRHS() instanceof ParameterizedExpr)
				  {					  
					  if(aStmt.getLHS().getVarName().equals(aStmt.getRHS().getVarName()))
					  {						
						  makeEquationsForSubscriptExprs(aStmt);
					  }
				  }
				  /*for(int j=i+1;j<nStmts;j++)
				  {
					  if(aStmt.getRHS() instanceof PlusExpr)
					  {
						  
						  System.out.println(aStmt.getRHS().dumpCodeTree());
					  }
				  }*/
				  
			  }
			  reportTestResult(((ParameterizedExpr)aStmt.getLHS()).getNumArg());
			  
		  }
		  
		  
		}//end of for loop
		
		
		
	}//end of function checkSameArrayAccess.
	
	//This function makes equations from array subscript expression.
	private void makeEquationsForSubscriptExprs(AssignStmt aStmt)
	{
		ParameterizedExpr paraLHSExpr=(ParameterizedExpr)aStmt.getLHS();
		ParameterizedExpr paraRHSExpr=(ParameterizedExpr)aStmt.getRHS();
		
		//System.out.println("LHS Parameterized Expression" + paraLHSExpr.getArg(0).dumpCodeTree());
		//System.out.println("RHS Parameterized Expression" + paraRHSExpr.getArg(0).dumpCodeTree());
		
		AffineExpression aExpr1=new AffineExpression();
		AffineExpression aExpr2=new AffineExpression();
		
		resultArray=new boolean[paraLHSExpr.getNumArg()]; //instantiate a boolean array based on dimensions of array under dependence testing.
		
		for(int i=0;i < paraLHSExpr.getNumArg();i++)
		{	
		
			if(paraLHSExpr.getArg(i) instanceof NameExpr && paraRHSExpr.getArg(i) instanceof PlusExpr)
			{
				NameExpr nExpr=(NameExpr)paraLHSExpr.getArg(i);
				aExpr1.setC(0);
				aExpr1.setVariable(nExpr.getVarName());			
				PlusExpr pExpr=(PlusExpr)paraRHSExpr.getArg(i);
				aExpr2.setVariable(pExpr.getLHS().getVarName());			
				if(pExpr.getRHS() instanceof IntLiteralExpr)			
				{
					IntLiteralExpr iExpr=(IntLiteralExpr)pExpr.getRHS();				
					aExpr2.setC(iExpr.getValue().getValue().intValue());
					checkDependence(aExpr1,aExpr2,i);
									
				}//end of nested if	
			}//end of main if
	}//end of for
		
		
}//end of function makeEquationsForSubscriptExprs
	
	
	/*private void combineEquations(AffineExpression expr1, AffineExpression expr2)
	{
		String t1=expr1.getVariable();
		MinusExpr mExpr = new MinusExpr();
		
		//Setting LHS of Minus Expression.
		NameExpr tNExpr=new NameExpr();
		Name tN=new Name();
		tN.setID(t1);
		tNExpr.setName(tN);
		mExpr.setLHS(tNExpr);
		
		//Setting RHS of Minus Expression.
		IntLiteralExpr iExpr=new IntLiteralExpr();
		//IntNumericLiteralValue iValue=new IntNumericLiteralValue();
		BigInteger bInt=(BigInteger) expr2.getC();
		//iExpr.setValue(new natlab.DecIntNumericLiteralValue(new BigInteger().));
		
		//mExpr.setRHS(new natlab.ast.IntLiteralExpr(new ));
		
		
		
		
	}*/
	
	
	/*
	 * Sets the index of resultArray to false if there is dependence for that particular equation.
	 * If there is no dependence for that equation then sets value true at the location of resultArray.
	 * Size of resultArray depends on the dimensions of the array under dependence testing.
	 */
	private void checkDependence(AffineExpression expr1, AffineExpression expr2,int index)
	{
		AssignStmt assStmt= forNode.getAssignStmt();//This gives the assignment statement of the loop
		
		if(assStmt.getRHS() instanceof RangeExpr)
		{
			RangeExpr rExpr=(RangeExpr) assStmt.getRHS();
			if(rExpr.getUpper() instanceof IntLiteralExpr)
			{
	    		
				
				IntLiteralExpr iExprUpper=(IntLiteralExpr) rExpr.getUpper();
						
				if(expr2.getC() > (iExprUpper.getValue().getValue().intValue()))
				{
					//System.out.println("There is no dependence");
					resultArray[index]=false;
				//	return true;
				}
				else
					{
					 //System.out.println("There is a dependence in the System");
					 resultArray[index]=true;
					}
				
					
			}
			
			
		}
		
		else if(assStmt.getRHS() instanceof ColonExpr)
		{
			System.out.println("For first loop colon expr");
			
		}
		
		
		
		
	}//end of function checkDependence
	
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
	

}
