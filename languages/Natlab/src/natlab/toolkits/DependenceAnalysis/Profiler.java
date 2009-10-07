package natlab.toolkits.DependenceAnalysis;
import ast.ForStmt;
import ast.Program;
import ast.AssignStmt;
import ast.*;
import ast.ASTNode;
import natlab.toolkits.analysis.ForVisitor;
import natlab.IntNumericLiteralValue;
import java.math.*;
import natlab.NumericLiteralValue;

//TODO:Write all the code and then start inserting into the location.
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
	private NameExpr fileNameExpr;
	private String fileName;
	private ExpressionFactory eFactory;
	private NameExpr docNode;
	private NameExpr timeStamp;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fName) {
		this.fileName ="";		
		for(int i=0;i<fName.length();i++)
		{
		  char ch=fName.charAt(i);	
		  if(ch=='.') break;
		  else fileName+=ch;		  
		}
		fileName.trim();		
	}
	public Profiler()
	{     
	    eFactory=new ExpressionFactory();
	    fileNameExpr=eFactory.createNameExpr("xmlFileName");
	    timeStamp=eFactory.createNameExpr("S");	
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
		//System.out.println(prog.dumpTreeAll());
		//insertDateTimeNode();
		//insertFileNameVariableNode();
		//traverseProgramNode(); 
		//insertCheckFileExistNode();//needs to call insertFieOpenNode if file doesnot exist.
		//System.out.println(prog.getPrettyPrinted());
		//insertRootNode();
		// insertElseBlock();
		insertCreateElementNode("RunNo");
	
		
		
		
		
		
		//insertFileOpenNode();
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
	 * This function inserts file following code into the ast
	 * 1.xmlFileName=fileName
	 */
	private void insertFileNameVariableNode()
	{
		  //For inserting this variable xmlFileName=fileName		   
		  AssignStmt aStmt=new AssignStmt();		  		  
		  StringLiteralExpr sExpr=new StringLiteralExpr();
		  sExpr.setValue(fileName+".xml");	  
		  aStmt.setLHS(fileNameExpr);
		  aStmt.setRHS(sExpr);
		  aStmt.setOutputSuppressed(true);
		  aStmt.setParent(prog.getChild(1));		  
		  prog.getChild(1).insertChild(aStmt, 2);
	}
	
	
	/*
	 * This function inserts file following code into the ast	 * 
	 * 1.xmlFileName = fullfile('/home/2008/aaslam1', 'dataOutFile.xml');
	 */
	private AssignStmt insertFileOpenNode()
	{
	  	  
	  //For inserting this code to MatLabfile xmlFileName = fullfile('/home/2008/aaslam1', 'dataOutFile.xml');	  	
	  AssignStmt fOAStmt=new AssignStmt();	  
	  fOAStmt.setLHS(fileNameExpr);
	  
	  //ParameterizedExpr fOParaExpr=new ParameterizedExpr();
	  NameExpr fONExpr2=eFactory.createNameExpr("fullfile");	  
	  //fOParaExpr.setTarget(fONExpr2);	 
	  List fOList=new List();
	  StringLiteralExpr fOSE1=new StringLiteralExpr();
	  fOSE1.setValue(" ");
	  StringLiteralExpr fOSE2=new StringLiteralExpr();
	  fOSE2.setValue(fileName+".xml");
	  fOList.insertChild(fOSE1,0);
	  fOList.insertChild(fOSE2,1);
	  //fOParaExpr.setArgList(fOList);
	  fOAStmt.setRHS(eFactory.createParaExpr(fONExpr2, fOList));	
	  fOAStmt.setOutputSuppressed(true);
	  //fOAStmt.setParent(prog.getChild(1).getChild(nodeNumber));				
	 // prog.getChild(1).insertChild(fOAStmt, nodeNumber+1);
	  System.out.println(fOAStmt.dumpTreeAll());
	  return fOAStmt;
	 }
	
	
	/*
	 * This function insert the following code into the ast
	 * 1.[pathstr, name, ext, versn] = fileparts(xmlFileName);
	 * 2.A = exist([pathstr name '.xml'],'file');
	 * 	   
	 */
	private void insertCheckFileExistNode(int nodeNumber)
	{
		AssignStmt fEAStmt1=new AssignStmt();
		MatrixExpr fEMExpr1=new MatrixExpr();
		
		Row fERow=new Row();		
		NameExpr pathStrExpr=eFactory.createNameExpr("pathstr");		
		NameExpr nameExpr=eFactory.createNameExpr("name");				
		NameExpr extExpr=eFactory.createNameExpr("ext");	
		
		fERow.addElement(pathStrExpr);
		fERow.addElement(nameExpr);
		fERow.addElement(extExpr);
		
		fEMExpr1.setRow(fERow, 0);
		fEAStmt1.setLHS(fEMExpr1);
		
		//ParameterizedExpr filePartsParaExpr=new ParameterizedExpr();
		NameExpr filePartsNameExpr=eFactory.createNameExpr("fileparts");		
		//filePartsParaExpr.setTarget(filePartsNameExpr);	

		//filePartsParaExpr.setArg(fileNameExpr, 0);
		fEAStmt1.setRHS(eFactory.createParaExpr(filePartsNameExpr, fileNameExpr, 0));
		fEAStmt1.setOutputSuppressed(true);
		
		AssignStmt fEAStmt2=new AssignStmt();
		NameExpr aNameExpr=eFactory.createNameExpr("A");		
		fEAStmt2.setLHS(aNameExpr);
		
		//ParameterizedExpr existParaExpr=new ParameterizedExpr();
		NameExpr existNameExpr=eFactory.createNameExpr("exist");		
		MatrixExpr existMExpr=new MatrixExpr();
		StringLiteralExpr xmlString=new StringLiteralExpr();
		xmlString.setValue(".xml");
		Row fERow1=new Row();
		fERow1.addElement(pathStrExpr);
		fERow1.addElement(nameExpr);
		fERow1.addElement(xmlString);		
		existMExpr.setRow(fERow1, 0);
		StringLiteralExpr fileString=new StringLiteralExpr();
		fileString.setValue("file");
		List existList=new List();
		existList.insertChild(existMExpr, 0);
		existList.insertChild(fileString, 1);
		//existParaExpr.setTarget(existNameExpr);
		//existParaExpr.setArgList(existList);
		fEAStmt2.setRHS(eFactory.createParaExpr(existNameExpr, existList));
		fEAStmt2.setOutputSuppressed(true);
		fEAStmt1.setParent(prog.getChild(1).getChild(nodeNumber));				
		prog.getChild(1).insertChild(fEAStmt1, nodeNumber+1);
		fEAStmt2.setParent(prog.getChild(1).getChild(nodeNumber));				
		prog.getChild(1).insertChild(fEAStmt2, nodeNumber+2);
		System.out.println(fEAStmt1.dumpTreeAll());
		System.out.println(fEAStmt2.dumpTreeAll());
		insertIfNode(nodeNumber+3);		
	}
	
	/*
	 *This function inserts following node into the AST
	 *
	 *docNode = com.mathworks.xml.XMLUtils.createDocument('AD');
	 *
	 */
	private AssignStmt insertRootNode()
	{
		AssignStmt aStmt=new AssignStmt();
		docNode=eFactory.createNameExpr("docNode");
		aStmt.setLHS(docNode);
		StringLiteralExpr sExpr=new StringLiteralExpr();
		sExpr.setValue("AD");
		Name createDocument=eFactory.createName("createDocument");
		Name XMLUtils=eFactory.createName("XMLUtils");
		Name xml=eFactory.createName("xml");
		Name mathworks=eFactory.createName("mathworks");
		
		DotExpr dExpr1=new DotExpr();
		DotExpr dExpr2=new DotExpr();
		dExpr2.setField(createDocument);		
		
		DotExpr dExpr3=new DotExpr();
		dExpr3.setField(XMLUtils);
		
		DotExpr dExpr4=new DotExpr();
		dExpr4.setField(xml);
		
		DotExpr dExpr5=new DotExpr();
		dExpr5.setField(mathworks);
		
		//DotExpr dExpr6=new DotExpr();
		dExpr5.setTarget(eFactory.createNameExpr("com"));
		dExpr4.setTarget(dExpr5);
		dExpr3.setTarget(dExpr4);
		dExpr2.setTarget(dExpr3);		
		ParameterizedExpr pExpr=eFactory.createParaExpr(dExpr2,sExpr , 0);		
		aStmt.setRHS(pExpr);
		aStmt.setOutputSuppressed(true);
		return aStmt;
		//System.out.println(aStmt.dumpTreeAll());
		//System.out.println(aStmt.getPrettyPrinted());
	}
	
	
	/*
	 *This function inserts date time writing code to the program  
	 *1.t=now
	 *2.S = datestr(t);
	 */
	private void insertDateTimeNode()
	{
		AssignStmt dTAStmt1=new AssignStmt();
		NameExpr dTNExpr1=eFactory.createNameExpr("t");		
		dTAStmt1.setLHS(dTNExpr1);
		
		NameExpr dTNExpr2=eFactory.createNameExpr("now");
		dTAStmt1.setRHS(dTNExpr2);
		dTAStmt1.setOutputSuppressed(true);
		
		//This code is inserted into the AST S = datestr(t);
		//AssignStmt dTAStmt2=new AssignStmt();
		//NameExpr dTNExpr3=eFactory.createNameExpr("S");		
		//dTAStmt2.setLHS(dTNExpr3);
		AssignStmt dTAStmt2 =new AssignStmt();				
		dTAStmt2.setLHS(timeStamp);
		
		
		//ParameterizedExpr dTParaExpr=new ParameterizedExpr();
		NameExpr dTNExpr4=eFactory.createNameExpr("datestr");				
		//dTParaExpr.setTarget(dTNExpr4);
		//List dTList=new List();
		//dTList.insertChild(dTNExpr1,0);	
		//dTParaExpr.setArgList(dTList);
		dTAStmt2.setRHS(eFactory.createParaExpr(dTNExpr4, dTNExpr1, 0));
		dTAStmt2.setOutputSuppressed(true);
		//insert the siblings to the AST
		dTAStmt1.setParent(prog.getChild(1));				
		prog.getChild(1).insertChild(dTAStmt1, 0);
		dTAStmt2.setParent(prog.getChild(1));				
		prog.getChild(1).insertChild(dTAStmt2, 1);
		
		System.out.println(dTAStmt1.dumpTreeAll());
		System.out.println(dTAStmt2.dumpTreeAll());
		//System.out.println(prog.getPrettyPrinted());
		
		
	}
	/*
	 * This method inserts the following code into the AST.
	 * if(A==2)  
          xDoc=xmlread([xmlFileName '.xml']);       
          xRoot=xDoc.getDocumentElement;
       else   
       end
	 */
	private void insertIfNode(int index)
	{
		
		//For inserting this code to ast if(A==2)
	    IfStmt ifStmt=new IfStmt();
	    IfBlock ifBlock=new IfBlock();
	    EQExpr eqExpr=new EQExpr();
	    NameExpr aNameExpr= eFactory.createNameExpr("A");	    
	    IntLiteralExpr incExpr=new IntLiteralExpr();					
		BigInteger incValue=new BigInteger(new Integer(2).toString());						
		incExpr.setValue(new natlab.DecIntNumericLiteralValue(incValue.toString()));
		eqExpr.setLHS(aNameExpr);
		eqExpr.setRHS(incExpr);		
		
		//For inserting this code into the AST xDoc=xmlread([xmlFileName '.xml']);
		AssignStmt xDocAStmt=new AssignStmt();
		NameExpr xDocNExpr=eFactory.createNameExpr("xDoc");
		xDocAStmt.setLHS(xDocNExpr);
		
		//ParameterizedExpr xmlReadPExpr=new ParameterizedExpr();
		NameExpr xmlReadNExpr=eFactory.createNameExpr("xmlRead");		
		//xmlReadPExpr.setTarget(xmlReadNExpr);
		
		//MatrixExpr xmlReadMExpr=new MatrixExpr();
		//Row xmlReadRow=new Row();
		//NameExpr xmlFileNameExpr=eFactory.createNameExpr("xmlFileName");
		//StringLiteralExpr xmlString=new StringLiteralExpr();
		//xmlString.setValue(".xml");		
		//xmlReadRow.addElement(xmlFileNameExpr);
		//xmlReadRow.addElement(xmlString);				
		//xmlReadMExpr.setRow(xmlReadRow, 0);
		xDocAStmt.setRHS(eFactory.createParaExpr(xmlReadNExpr, fileNameExpr, 0));
		xDocAStmt.setOutputSuppressed(true);
		
		//For inserting this code into the AST xRoot=xDoc.getDocumentElement;
		AssignStmt xRootAStmt=new AssignStmt();
		NameExpr xRootNExpr=eFactory.createNameExpr("xRoot");
		xRootAStmt.setLHS(xRootNExpr);
		DotExpr xDocDExpr=new DotExpr();
		Name n=new Name();
		n.setID("getDocumentElement");
		xDocDExpr.setTarget(xDocNExpr);
		xDocDExpr.setField(n);
		xRootAStmt.setRHS(xDocDExpr);
		xRootAStmt.setOutputSuppressed(true);
		//Adding statements to if block.
		List ifList=new List();
		ifList.insertChild(xDocAStmt, 0); //needs to change this to handle more statements.
		ifList.insertChild(xRootAStmt, 1);
		//TODO: insert Statements here for children.. Look into this 
		insertCreateElementNode("RunNo"); 
		insertCreateElementNode("LoopNo");
		insertCreateElementNode("LowerBound");
		ifBlock.setCondition(eqExpr);
		ifBlock.setStmtList(ifList);
		ifStmt.setIfBlock(ifBlock, 0);
		
		prog.getChild(1).insertChild(ifStmt, index);
		ifStmt.setParent(prog.getChild(1));
		System.out.println(ifStmt.dumpTreeAll()); 
	 }
	/*
	 * This function inserts the following node into the ast
	 * else
	 *   statements
	 *   e.g.docNode = com.mathworks.xml.XMLUtils.createDocument('AD');
	 *   
	 */
	private void insertElseBlock()
	{
		ElseBlock elseBlock=new ElseBlock();
		elseBlock.addStmt(insertFileOpenNode());
		elseBlock.addStmt(insertRootNode());
		System.out.println(elseBlock.dumpTreeAll());
		System.out.println(elseBlock.getPrettyPrinted());
	}
	
	/*
	 * This node inserts following code into the AST
	 * 1.Would create an element in the docNode
	 * e.g.elRunNo = docNode.createElement('RunNo');
	 * TODO:pass the attribute Name also if needs to set it.overload this method 
	 */
	private void insertCreateElementNode(String nodeName)
	{
	   AssignStmt aStmt=new AssignStmt();
	   NameExpr nExpr1=eFactory.createNameExpr(nodeName);
	   aStmt.setLHS(nExpr1);
	   
	   DotExpr dExpr=new DotExpr();
	   NameExpr nExpr2=eFactory.createNameExpr("docNode"); //needs to look at it.
	   Name createElement=new Name();
	   createElement.setID("createElement");
	   StringLiteralExpr sExpr=new StringLiteralExpr();
	   sExpr.setValue(nodeName);
	   dExpr.setTarget(nExpr2);
	   dExpr.setField(createElement);
	   ParameterizedExpr pExpr=eFactory.createParaExpr(dExpr, sExpr, 0);
	   aStmt.setRHS(pExpr);
	   aStmt.setOutputSuppressed(true);
	   insertAppendChildNode(nExpr1,"VariableName");
	   insertAppendChildNode(nExpr1,"Range");
	   
	   StringLiteralExpr s=new StringLiteralExpr(); // these three lines are temporary code
	   s.setValue("TimeStamp");
	   insertSetAttributeNode(nExpr1,s,timeStamp);
	   
	   System.out.println(aStmt.dumpTreeAll());   	   
	}
	
	/*
	 * This function inserts the following code into the AST
	 * 1.Would append children to existing nodes.
	 * e.g. elLBVariableName=elLowerBound.appendChild(docNode.createElement('VariableName')); 
	 */
	private void insertAppendChildNode(NameExpr parentName,String Name)
	{
		AssignStmt aStmt=new AssignStmt();
		NameExpr nExpr=eFactory.createNameExpr(parentName.getName().getVarName()+"child");
		aStmt.setLHS(nExpr);
		
		DotExpr dExpr1=new DotExpr();
		dExpr1.setTarget(parentName);
		Name appendChild=new Name("appendChild");
		dExpr1.setField(appendChild);
		
		DotExpr dExpr2=new DotExpr();
		NameExpr nExpr2=eFactory.createNameExpr("docNode"); //needs to look at it.
		Name createElement=new Name();
		createElement.setID("createElement");
		StringLiteralExpr sExpr=new StringLiteralExpr();
		sExpr.setValue(Name);
		dExpr2.setTarget(nExpr2);
		dExpr2.setField(createElement);
		ParameterizedExpr pExpr1=eFactory.createParaExpr(dExpr2, sExpr, 0);
		ParameterizedExpr pExpr2=eFactory.createParaExpr(dExpr1,pExpr1,0);
		aStmt.setRHS(pExpr2);
		aStmt.setOutputSuppressed(true);
		System.out.println(aStmt.dumpTreeAll());
		
	}
	/*
	 * This function inserts setAttribute field of an element.
	 * e.g.elRunNo.setAttribute('TimeStamp',S);
	 */
	private void insertSetAttributeNode(NameExpr callerExpr,StringLiteralExpr attributeName,NameExpr attributeValue)
	{
		
		ExprStmt attributeStmt= new ExprStmt();
		//ParameterizedExpr attributeParaExpr=
		Name setAttribute=eFactory.createName("setAttribute");
	    DotExpr attributeDotExpr=new DotExpr();	
	    attributeDotExpr.setTarget(callerExpr);
	    attributeDotExpr.setField(setAttribute);
	    List attributeList=new List();
	    attributeList.insertChild(attributeName, 0);
	    attributeList.insertChild(attributeValue, 1);
	    ParameterizedExpr attributeParaExpr=eFactory.createParaExpr(attributeDotExpr,attributeList);
	    attributeStmt.setExpr(attributeParaExpr);
	    attributeStmt.setOutputSuppressed(true);
	    System.out.println(attributeStmt.dumpTreeAll());
	    System.out.println(attributeStmt.getPrettyPrinted());
	    		
	}
	
	
	/*
	 * This function traverses the program node and looks for ForLoop.
	 */
	private void traverseProgramNode()
	{
		int nNode=prog.getChild(1).getNumChild();
		//System.out.println(nNode);
		//System.out.println(prog.dumpTreeAll());
		for(int i=0;i<nNode;i++)
		{	
			ASTNode node=prog.getChild(1).getChild(i);
			if(node instanceof ForStmt)
			{
				forNode=(ForStmt)node;
				insertCheckFileExistNode(i);				
			}//end of if
		}//end of for
		//System.out.println(prog.getPrettyPrinted());
	}//end of function
	
	
	/*
	 * This function insert a node which adds write to file functionality to MATLAB progam.
	 */
/*	private void insertFileWriteNode(int nodeNumber)
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
		
		//fPrintfList.insertChild(fidNameExpr, 0);
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

		
	}*/
	
}
