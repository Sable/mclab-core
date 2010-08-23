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
import java.util.LinkedList;

//import ast.AST.Opt;

import natlab.IntNumericLiteralValue;
import ast.ASTNode;
import ast.AndExpr;
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
		//ast.List<Stmt> forList=forStmt.getStmtList();
		//forList.removeChild(0);	
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
	
/*
 * This function applies loop reversal on a given loop using predicted loop bounds
 */	
public void ApplyLoopReversal(DependenceData data,ForStmt fStmt, boolean aFlag){
  copy(fStmt);  
  boolean flag=true; //This flag tests whether the loop is nested or not.
  //if(aFlag) {
	  //forStmt.getStmtList().removeChild(0);
	  //forStmt.getStmtList().removeChild(0);
  //}
  //else forStmt.getStmtList().removeChild(0);
  AssignStmt assStmt= forStmt.getAssignStmt();//This gives the assignment statement of the loop  
  if(forStmt.getStmt(0) instanceof ForStmt){ //this is for nested loops	    
      AssignStmt assStmt1= ((ForStmt)forStmt.getStmt(0)).getAssignStmt();
      RangeExpr expr=(RangeExpr) assStmt1.getRHS();									
	  RangeExpr nRExpr=new RangeExpr(); //create a new node for range expr.			
	  nRExpr.setLower(expr.getUpper());	
	  nRExpr.setUpper(expr.getLower());  
	  IntLiteralExpr incExpr=new IntLiteralExpr();					
	  BigInteger incValue=new BigInteger(Integer.toString(1));
	  BigInteger incValue1=incValue.negate();		
	  incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue1.toString()));
	  nRExpr.setIncrOpt(new ast.Opt <Expr>());
	  nRExpr.setIncr((Expr)incExpr);		
	  assStmt1.setRHS(nRExpr);
	  flag=false;
  }//end of if  
  
  if(assStmt.getRHS() instanceof RangeExpr){  //This is for single loop	  
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
	  addConditions(data,expr,flag);	  
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
private void addConditions(DependenceData data, RangeExpr rExpr, boolean flag){
 
if(flag){	
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
}
  
else{ 
  LEExpr eqExpr1=new LEExpr();	
  String vName="";
  
  if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower() instanceof NameExpr){
	  vName=((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower().getVarName();
	  //System.out.println("vName"+vName);
  }
  
  else if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower() instanceof PlusExpr){
	  if(((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getRHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getRHS().getVarName();
	  else if(((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getLHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getLHS().getVarName();	  
  }
  
  else if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower() instanceof MinusExpr){
	  if(((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getRHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getRHS().getVarName();
	  else if(((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getLHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getLower()).getLHS().getVarName();
  }
  
  //String vName=((NameExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getVarName();//((NameExpr)((RangeExpr)aStmt[0].getLHS()).getUpper()).getVarName();
  NameExpr nExpr=new NameExpr();
  Name name=new Name();
  name.setID(vName);
  nExpr.setName(name); 
  IntLiteralExpr incExpr=new IntLiteralExpr();					
  BigInteger incValue=new BigInteger(new Integer(data.getEndRange()).toString());						
  incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue.toString()));
  eqExpr1.setLHS(nExpr);
  eqExpr1.setRHS(incExpr);
  //System.out.println("eqExpr"+eqExpr1.getPrettyPrinted());
  
  
  //LEExpr eqExpr2=new LEExpr();	  
  //String vName1=((NameExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getVarName();
  String vName1="";
  if(((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower() instanceof NameExpr){vName1=((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower().getVarName();}
  
  else if(((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower() instanceof PlusExpr){
	  if(((PlusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getRHS() instanceof NameExpr) vName1=((PlusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getRHS().getVarName();
	  else if(((PlusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getLHS() instanceof NameExpr) vName1=((PlusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getLHS().getVarName();	  
  }
  
  else if(((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower() instanceof MinusExpr){
	  if(((MinusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getRHS() instanceof NameExpr){
		  vName1=((MinusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getRHS().getVarName();
		  //System.out.println("vName1"+vName1);
	  }
	  else if(((MinusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getLHS() instanceof NameExpr){ 
		     vName1=((MinusExpr)((RangeExpr)((ForStmt)forStmt.getStmt(0)).getAssignStmt().getRHS()).getLower()).getLHS().getVarName();
		     //System.out.println("vName1"+vName1);
	  }
  }
  
  int size=data.getNLoopList().size();
  LinkedList<NestedLoop> list=data.getNLoopList();
  for(int j=0;j<size;j++){
	  IfBlock ifBlock=new IfBlock();
	  NestedLoop nLoop=list.get(j);
	  NameExpr nExpr1=new NameExpr();
	  Name name1=new Name();
	  name1.setID(vName1);
	  nExpr1.setName(name1); 
	  IntLiteralExpr incExpr1=new IntLiteralExpr();
	  BigInteger incValue1=new BigInteger(new Integer(nLoop.getEndRange()).toString());						
	  incExpr1.setValue(new natlab.DecIntNumericLiteralValue(incValue1.toString()));
	  LEExpr eqExpr2=new LEExpr();
	  eqExpr2.setLHS(nExpr1);
	  eqExpr2.setRHS(incExpr1);
	  AndExpr aExpr=new AndExpr();
	  aExpr.setRHS(eqExpr1);
	  aExpr.setLHS(eqExpr2);
	  //System.out.println(aExpr.getPrettyPrinted());
	  List<Stmt> ifList=new List<Stmt>();
	  ifList.insertChild(forStmt, 0);  
	  ifBlock.setCondition(aExpr);
	  ifBlock.setStmtList(ifList);
	  ifStmt.addIfBlock(ifBlock);
   }//end of if
 }//end of else 
 //System.out.println(ifStmt.getPrettyPrinted());
}//end of function

public IfStmt getIfStmt() {
	//System.out.println(ifStmt.getPrettyPrinted());
	return ifStmt;
}
//public void setIfStmt(IfStmt ifStmt) {
	//this.ifStmt = ifStmt;
//}
		
	

}
