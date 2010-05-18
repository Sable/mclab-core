/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopFission" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */
package natlab.toolkits.DependenceAnalysis;

import ast.ASTNode;
import ast.ForStmt;
import ast.Stmt;

public class LoopFission {
	
	private ForStmt forStmt;
	private int fissionPoint;
	private boolean nestedStatus;
	private int nestedLevel;
	public LoopFission(ForStmt fStmt,int fPoint){	
				forStmt=fStmt;			
				fissionPoint=fPoint;
				//nestedStatus=false;
				//nestedLevel=0;
				//checkForNestedLoops();		
	}
	/*
	 * This method checks the nesting level of the loop and applies loop fission
	 * at the nested loop if it is annotated with "LoopFission".
	 */
/*	private void checkForNestedLoops(){
		ast.List<Stmt> fList=forStmt.getStmts();
		for(int k=0;k<fList.getNumChild();k++){
			
			if(fList.getChild(k) instanceof ForStmt){
				ForStmt tforStmt=(ForStmt)fList.getChild(k);
				if(tforStmt.isEligibleForLoopFission()){
					nestedLevel++;
					ast.List<Stmt> forList=tforStmt.getStmtList();
					forList.removeChild(0);
					nestedStatus=true;
					ApplyLoopFission(fissionPoint,tforStmt);
				}
			}
		}
		if(!nestedStatus){
			//natlab.ast.List<Stmt> forList=forStmt.getStmtList();
			//forList.removeChild(0);
			ApplyLoopFission(fissionPoint,forStmt);
			
		}*/
	
		
	//}
	/*
	 * This method does the following.
	 * 1.Break the loop at the fission point.
	 * 2.Add the new loop to AST.
	 * 3.Send the transformed AST to pretty printer for generating new MATLAB code from the transformed AST.
	 */
   public void ApplyLoopFission(){		 
		
		int no=forStmt.getNumStmt();		
		ForStmt forStmt2=new ForStmt();
		ForStmt forStmt1=new ForStmt();
		
		forStmt1.setAssignStmt(forStmt.getAssignStmt()); //for setting the range expression
		forStmt2.setAssignStmt(forStmt.getAssignStmt()); //for setting the range expression
		
	
		
		ast.List<Stmt> forList1 = new ast.List<Stmt>();
		ast.List<Stmt> forList2 = new ast.List<Stmt>();		
		
		for(int i=0;i<fissionPoint;i++){			
			//forStmt1.addStmt(forStmt.getStmt(i));
			forList1.add(forStmt.getStmt(i));
		}
		
	    for(int j=fissionPoint;j<no;j++){
			//forStmt2.addStmt(forStmt.getStmt(j));
			forList2.add(forStmt.getStmt(j));				
		}
			
	    forStmt1.setStmtList(forList1);  //add the loop statements to For node
	    forStmt2.setStmtList(forList2);  //add the loop statements to For node		
		
		ASTNode parent = forStmt.getParent();
		int loc = parent.getIndexOfChild(forStmt);
		//if(nestedStatus){			
			///for(int i=0;i<nestedLevel;i++){
				//forStmt.setStmt(forStmt1, 1);
				//forStmt.setStmt(forStmt2, 2);
			//}		
		//}
		//else{
			parent.setChild(forStmt1, loc);		
			parent.insertChild(forStmt2, loc+1);
			
		//}
	
				
	}

}
