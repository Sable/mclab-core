/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopReversal" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */
package natlab.toolkits.DependenceAnalysis;

import java.math.BigDecimal;
import java.math.BigInteger;

//import ast.AST.Opt;

import natlab.IntNumericLiteralValue;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.Program;
import ast.RangeExpr;
import ast.FPLiteralExpr;
import ast.ColonExpr;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.UMinusExpr;
import ast.UnaryExpr;

public class LoopReversal {
	
	private ForStmt forStmt;
	
	public LoopReversal(ForStmt fStmt){
		forStmt=fStmt;//.copy();
	}
	/*
	 * This method does the following.
	 * 1.Assign the upperbound of the �For� loop to lower bound and vice versa.
	 * 2.Reverse the sign of increment operator.
	 * 3.send the new �For� loop to pretty printer for generating the MATLAB code
	 */
	public void ApplyLoopReversal(){
		
		AssignStmt assStmt= forStmt.getAssignStmt();//This gives the assignment statement of the loop
		ast.List<Stmt> forList=forStmt.getStmtList();
		forList.removeChild(0);
		
		if(assStmt.getRHS() instanceof ColonExpr)
		{
			System.out.println("For first loop colon expr");
		}
		
		else if(assStmt.getRHS() instanceof RangeExpr){
			
			RangeExpr expr=(RangeExpr) assStmt.getRHS();									
			RangeExpr nRExpr=new RangeExpr(); //create a new node for range expr.			
			nRExpr.setLower(expr.getUpper());	
			nRExpr.setUpper(expr.getLower());				
			if(expr.hasIncr()){	
			 if(expr.getIncr() instanceof IntLiteralExpr){						
				IntLiteralExpr incExpr=(IntLiteralExpr)expr.getIncr();					
				BigInteger incValue=incExpr.getValue().getValue().negate();						
				incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue.toString()));
				nRExpr.setIncrOpt(new ast.Opt <Expr>());
				nRExpr.setIncr((Expr)incExpr);
			  }
					/*else if(expr.getIncr() instanceof UMinusExpr)
					{
						
						UMinusExpr incExpr=(UMinusExpr)expr.getIncr();					
						//IntLiteralExpr incValue=(IntLiteralExpr)incExpr.getOperand();	
						//BigInteger bValue=incValue.getValue().getValue().negate();
						//incV
						//UMinusExpr newExpr = new UMinusExpr();
						//newExpr
						//incExpr.setOperand(new natlab.ast.UMinusExpr(bValue));
					//	System.out.println("new operator is" + incValue);
						//incExpr.setOperand();
					    //expr.setIncr((Expr)incExpr);
						nRExpr.setIncrOpt(new natlab.ast.Opt <Expr>());
						nRExpr.setIncr((Expr)incExpr);
						//IntLiteralExpr incExpr1=(IntLiteralExpr)nRExpr.getIncr();
						//int temp=incExpr1.getValue().getValue().intValue();
						//System.out.println("new operator is" + temp);
					}*/
					/*else if(expr.getIncr() instanceof FPLiteralExpr)
					{
						
						FPLiteralExpr incExpr=(FPLiteralExpr)expr.getIncr();					
						BigDecimal incValue=incExpr.getValue().getValue().negate();						
				    	incExpr.setValue(new natlab.FPNumericLiteralValue(incValue.toString()));					
						nRExpr.setIncrOpt(new ast.Opt <Expr>());
						nRExpr.setIncr((Expr)incExpr);
						FPLiteralExpr incExpr1=(FPLiteralExpr)nRExpr.getIncr();
					//	float temp=incExpr1.getValue().getValue().floatValue();
					  //  System.out.println("new operator is" + temp);
					}*/
					
				}//end of hasExpr() if block.
			   else{ //this creates new expression.					
					IntLiteralExpr incExpr=new IntLiteralExpr();					
					BigInteger incValue=new BigInteger(Integer.toString(1));
					BigInteger incValue1=incValue.negate();
				//	System.out.println("new operator is" + incValue);
					incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue1.toString()));
				    //expr.setIncr((Expr)incExpr);
					nRExpr.setIncrOpt(new ast.Opt <Expr>());
					nRExpr.setIncr((Expr)incExpr);
					//IntLiteralExpr incExpr1=(IntLiteralExpr)nRExpr.getIncr();
					//int temp=incExpr1.getValue().getValue().intValue();
				//	System.out.println("new operator is" + temp);
			 }
			assStmt.setRHS(nRExpr);
			//System.out.println(forStmt.getPrettyPrinted());
		
	}// end of range expr
		
}
		
	

}
