package natlab.toolkits.DependenceAnalysis;

import ast.ForStmt;
import ast.Program;
import ast.Stmt;
import natlab.toolkits.analysis.*;

public class ProfilerDriver {
	private Program program;
	private ForStmt forStmt;
	private ForStmt forStmtArray[];	
	private int loopIndex=0;
	private static Profiler prof=new Profiler();
	private String fileName;
	//public ProfilerDriver(ForStmt fStmt)
	public ProfilerDriver()
	{	
		//forStmt=fStmt;
		//program=prog;
		
		
		//fileName=fName;
	}
	/*
	 * This function does the following 
	 * 1.Checks for tightly nested loops.
	 */	
    private void isTightlyNestedLoop(ForStmt fStmt)
    {
			 
       Stmt stmt=fStmt.getStmt(0);
       if(stmt instanceof ForStmt && stmt!=null)
        { loopIndex++;			  
		  ForStmt tForStmt=(ForStmt)stmt;
		  forStmtArray[loopIndex]=tForStmt;
		  forStmt=tForStmt;				  
		  isTightlyNestedLoop(tForStmt);				  
         }//end of if
	}//end of function
    
	public void traverseForNode(ForStmt fStmt)
	{
		 forStmt=fStmt;
		 loopIndex=0;
		 //System.out.println("3333"+forStmt.getPrettyPrinted());
         forStmtArray=new ForStmt[forStmt.getNumChild()+1];		
         //System.out.println("33336666666666:::::"+forStmt.getNumChild()+loopIndex);
		 forStmtArray[loopIndex]=forStmt;		  
		 isTightlyNestedLoop(forStmt);		 
		 prof.setFileName(fileName);		 
		 prof.insertLoopNo(forStmtArray);
		 prof.insertFunctionCall(loopIndex,forStmtArray);         
	    //call a function in profiler with nesting level added to it
	    //nesting level would be forStmtArray.size;
		
	}
	public void setFileName(String fName)
	{
		fileName=fName;
	}

}
