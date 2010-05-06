package natlab.toolkits.DependenceAnalysis;

import java.io.*;
import ast.ASTNode;
import ast.ForStmt;
import ast.IfStmt;
import ast.Program;
import ast.Stmt;
import ast.SwitchStmt;
import natlab.toolkits.analysis.*;

public class ProfilerDriver extends ForVisitor{
	//private Program program;
	private ForStmt forStmt;
	private ForStmt forStmtArray[];	
	private int loopIndex=0;
	private static Profiler prof=new Profiler();
	private String fileName;	
	//public ProfilerDriver(ForStmt fStmt)
	
	public ProfilerDriver(){	

	}

	public void traverseFile(Program prog){
		//ForVisitor forVisitor ;//= new ForVisitor();
		
	    prog.apply(this);    
	   
	}
	public void caseLoopStmt(ASTNode node){
		
		if (node instanceof ForStmt){
			//System.out.println("Dependence Analyzer caseLoopStmt is called by "+ node.getClass().getName());			
			ForStmt fNode=(ForStmt) node;
			forStmt=fNode;
			traverseForNode();
		}
		else{
			node.applyAllChild(this);
		}
	}

	public void caseIfStmt(IfStmt node){
		//System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
		IfStmt ifNode=(IfStmt) node;
		//ForVisitor fVisitor=new ForVisitor(); 
		//ifNode.applyAllChild(fVisitor);
	}

	public void caseSwitchStmt(SwitchStmt node){
		//System.out.println("caseLoopStmt is called by "+ node.getClass().getName());
		SwitchStmt sNode=(SwitchStmt)node;
		//ForVisitor fVisitor=new ForVisitor(); 
		//sNode.applyAllChild(fVisitor);
	}

	public void caseBranchingStmt(ASTNode node){}

	public void caseASTNode(ASTNode node) {}
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
    
	public void traverseForNode()
	{
		 //forStmt=fStmt;
		 loopIndex=0;
		 //System.out.println("3333"+forStmt.getPrettyPrinted());
         forStmtArray=new ForStmt[forStmt.getNumChild()+1];		
         //System.out.println("33336666666666:::::"+forStmt.getNumChild()+loopIndex);
		 forStmtArray[loopIndex]=forStmt;		  
		 isTightlyNestedLoop(forStmt);		 
		 //prof.setFileName(fileName);//This is already done in Main.java		 
		 prof.insertLoopNo(forStmtArray);
		 prof.insertFunctionCall(loopIndex,forStmtArray);         
	    //call a function in profiler with nesting level added to it
	    //nesting level would be forStmtArray.size;		
	}
	public void setFileName(String fName){
		prof.setFileName(fName);//calls the setFileName of profiler.
		fileName=prof.getFileName();
	}
	public String getFileName() {
		return fileName;
	}
	
	/*
	 * 
	 * This function does the following
	 * 1.It creates a directory with the same name as that of the input .mFile 
	 * 2.It creates and writes to a file which has the same name as that of input .mFile with profiled word attached to it.Saves the file in this new directory.
	 * 3.It then writes the instrumented code to this new file
	 * 
	 */
	public void generateInstrumentedFile(Program prog){
			
	    File f = new File(fileName);//this checks for the presence of directory	    
		if(!f.exists()){
           f.mkdir();
		}
           Writer output;        
           File file = new File(fileName+"/"+"Profiled"+ fileName + ".m");
           try {
			  output = new BufferedWriter(new FileWriter(file));
			  output.write(prog.getPrettyPrinted());
	          output.close();
		   } catch (IOException e) {			  
			  e.printStackTrace();
		 }   	
	}//end of function 

}
