package natlab.toolkits.DependenceAnalysis;
import natlab.ast.ForStmt;
import natlab.ast.Program;
import natlab.ast.AssignStmt;
import natlab.ast.*;
import natlab.ast.ASTNode;
import natlab.toolkits.analysis.ForVisitor;

/*
 * Author:Amina Aslam
 * Date:11 Sep,2009
 * Profiler class does the following.
 * 1.Changes the AST of input file to add additional code 
 *     1.1.Open a .txt file.
 *     1.2.Extract features and their values and writes it to .txt file.
 * 	   1.3.Closes the file.  	 
 */

public class Profiler {
	private Program prog;
	private ForStmt forNode;
	private NameExpr fidNameExpr;
	public Profiler()
	{ 
		fidNameExpr=new NameExpr();
	}	
	public Program getProg() {
		return prog;
	}
	public void setProg(Program prog) {
		this.prog = prog;
	}
	public ForStmt getForNode() {
		return forNode;
	}
	public void setForNode(ForStmt forNode) {
		this.forNode = forNode;
	}
	public void changeAST()
	{
		System.out.println(prog.dumpTreeAll());
		/*AssignStmt fOpenAStmt=new AssignStmt(); //this is for opening up a file. fid=fopen('test.txt', 'a+');
		
		Name fidName=new Name();
		fidName.setID("fid");
		fidNameExpr.setName(fidName);
		fOpenAStmt.setLHS(fidNameExpr);//this set the LHS of AssignStmt.
		ParameterizedExpr fOpenParaExpr=new ParameterizedExpr();
		NameExpr fOpenNameExpr=new NameExpr();
		Name fOpenName=new Name();
		fOpenName.setID("fopen");
		fOpenNameExpr.setName(fOpenName);
		fOpenParaExpr.setTarget(fOpenNameExpr);
		List fOpenList=new List();
		StringLiteralExpr sExpr1=new StringLiteralExpr();		
		sExpr1.setValue("test.txt");
		StringLiteralExpr sExpr2=new StringLiteralExpr();
		sExpr2.setValue("a+");		
		fOpenList.insertChild(sExpr1, 0);
		fOpenList.insertChild(sExpr2, 1);		
		fOpenParaExpr.setArgList(fOpenList);
		fOpenAStmt.setRHS(fOpenParaExpr);
		
		fOpenAStmt.setParent(prog.getChild(1));
		//System.out.println(prog.getChild(1).getChild(0));		
		prog.getChild(1).insertChild(fOpenAStmt, 0);			
		//System.out.println(prog.dumpTreeAll());
		//System.out.println(prog.getPrettyPrinted());
		
		traverseProgramNode();		
		//insert file closing node.This should be the last node in the program.. //fclose(fid)
		ExprStmt fCloseEStmt= new ExprStmt();
		ParameterizedExpr fCloseParaExpr=new ParameterizedExpr();
		NameExpr fCloseNameExpr=new NameExpr();
		Name fCloseName=new Name();
		fCloseName.setID("fclose");
		fCloseNameExpr.setName(fCloseName);
		fCloseParaExpr.setTarget(fCloseNameExpr);
		List fCloseList=new List();
		fCloseList.insertChild(fidNameExpr, 0);
		fCloseParaExpr.setArgList(fCloseList);
		fCloseEStmt.setExpr(fCloseParaExpr);
		//System.out.println(fCloseEStmt.dumpTreeAll());*/		
	}
	
	/*
	 * This function traverses the program node and looks for ForLoop.
	 */
	private void traverseProgramNode()
	{
		int nNode=prog.getChild(1).getNumChild();
		System.out.println(nNode);
		System.out.println(prog.dumpTreeAll());
		for(int i=0;i<nNode;i++)
		{	
			ASTNode node=prog.getChild(1).getChild(i);
			if(node instanceof ForStmt)
			{
				forNode=(ForStmt)node;
				insertFileWriteNode(i);				
			}//end of if
		}//end of for
		System.out.println(prog.getPrettyPrinted());
	}//end of function
	
	
	/*
	 * This function insert a node which adds write to file functionality to MATLAB progam.
	 */
	private void insertFileWriteNode(int nodeNumber)
	{
		//ForVisitor forVisitor = new ForVisitor();
        //prog.apply(forVisitor);
		//insert file writing node after the loop node.
		int sECount=0;
		int nECount=0;
		AssignStmt fPrintfAStmt=new AssignStmt(); //this is for inserting this code count=fprintf(fid,'%d',n,m);
		NameExpr count=new NameExpr();
		Name n1=new Name();
		n1.setID("count");
		count.setName(n1);
		fPrintfAStmt.setLHS(count);//this set the LHS of AssignStmt.
		
		ParameterizedExpr fPrintfParaExpr=new ParameterizedExpr();
		NameExpr fPrintfNameExpr=new NameExpr();
		Name fPrintfName=new Name();
		fPrintfName.setID("fprintf");
		fPrintfNameExpr.setName(fPrintfName);
		fPrintfParaExpr.setTarget(fPrintfNameExpr);
		List fPrintfList=new List();
		StringLiteralExpr sE1=new StringLiteralExpr();		
		sE1.setValue("%s%d\n");
		sECount++;
		StringLiteralExpr sE2=new StringLiteralExpr();
		sE2.setValue("LoopNumber");
		sECount++;
		
		NameExpr lNoNameExpr=new NameExpr();
		Name lNoName=new Name();
		lNoName.setID(new Integer(nodeNumber).toString()); //writing loop no to file
		nECount++;
		
		NameExpr lBoundNameExpr=new NameExpr();		//writing lower bound of loop to file
		Name lBoundName=new Name();
		lBoundName.setID(new Integer(((IntLiteralExpr)((RangeExpr)forNode.getAssignStmt().getLHS()).getLower()).getValue().getValue().intValue()).toString());//writing lower bound to file
		//lBoundName.setID("m");
		nECount++;
		
		NameExpr uBoundNameExpr=new NameExpr();//writing upper bound of loop to file
		Name uBoundName=new Name();
		uBoundName.setID(new Integer(((IntLiteralExpr)((RangeExpr)forNode.getAssignStmt().getLHS()).getUpper()).getValue().getValue().intValue()).toString());//writing upper bound to file
		//uBoundName.setID("n");
		nECount++;
		
		fPrintfList.insertChild(fidNameExpr, 0);
		for(int i=0;i<sECount;i++)
		{
	    	fPrintfList.insertChild(sE1, i);
	    	fPrintfList.insertChild(sE2, i);
		}
		fPrintfList.insertChild(lBoundNameExpr, 1);
		fPrintfList.insertChild(uBoundNameExpr, 2);
		fPrintfParaExpr.setArgList(fPrintfList);
		fPrintfAStmt.setRHS(fPrintfParaExpr);
		//insert the node in the AST
		fPrintfAStmt.setParent(prog.getChild(1).getChild(nodeNumber));
		//System.out.println(prog.getChild(1).getChild(0));		
		prog.getChild(1).insertChild(fPrintfAStmt, nodeNumber+1);
	
		System.out.println(fPrintfAStmt.dumpTreeAll());

		
	}
	
}
