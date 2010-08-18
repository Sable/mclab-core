/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopInterchange" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */

package natlab.toolkits.DependenceAnalysis;


import java.math.BigInteger;
import java.util.LinkedList;

import ast.ASTNode;
import ast.AndExpr;
import ast.AssignStmt;
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
import ast.RangeExpr;
import ast.Stmt;

public class LoopInterchange {
	
private ForStmt forStmt;
private IfStmt ifStmt=new IfStmt();

//public LoopInterchange(ForStmt fStmt){				
  //forStmt=fStmt;			
//}
private void copy(LinkedList<ForStmt> fStmt){
	forStmt=fStmt.get(0).fullCopy();
		
}
	
/*
 * This method does the following.
 * 1.Assign the statement of inner For loop which can be an instance of AssignStmt,ExprStmt to the statement of outer For loop.
 * 2.Send the transformed AST to pretty printer for generating the MATLAB code.
 * 
*/
public void ApplyLoopInterchange(ForStmt fStmt1,ForStmt fStmt2){//interchange fStmt1 with fStmt2.	
	//System.out.println("i am in loop interchange");
	/*ForStmt forStmt2;		
	ast.List<Stmt> forList1=forStmt.getStmtList();
	forList1.removeChild(0);*/	
	AssignStmt assStmt2= fStmt2.getAssignStmt();	
	for(int i=0;i<fStmt1.getNumChild();i++){
	  if(fStmt1.getStmt(i) instanceof ForStmt){
	  		fStmt2=(ForStmt) fStmt1.getStmt(i); // .copy();
	  		AssignStmt assStmt1= fStmt1.getAssignStmt();
	  		fStmt2.setAssignStmt(assStmt1);
	  		fStmt1.setAssignStmt(assStmt2);	  		
	  		break;
	  	}//end of if
    }//end of for	
	//System.out.println(fStmt1.getPrettyPrintedLessComments());
	//System.out.println(fStmt2.getPrettyPrintedLessComments());
	/*ASTNode node=forStmt.getParent();
	int a=node.getIndexOfChild(forStmt);
	node.setChild(forStmt, a);*/	   
  }//end of  interchange


public void ApplyLoopInterchange(DependenceData dData,LinkedList<ForStmt> forStmtList,int i){//interchange fStmt1 with fStmt2.	
	//System.out.println("i am in loop interchange");
	/*ForStmt forStmt2;		
	ast.List<Stmt> forList1=forStmt.getStmtList();
	forList1.removeChild(0);*/	
	copy(forStmtList);
	//LinkedList<NestedLoop> nLoopList=dData.getNLoopList();
	if(forStmtList.size()==2){
		//AssignStmt[] aStmt=new AssignStmt[2];
		
		//IntLiteralExpr incExpr=new IntLiteralExpr();					
		//BigInteger incValue=new BigInteger(Integer.toString(nLoopList.get(0).getEndRange()));
		//incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue.toString()));		
		//RangeExpr rExpr1=(RangeExpr)forStmt.get(0).getAssignStmt().getRHS();
		
		AssignStmt tStmt=forStmt.getAssignStmt();
		//System.out.println("RangeExpr"+tStmt.getPrettyPrinted());
		forStmt.setAssignStmt(((ForStmt)forStmt.getStmt(0)).getAssignStmt());
		//System.out.println(forStmt.getAssignStmt().getPrettyPrinted());
		
		//IntLiteralExpr incExpr1=new IntLiteralExpr();					
		//BigInteger incValue1=new BigInteger(Integer.toString(nLoopList.get(1).getEndRange()));
		//incExpr1.setValue(new natlab.DecIntNumericLiteralValue(incValue1.toString()));
		
		((ForStmt)forStmt.getStmt(0)).setAssignStmt(tStmt);
		//System.out.println(((ForStmt)forStmt.getStmt(0)).getAssignStmt().getPrettyPrinted());
		
		//RangeExpr rExpr2=(RangeExpr)forStmt.get(1).getAssignStmt().getRHS();
		//AssignStmt assStmt2= ((ForStmt)forStmt.getStmt(0)).getAssignStmt();		
		//System.out.println(assStmt2.getPrettyPrinted());
		//AssignStmt assStmt1= forStmt.getAssignStmt();
		//System.out.println(assStmt1.getPrettyPrinted());
		//forStmt.get(1).setAssignStmt(assStmt1);
		//forStmt.setAssignStmt(assStmt2);
		//((ForStmt)forStmt.getStmt(0)).setAssignStmt(assStmt1);
		//aStmt[0]=assStmt2;
		//aStmt[1]=assStmt1;
		addConditions(dData,forStmtList,i);
	}		
	//System.out.println("first loop:"+forStmt.getPrettyPrinted());
	//System.out.println("second loop:"+forStmtList.get(0).getPrettyPrinted());
	
	/*ASTNode node=forStmt.getParent();
	int a=node.getIndexOfChild(forStmt);
	node.setChild(forStmt, a);*/	   
  }//end of  interchange

private void addConditions(DependenceData data,LinkedList<ForStmt>forStmtList,int i){ 
	  //IfBlock ifBlock=new IfBlock();
	  //AndExpr aExpr=new AndExpr();
	  LEExpr eqExpr1=new LEExpr();	
	  String vName="";
	  
	  if(((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper() instanceof NameExpr){
		  vName=((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper().getVarName();
		  //System.out.println("vName"+vName);
	  }
	  
	  else if(((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper() instanceof PlusExpr){
		  if(((PlusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
		  else if(((PlusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getLHS().getVarName();	  
	  }
	  
	  else if(((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper() instanceof MinusExpr){
		  if(((MinusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
		  else if(((MinusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmtList.get(0).getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
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
	  if(((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper() instanceof NameExpr){vName1=((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper().getVarName();}
	  
	  else if(((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper() instanceof PlusExpr){
		  if(((PlusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr) vName1=((PlusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
		  else if(((PlusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr) vName1=((PlusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getLHS().getVarName();	  
	  }
	  
	  else if(((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper() instanceof MinusExpr){
		  if(((MinusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr){
			  vName1=((MinusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
			  //System.out.println("vName1"+vName1);
		  }
		  else if(((MinusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr){ 
			     vName1=((MinusExpr)((RangeExpr)forStmtList.get(1).getAssignStmt().getRHS()).getUpper()).getLHS().getVarName();
			     //System.out.println("vName1"+vName1);
		  }
	  }
	  
	  
	  
	  //System.out.println("Size is:::" + data.getNLoopList().size());
	  
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
	  }
	  
	  
	  
	  
	  //System.out.println("eqExpr2"+eqExpr2.getPrettyPrinted());
	  
	  
	  //System.out.println("aExpr"+aExpr.getPrettyPrinted());
	  
	
	  System.out.println(ifStmt.getPrettyPrinted());
 }//end of function

 public IfStmt getIfStmt() {
	return ifStmt;
 }

 public void setIfStmt(IfStmt ifStmt) {
	this.ifStmt = ifStmt;
 }

}
