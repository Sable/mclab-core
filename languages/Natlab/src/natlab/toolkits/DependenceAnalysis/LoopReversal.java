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
import ast.ASTNode;
import ast.AssignStmt;
import ast.EQExpr;
import ast.Expr;
import ast.ForStmt;
import ast.IfBlock;
import ast.IfStmt;
import ast.IntLiteralExpr;
import ast.LEExpr;
import ast.List;
import ast.MinusExpr;
import ast.Name;
import ast.NameExpr;
import ast.PlusExpr;
import ast.Program;
import ast.RangeExpr;
import ast.FPLiteralExpr;
import ast.ColonExpr;
import ast.Stmt;
import ast.StringLiteralExpr;
import ast.UMinusExpr;
import ast.UnaryExpr;

public class LoopReversal {
	
	//private ForStmt forStmt=new ForStmt();
	private ForStmt forStmt;
	private IfStmt ifStmt=new IfStmt();
	//private IfBlockList ifBlock=new IfBlock();
	
	public LoopReversal(){
	   //forStmt=fStmt;		
	}
	private void copy(ForStmt fStmt){
		forStmt=fStmt.fullCopy();		
		ast.List<Stmt> forList=forStmt.getStmtList();
		forList.removeChild(0);	
	}
	/*
	 * This method does the following.
	 * 1.Assign the upperbound of the For loop to lower bound and vice versa.
	 * 2.Reverse the sign of increment operator.
	 * 
	 */
	public void ApplyLoopReversal(){
		
		AssignStmt assStmt= forStmt.getAssignStmt();//This gives the assignment statement of the loop
		ast.List<Stmt> forList=forStmt.getStmtList();
		forList.removeChild(0);
		
		if(assStmt.getRHS() instanceof ColonExpr){
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
			ASTNode node=forStmt.getParent();
			int a=node.getIndexOfChild(forStmt);
			node.setChild(forStmt, a);	   
		
	}// end of range expr
		
}
	
public void ApplyLoopReversal(DependenceData data,ForStmt fStmt){
  copy(fStmt);
  AssignStmt assStmt= forStmt.getAssignStmt();//This gives the assignment statement of the loop 		
  if(assStmt.getRHS() instanceof RangeExpr){		
	  RangeExpr expr=(RangeExpr) assStmt.getRHS();									
	  RangeExpr nRExpr=new RangeExpr(); //create a new node for range expr.			
	  nRExpr.setLower(expr.getUpper());	
	  nRExpr.setUpper(expr.getLower());  
	  IntLiteralExpr incExpr=new IntLiteralExpr();					
	  BigInteger incValue=new BigInteger(Integer.toString(1));
	  BigInteger incValue1=incValue.negate();		
	  incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue1.toString()));
	  nRExpr.setIncrOpt(new ast.Opt <Expr>());
	  nRExpr.setIncr((Expr)incExpr);		
	  assStmt.setRHS(nRExpr);
	  addConditions(data,expr);
  }//end of if
    
   //ASTNode node=forStmt.getParent();   
   //int a=node.getIndexOfChild(forStmt);	 
   //if(a<0)node.setChild(forStmt, 0);
   //else node.setChild(forStmt, a);
   
 }//end of function.

/*
 * Assumption is that lower bound is known like it is either 1 or 2.just the upper bound is unknown.
 * 
 */
private void addConditions(DependenceData data,RangeExpr rExpr){
  	
  IfBlock ifBlock=new IfBlock();
  LEExpr eqExpr=new LEExpr();
  String vName="";
  if(rExpr.getUpper() instanceof NameExpr){vName=((NameExpr)rExpr.getUpper()).getVarName();}
  else if(rExpr.getUpper() instanceof PlusExpr){
	  if(((PlusExpr)rExpr.getUpper()).getRHS() instanceof NameExpr) vName=((NameExpr)((PlusExpr)rExpr.getUpper()).getRHS()).getVarName();
	  else if(((PlusExpr)rExpr.getUpper()).getLHS() instanceof NameExpr) vName=((NameExpr)((PlusExpr)rExpr.getUpper()).getLHS()).getVarName();	  
  }
  else if(rExpr.getUpper() instanceof MinusExpr){
	  if(((MinusExpr)rExpr.getUpper()).getRHS() instanceof NameExpr) vName=((NameExpr)((MinusExpr)rExpr.getUpper()).getRHS()).getVarName();
	  else if(((MinusExpr)rExpr.getUpper()).getLHS() instanceof NameExpr) vName=((NameExpr)((MinusExpr)rExpr.getUpper()).getLHS()).getVarName();
  }	  
  NameExpr nExpr=new NameExpr();
  Name name=new Name();
  name.setID(vName);
  nExpr.setName(name); 
  IntLiteralExpr incExpr=new IntLiteralExpr();					
  BigInteger incValue=new BigInteger(new Integer(data.getEndRange()).toString());						
  incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue.toString()));
  eqExpr.setLHS(nExpr);
  eqExpr.setRHS(incExpr);
  List<Stmt> ifList=new List<Stmt>();
  ifList.insertChild(forStmt, 0);  
  ifBlock.setCondition(eqExpr);
  ifBlock.setStmtList(ifList);
  ifStmt.addIfBlock(ifBlock);	 
 }//end of function

public IfStmt getIfStmt() {
	return ifStmt;
}
//public void setIfStmt(IfStmt ifStmt) {
	//this.ifStmt = ifStmt;
//}
		
	

}
