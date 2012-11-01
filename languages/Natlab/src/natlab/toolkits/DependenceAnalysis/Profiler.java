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

package natlab.toolkits.DependenceAnalysis;
import ast.*;
import natlab.IntNumericLiteralValue;
import java.math.*;
import java.util.LinkedList;
import java.util.StringTokenizer;

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
	private ForStmt forNode;	
	private String fileName;
	private ExpressionFactory eFactory;	
	private LinkedList<ForStmt> forStmtList;
	//private static int loopNo=0;
	private int loopNo=0;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fName) {
		StringTokenizer st = new StringTokenizer(fName,".");		
		fileName=st.nextToken();
		//System.out.println(fileName);
		/*this.fileName ="";		
		for(int i=0;i<fName.length();i++)
		{
		  char ch=fName.charAt(i);	
		  if(ch=='.') break;
		  else fileName+=ch;		  
		}
		fileName.trim();*/		
	}
	public Profiler(){     
	    eFactory=new ExpressionFactory();    
	    
	}
	
/*
 * This function keeps track of all the variables 
 * assigned in the loop to check for parallelization.
 */
public MTransposeExpr recordVariables(ForStmt fStmt){	
	int size=fStmt.getNumStmt();
	Row vRow=new Row();
	for(int i=0;i<size;i++){
	  if(fStmt.getStmt(i) instanceof AssignStmt){
	    AssignStmt aStmt=(AssignStmt)fStmt.getStmt(i);
	    if(aStmt.getLHS() instanceof NameExpr){     	
	      vRow.addElement(new StringLiteralExpr(((NameExpr)aStmt.getLHS()).getVarName()));
	      if(i!=size-1)vRow.addElement(new StringLiteralExpr(":"));
	    }//end of NameExpr if	    
	 }//end of if
   }//end of for	
   MatrixExpr vMExpr=new MatrixExpr();
   vMExpr.addRow(vRow);
   MTransposeExpr vMTExpr=new MTransposeExpr();
   vMTExpr.setOperand(vMExpr);
   return vMTExpr;
}

	
/*
*
* This function annotates the loop with loop no
* 
*/
public void insertLoopNo(LinkedList<ForStmt> fStmtList){
   forStmtList=fStmtList;
   int size=fStmtList.size();
   Stmt stmt=fStmtList.get(size-1).getStmt(0);
   if(stmt instanceof AssignStmt){   
		AssignStmt aStmt=(AssignStmt)stmt;
		if(aStmt.getLHS() instanceof NameExpr){
			NameExpr nExpr=(NameExpr)aStmt.getLHS();
			if(!(nExpr.getName().getVarName().equals("lNum"))){ 
				insertStmt();					
			}//end of 3rd if				
		}//end of 2nd if
		else insertStmt();
	 }//end of 1st if
	else insertStmt();				
}
   /*
    * This function inserts the loop no variable into the loop body.
    */
private void insertStmt(){
	    AssignStmt lNAStmt=new AssignStmt();
		NameExpr lNExpr=eFactory.createNameExpr("lNum");
		FPLiteralExpr lNoExpr=new FPLiteralExpr();
		loopNo++;
		Float iObj=new Float(loopNo);    		        											
		lNoExpr.setValue(new natlab.FPNumericLiteralValue(iObj.toString()));
		lNAStmt.setLHS(lNExpr);
		lNAStmt.setRHS(lNoExpr);
		lNAStmt.setOutputSuppressed(true);
		int size=forStmtList.size();
		lNAStmt.setParent(forStmtList.get(size-1));			
		List tList=new List();
		int a=0;
		if(forStmtList.get(size-1).getStmt(0) instanceof ExpandedAnnotation) {			
			tList.insertChild(forStmtList.get(size-1).getStmt(0), 0);
			tList.insertChild(lNAStmt, 1);
			a=1;
		}
		else {
			tList.insertChild(lNAStmt, 0);
			a=0;
		}
		for(int i=a;i<forStmtList.get(size-1).getNumStmt();i++){
		  tList.insertChild(forStmtList.get(size-1).getStmt(i), i+1);							
		}
		forStmtList.get(size-1).setStmtList(tList);
   }

	
	
	/*
	 * This function inserts the following code into the ast
	 * xmlCodeGenerator('test',1,lVariableName,lowerBound,loopIncFactor,upperBound,nestingLevel);
	 */
public void insertFunctionCall(int nestingLevel,LinkedList<ForStmt> fStmtList,int maxLoopNo,MTransposeExpr mExpr){
		int insertLevel=0;
		ExprStmt fCExprStmt=new ExprStmt();
		NameExpr fCNExpr=eFactory.createNameExpr("xmlDataGenerator");
		List fCList=new List();
		//forStmtArray=fStmtArray;
		
		StringLiteralExpr fileNameExpr=new StringLiteralExpr();
		fileNameExpr.setValue(fileName);//create an expression for fileName
		
		IntLiteralExpr nLevelExpr=new IntLiteralExpr();
		Integer iObj;
		//iObj=new Integer(nestingLevel+1);		    		        											
		iObj=new Integer(nestingLevel);
		nLevelExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));//creates an expression for nestingLevel.
		
		IntLiteralExpr lNoExpr=new IntLiteralExpr();
		Integer lNoObj;
		lNoObj=new Integer(loopNo);		    		        											
		lNoExpr.setValue(new natlab.DecIntNumericLiteralValue(lNoObj.toString()));//creates an expression for loopNo.
		
		IntLiteralExpr mLExpr=new IntLiteralExpr();
		Integer mLObj;
		mLObj=new Integer(maxLoopNo);		    		        											
		mLExpr.setValue(new natlab.DecIntNumericLiteralValue(mLObj.toString()));//creates an expression for maxLoopNo.
		
		
		
		fCList.insertChild(fileNameExpr, 0);
		fCList.insertChild(createLoopVariableNameExpr(nestingLevel), 1);
		fCList.insertChild(createLowerBoundExpr(nestingLevel), 2);
		fCList.insertChild(createUpperBoundExpr(nestingLevel), 3);
		fCList.insertChild(createLoopIncExpr(nestingLevel), 4);
		fCList.insertChild(nLevelExpr, 5);
		fCList.insertChild(lNoExpr, 6);
		fCList.insertChild(mLExpr, 7);
		fCList.insertChild(mExpr, 8);
		
		insertLevel=forStmtList.get(0).getParent().getIndexOfChild(forStmtList.get(0))+1;		
		ParameterizedExpr pExpr=eFactory.createParaExpr(fCNExpr, fCList);
		
		fCExprStmt.setExpr(pExpr);
		fCExprStmt.setOutputSuppressed(true);	
		fCExprStmt.setParent(forStmtList.get(0).getParent());				
		//System.out.println("Node Id is"+(forStmtArray[0].getParent().getIndexOfChild(forStmtArray[0])));
		forStmtList.get(0).getParent().insertChild(fCExprStmt, insertLevel);
		//forStmtArray[0].getParent().insertChild(fCExprStmt, 4);
		//prog.getChild(1).insertChild(dTAStmt1, 0);		
}
	
	
	/*
	 * This function inserts following code into the program.
	 * 1.lVariableName=['i','j','k']';
	 */
private Expr createLoopVariableNameExpr(int nestingLevel){
		if(nestingLevel >=1){		  	
			
		  Row lVariableNamesRow=new Row();
		  //for(int i=0;i<forStmtArray.length;i++)
		  int level=nestingLevel+1;
		  for(int i=0;i<level;i++){			  
			 AssignStmt aStmt=new AssignStmt();
			 aStmt=forStmtList.get(i).getAssignStmt();			  
			 if(aStmt.getLHS() instanceof NameExpr){
			   StringLiteralExpr sExpr=new StringLiteralExpr();
			   sExpr.setValue(((NameExpr)aStmt.getLHS()).getName().getVarName());
			   lVariableNamesRow.addElement(sExpr);
			 }			 
			 if(i<(nestingLevel)){lVariableNamesRow.addElement(new StringLiteralExpr(":"));}			 
		  }
		  MatrixExpr lVariableNamesMExpr=new MatrixExpr();
		  lVariableNamesMExpr.addRow(lVariableNamesRow);
		  //MTransposeExpr lVariableNamesMTExpr=new MTransposeExpr();
		  //lVariableNamesMTExpr.setOperand(lVariableNamesMExpr);
		  //return lVariableNamesMTExpr;
		  return lVariableNamesMExpr;
		  
		}
		else{
		  AssignStmt aStmt=forStmtList.get(0).getAssignStmt();
		  if(aStmt.getLHS() instanceof NameExpr){
			   StringLiteralExpr sExpr=new StringLiteralExpr();
			    sExpr.setValue(((NameExpr)aStmt.getLHS()).getName().getVarName());
			   return sExpr;
		   }
		  return aStmt.getLHS();//TODO:needs to fix this 
		}
}
	
	/*
	 * This function inserts following code into the program.
	 * 1.loopIncFactor =[1 2 3]'; %this is a loopInc factor column vector which has following values(1,2,3) depending on nesting levels.
	 * TODO:1.need to handle all the expression types
	 *       
	 */
private Expr createLoopIncExpr(int nestingLevel){
		
		if(nestingLevel >=1){
		  Row lIncRow=new Row();
		  //for(int i=0;i<forStmtArray.length;i++)
		  for(int i=0;i<nestingLevel+1;i++){
			 AssignStmt aStmt=forStmtList.get(i).getAssignStmt();		     
			 if(aStmt.getRHS() instanceof RangeExpr){
			   RangeExpr rExpr=(RangeExpr) aStmt.getRHS();
			   if(rExpr.hasIncr())lIncRow.addElement(rExpr.getIncr());
			   else{      
				 IntLiteralExpr incExpr=new IntLiteralExpr();
				 Integer iObj=new Integer(1);        											
				 incExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
				 lIncRow.addElement(incExpr);			    
			   }
			 }
		  }
		  MatrixExpr lIncMExpr=new MatrixExpr();
		  lIncMExpr.addRow(lIncRow);
		  MTransposeExpr lIncMTExpr=new MTransposeExpr();
		  lIncMTExpr.setOperand(lIncMExpr);		  
		  return lIncMTExpr;
		  
		}
		else{
			  AssignStmt aStmt=forStmtList.get(0).getAssignStmt();
			  if(aStmt.getRHS() instanceof RangeExpr){
				   RangeExpr rExpr=(RangeExpr) aStmt.getRHS();	 				   
				   if(rExpr.hasIncr())return rExpr.getIncr();
				   else{      
					   IntLiteralExpr incExpr=new IntLiteralExpr();
					   Integer iObj=new Integer(1);        											
					   incExpr.setValue(new natlab.DecIntNumericLiteralValue(iObj.toString()));
					   return incExpr;			    
				   }
			   }
			  return aStmt.getRHS();//TODO:needs to fix this
		}	
		
}
	
	/*
	 * This function inserts following code into the program.
	 * 1.lowerBound =[1 2 3]'; %this is a lowerBound column vector which has following values(1,2,3) depending on nesting levels.
	 *TODO:1.need to handle all the expression types 
	 */
private Expr createLowerBoundExpr(int nestingLevel){
		//AssignStmt lBoundAStmt =new AssignStmt();
		//lBoundNExpr=eFactory.createNameExpr("lowerBound");
		//lBoundAStmt.setLHS(lBoundNExpr);
		if(nestingLevel >=1)
		{
			Row lBoundRow=new Row();
		  //for(int i=0;i<forStmtArray.length;i++)
		  for(int i=0;i<nestingLevel+1;i++){
			 AssignStmt aStmt=forStmtList.get(i).getAssignStmt();		     
			 if(aStmt.getRHS() instanceof RangeExpr)
			 {
			   RangeExpr rExpr=(RangeExpr) aStmt.getRHS();	 
			   lBoundRow.addElement(rExpr.getLower());			   
			 }
			 
		  }
		  MatrixExpr lBoundMExpr=new MatrixExpr();
		  lBoundMExpr.addRow(lBoundRow);
		  MTransposeExpr lBoundMTExpr=new MTransposeExpr();
		  lBoundMTExpr.setOperand(lBoundMExpr);
		  //lBoundAStmt.setRHS(lBoundMTExpr);
		  //lBoundAStmt.setOutputSuppressed(true);
		  return lBoundMTExpr;
		  //return lBoundMExpr;
		  
		}
		else{
			  AssignStmt aStmt=forStmtList.get(0).getAssignStmt();
			  if(aStmt.getRHS() instanceof RangeExpr){
				   RangeExpr rExpr=(RangeExpr) aStmt.getRHS(); 			   
				   return rExpr.getLower();
			   }
			  return aStmt.getRHS();//TODO:needs to fix this 
			  
			  //lBoundAStmt.setOutputSuppressed(true);
		}
		//lBoundAStmt.setParent(forStmtArray[0].getParent());				
		//System.out.println(fCExprStmt.getPrettyPrinted());
		//forStmtArray[0].getParent().insertChild(lBoundAStmt, ((forStmtArray[0].nestedLevel)+insertLevel));
		//forStmtArray[0].getParent().insertChild(lBoundAStmt, 3);	
		
}

	/*
	 * This function prepares the upperBound parameter of the function call.
	 */
private Expr createUpperBoundExpr(int nestingLevel){
		
		if(nestingLevel >=1)
		{
			Row uBoundRow=new Row();
		  //for(int i=0;i<forStmtArray.length;i++)
		  for(int i=0;i<nestingLevel+1;i++)
		  {
			 AssignStmt aStmt=forStmtList.get(i).getAssignStmt();		     
			 if(aStmt.getRHS() instanceof RangeExpr){
			   RangeExpr rExpr=(RangeExpr) aStmt.getRHS();	 
			   uBoundRow.addElement(rExpr.getUpper());
			 }
		  }
		  MatrixExpr uBoundMExpr=new MatrixExpr();
		  uBoundMExpr.addRow(uBoundRow);
		  MTransposeExpr uBoundMTExpr=new MTransposeExpr();
		  uBoundMTExpr.setOperand(uBoundMExpr);
		  return uBoundMTExpr;	 
		  
		}
		else{
		  AssignStmt aStmt=forStmtList.get(0).getAssignStmt();
	      if(aStmt.getRHS() instanceof RangeExpr){
			  RangeExpr rExpr=(RangeExpr) aStmt.getRHS();			  
			  return rExpr.getUpper();
		  }		 
			return aStmt.getRHS();
		}
		
  }//end of function
}
	












	/*
	 * This function inserts file following code into the ast
	 * 1.xmlFileName=fileName
	 * 2.It attaches .xml extension to the file name
	 */
	/*private void insertFileNameVariableNode()
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
	}*/
	
	
	/*
	 * This function inserts file following code into the ast	 * 
	 * 1.xmlFileName = fullfile('/home/2008/aaslam1', 'dataOutFile.xml');
	 */
/*	private AssignStmt insertFileOpenNode()
	{
	  	  
	  //For inserting this code to MatLabfile xmlFileName = fullfile('/home/2008/aaslam1', 'dataOutFile.xml');	  	
	  AssignStmt fOAStmt=new AssignStmt();	  
	  //fOAStmt.setLHS(fileNameExpr);
	  
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
	 * This function inserts code for this statement into the AST.
	 * xmlwrite(xmlFileName, docNode); 
	 */
/*	private void insertFileWriteNode()
	{
		NameExpr xmlWriteNExpr=eFactory.createNameExpr("xmlwrite");
		List xmlWriteList=new List();
		//xmlWriteList.add(fileNameExpr);
		xmlWriteList.add(docNode);
		ParameterizedExpr xmlParaExpr=eFactory.createParaExpr(xmlWriteNExpr, xmlWriteList);		
		System.out.println(xmlParaExpr.dumpTreeAll());
		
	}*/
	
	/*
	 * This function adds following code into the AST.
	 * xDoc = xmlread([xmlFileName, '.xml']);
	 */
/*	private void insertReadFileNode()
	{
	   AssignStmt docAStmt=new AssignStmt();
	   docAStmt.setLHS(docNode);
	   NameExpr xmlReadNExpr=eFactory.createNameExpr("xmlread");
	   //ParameterizedExpr readFilePExpr=eFactory.createParaExpr(xmlReadNExpr, fileNameExpr, 0);
	   //docAStmt.setRHS(readFilePExpr);
	   //docAStmt.setOutputSuppressed(true);
	}*/
	
	
	/*
	 * This function insert the following code into the ast
	 * 1.[pathstr, name, ext, versn] = fileparts(xmlFileName);
	 * 2.A = exist([pathstr name '.xml'],'file');
	 * 	   
	 */
/*	private void insertCheckFileExistNode(int nodeNumber)
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
		//fEAStmt1.setRHS(eFactory.createParaExpr(filePartsNameExpr, fileNameExpr, 0));
		//fEAStmt1.setOutputSuppressed(true);
		
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
/*	private AssignStmt insertDocNode()
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
	/*private void insertDateTimeNode()
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
/*	private void insertIfNode(int index)
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
		//xDocAStmt.setRHS(eFactory.createParaExpr(xmlReadNExpr, fileNameExpr, 0));
		//xDocAStmt.setOutputSuppressed(true);
		
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
	/*private void insertElseBlock()
	{
		ElseBlock elseBlock=new ElseBlock();
		elseBlock.addStmt(insertFileOpenNode());
		elseBlock.addStmt(insertDocNode());
		System.out.println(elseBlock.dumpTreeAll());
		System.out.println(elseBlock.getPrettyPrinted());
	}
	
	/*
	 * This node inserts following code into the AST
	 * 1.Would create an element in the docNode
	 * e.g.elRunNo = docNode.createElement('RunNo');
	 * TODO:pass the attribute Name also if needs to set it.overload this method 
	 */
/*	private void insertCreateElementNode(String nodeName)
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
/*	private void insertAppendChildNode(NameExpr parentName,String Name)
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
/*	private void insertSetAttributeNode(NameExpr callerExpr,StringLiteralExpr attributeName,NameExpr attributeValue)
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
	 * This function inserts following node into the AST.It takes as input the data type of variable that needs to be printed.
	 * docNode.createTextNode(sprintf('%s', 'ii'))
	 * TODO:Needs to handle expr argument.Add arguments to textNodeList
	 */
/*	private ParameterizedExpr insertTextNode(Expr expr)
	{
		DotExpr textNodeDExpr=new DotExpr();
		Name createTextNode=eFactory.createName("createTextNode");
		ParameterizedExpr textNodePExpr;		
		NameExpr sprintfNExpr=eFactory.createNameExpr("sprintf");
		List textNodeList=new List();
			
		
	}
	
	/*
	 * This function traverses the program node and looks for ForLoop.
	 */
/*	private void traverseProgramNode()
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
	

