/*.........................
 * Loop Transformations
 * Author:Amina Aslam.
 * Creation Date:Mar15,2009
 * Whenever a For node with "LoopInterchange" transformation annotated is encountered or visited
 * an object of this class is instantiated.
 */

package natlab.toolkits.DependenceAnalysis;


import ast.AssignStmt;
import ast.ForStmt;
import ast.IntLiteralExpr;
import ast.RangeExpr;
import ast.Stmt;

public class LoopInterchange {
	
//private ForStmt forStmt;
	
//public LoopInterchange(ForStmt fStmt){				
  //forStmt=fStmt;			
//}
	
	/*
	 * This method does the following.
	 * 1.Assign the statement of inner For loop which can be an instance of AssignStmt,ExprStmt to the statement of outer For loop.
	 * 2.Send the transformed AST to pretty printer for generating the MATLAB code.
	 * 
	 */
public void ApplyLoopInterchange(ForStmt fStmt1,ForStmt fStmt2){//interchange fStmt1 with fStmt2.	
	
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
}//end of  interchange

}
