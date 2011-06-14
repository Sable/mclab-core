// =========================================================================== //
//                                                                             //
// Copyright 2011 Amina Aslam and McGill University.                           //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopFusion" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */
package natlab.toolkits.DependenceAnalysis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Vector;

import ast.ASTNode;
import ast.AndExpr;
import ast.AssignStmt;
import ast.ElseBlock;
import ast.FPLiteralExpr;
import ast.IfBlock;
import ast.IfStmt;
import ast.LEExpr;
import ast.List;
import ast.MinusExpr;
import ast.Name;
import ast.NameExpr;
import ast.Opt;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.PlusExpr;
import ast.RangeExpr;
import ast.Stmt;

public class LoopFusion {
	
private static LinkedList<ForStmt> fList=new LinkedList<ForStmt>();
private static LinkedList<Vector<DependenceData>> vList=new LinkedList<Vector<DependenceData>>();
private IfStmt ifStmt=new IfStmt();
	

public LoopFusion(){						
	//fList=list;	
}
/*
 * This method does the following.
 * 1.Check the loop bounds of the two loops that need to be fused.
		1.If the upper,lower bounds and no of iterations are the same for the two loops then copy the statements of second loop into first loop.
		2.Else return an error �Two loops cannot be fused�.
	2.Once two loops are combined into one loop,remove the second loop from the AST and send the transformed AST to pretty printer for generating new MATLAB code from the transformed AST.
 */
public void ApplyLoopFusion(){
	
	((ForStmt)fList.get(0)).getStmtList().removeChild(0);
	//((ForStmt)fList.get(0)).getStmtList().removeChild(0);
	((ForStmt)fList.get(1)).getStmtList().removeChild(0);
	//((ForStmt)fList.get(1)).getStmtList().removeChild(0);
	
    for(int k=0;k<vList.get(0).size();k++){
    	DependenceData data1=((DependenceData)((Vector<DependenceData>)vList.get(0)).get(k));
    	DependenceData data2=((DependenceData)((Vector<DependenceData>)vList.get(1)).get(k));
    	if(data1.getEndRange()== data2.getEndRange()){  //This checks the loop bounds of the two loops,if equal then they are fused.
    		data2.setTransformation("LoopFusion");
    		data1.setTransformation("LoopFusion");    		
    		ForStmt forStmt = ((ForStmt)fList.get(0)).fullCopy();    		
    		int no=fList.get(1).getNumStmt();    		
    		for(int i=0;i<no;i++){
    		   forStmt.addStmt(fList.get(1).getStmt(i));
    		}    		
    		addConditions(data1,forStmt);
      }//end of if	
    }//end of for
 }//end of function


/*
 * This function adds dynamic guards(If conditions) around fused loop.
 */
private void addConditions(DependenceData data,ForStmt forStmt){		
 
  LEExpr eqExpr1=new LEExpr();	
  String vName="";
  
  
  if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper() instanceof NameExpr){
	  vName=((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper().getVarName();
	  
  }
  
  
  else if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper() instanceof PlusExpr){
	  if(((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
	  else if(((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr) vName=((PlusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getLHS().getVarName();	  
  }
  
  else if(((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper() instanceof MinusExpr){
	  if(((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getRHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getRHS().getVarName();
	  else if(((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getLHS() instanceof NameExpr) vName=((MinusExpr)((RangeExpr)forStmt.getAssignStmt().getRHS()).getUpper()).getLHS().getVarName();
  }
  
  
  IfBlock ifBlock=new IfBlock();
  LEExpr eqExpr=new LEExpr();  
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



/*
 * This method does the following.
 * 1.Check the loop bounds of the two loops that need to be fused.
	
private boolean TestLoopLimits(){
	
    	
	
		
	//if(vList.get(0).containsAll(vList.get(1))) return true;
	//return false;
		/*AssignStmt assStmt1= fList.get(0).getAssignStmt();//This gives the assignment statement of the loop
		AssignStmt assStmt2= fList.get(1).getAssignStmt();//This gives the assignment statement of the loop
	    if(assStmt1.getRHS() instanceof RangeExpr && assStmt2.getRHS() instanceof RangeExpr){	    	
	    	RangeExpr expr1=(RangeExpr) assStmt1.getRHS();
	    	RangeExpr expr2=(RangeExpr) assStmt2.getRHS();
	    	int LIndex1;
    		int LIndex2;
    		int UIndex1;
    		int UIndex2;
    		
        	float fLIndex1;
    		float fLIndex2;
    		float fUIndex1;
    		float fUIndex2;
    		boolean exprOptFlag=false;
	    	if(expr1.getLower() instanceof IntLiteralExpr && expr2.getLower() instanceof IntLiteralExpr){
	    		
				
				IntLiteralExpr fExprLower1=(IntLiteralExpr) expr1.getLower();				
				LIndex1 = fExprLower1.getValue().getValue().intValue();				
				//System.out.println("Lower Index of loop 1  " + LIndex1);
				
				IntLiteralExpr fExprLower2=(IntLiteralExpr) expr2.getLower();				
				LIndex2 = fExprLower2.getValue().getValue().intValue();				
				//System.out.println("Lower Index of loop 2  " + LIndex2);
			
				IntLiteralExpr fExprUpper1=(IntLiteralExpr) expr1.getUpper();
				UIndex1 = fExprUpper1.getValue().getValue().intValue();
			//	System.out.println("Upper Index of loop1  " + UIndex1);
				
				
				
				IntLiteralExpr fExprUpper2=(IntLiteralExpr) expr2.getUpper();
				UIndex2 = fExprUpper2.getValue().getValue().intValue();
			//	System.out.println("Upper Index of loop2  " + UIndex2);
				
				exprOptFlag=checkExprOpt(expr1,expr2);
				if((LIndex1==LIndex2 && UIndex1==UIndex2)&& exprOptFlag){
			    	return true;
			    }

			}
	    	
	    	if(expr1.getLower() instanceof FPLiteralExpr && expr2.getLower() instanceof FPLiteralExpr)
			{
	    		
				
				FPLiteralExpr fExprLower1=(FPLiteralExpr) expr1.getLower();				
				fLIndex1 = fExprLower1.getValue().getValue().floatValue();				
				//System.out.println("Lower Index of loop 1  " + LIndex1);
				
				FPLiteralExpr fExprLower2=(FPLiteralExpr) expr2.getLower();				
				fLIndex2 = fExprLower2.getValue().getValue().floatValue();				
				//System.out.println("Lower Index of loop 2  " + LIndex2);
			
				FPLiteralExpr fExprUpper1=(FPLiteralExpr) expr1.getUpper();
				fUIndex1 = fExprUpper1.getValue().getValue().floatValue();
			//	System.out.println("Upper Index of loop1  " + UIndex1);
				
				FPLiteralExpr fExprUpper2=(FPLiteralExpr) expr2.getUpper();
				fUIndex2 = fExprUpper2.getValue().getValue().floatValue();
			//	System.out.println("Upper Index of loop2  " + UIndex2);
				
				exprOptFlag=checkExprOpt(expr1,expr2);
				if((fLIndex1==fLIndex2 && fUIndex1==fUIndex2) && exprOptFlag) 
			    {
			    	return true;
			    }
			}			
		
	    	
			
	    }*/
	    
	   // return false;
		
		
	//}
	
/*	private boolean checkExprOpt(RangeExpr expr1,RangeExpr expr2){
		boolean flag=false;
		if(expr1.hasIncr() && expr2.hasIncr())
		{						
			if(expr1.getIncr() instanceof IntLiteralExpr && expr2.getIncr() instanceof IntLiteralExpr)
			{
				IntLiteralExpr incExpr1=(IntLiteralExpr)expr1.getIncr();					
				int incValue1=incExpr1.getValue().getValue().intValue();				
				
				IntLiteralExpr incExpr2=(IntLiteralExpr)expr2.getIncr();					
				int incValue2=incExpr2.getValue().getValue().intValue();
				
				if(incValue1==incValue2)
				{
				  flag=true;	
				}
				return flag;
			}
			
			else if(expr1.getIncr() instanceof FPLiteralExpr && expr2.getIncr() instanceof FPLiteralExpr)
			{
				FPLiteralExpr incExpr1=(FPLiteralExpr)expr1.getIncr();					
				float incValue1=incExpr1.getValue().getValue().floatValue();
				
				FPLiteralExpr incExpr2=(FPLiteralExpr)expr2.getIncr();					
				float incValue2=incExpr2.getValue().getValue().floatValue();
				if(incValue1==incValue2)
				{
				  flag=true;	
				}
				return flag;
			}
		}
		else if(!expr1.hasIncr() && !expr2.hasIncr())
		{
				flag=true;
				return flag;
			
		}
		return flag;

		
	}*/
	public LinkedList<Vector<DependenceData>> getVList() {
		return vList;
	}
	public void setVList(Vector<DependenceData> data) {
		vList.add(data);
	}
	public static LinkedList<ForStmt> getFList() {
		return fList;
	}
	public static void setFList(ForStmt fStmt) {
		ForStmt forStmt = fStmt.fullCopy();
		forStmt.getStmtList().removeChild(0); //remove the annotations from the loop body.		
		fList.add(forStmt);
	}
	public IfStmt getIfStmt() {
		return ifStmt;
	}
	public void setIfStmt(IfStmt ifStmt) {
		this.ifStmt = ifStmt;
	}

}
